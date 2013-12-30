package com.example.office.lists.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.office.R;
import com.example.office.lists.adapters.ListsAdapter;
import com.example.office.lists.data.Item;
import com.example.office.logger.Logger;
import com.example.office.ui.ListFragment;
import com.example.office.utils.Utility;
import com.microsoft.opentech.office.network.files.CreateFileOperation;
import com.microsoft.opentech.office.network.lists.CreateListItemOperation;
import com.microsoft.opentech.office.network.lists.GetItemOperation;
import com.microsoft.opentech.office.network.lists.GetListItemsOperation;
import com.microsoft.opentech.office.network.lists.RemoveListItemOperation;
import com.microsoft.opentech.office.network.lists.UpdateListItemOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.Entity.Builder;
import com.microsoft.opentech.office.odata.async.ICallback;

/**
 * Inbox fragment containing logic related to managing inbox emails.
 */
public class ListItemsFragment extends ListFragment<Item, ListsAdapter> implements ICallback<String> {

    /**
     * Request code for the operation of image retrieval either from gallery or from camera.
     */
    private static int PICTURE_REQUEST_CODE = 42;

    /**
     * Temporary dir that will be used/created on the sd card to store images received from camera/gallery.
     */
    private static String IMAGE_TMP_DIR = "TmpDir";

    /**
     * SharePoint library that will be used to store image files on the server side.
     */
    private static String IMAGE_LIB_NAME = "Shared%20Documents";

    /**
     * List with items retrieved from SP.
     */
    private ListView mList;

    /**
     * Curently selected list id.
     */
    private String mSelectedListId;

    /**
     * Adapter index of currently updating item.
     */
    private int mUpdatingItemAdapterIndex;

    /**
     * Uri of the image faile attached to the list item during creation.
     */
    private Uri mOutputFileUri;

    /**
     * Instance of the dialog used to create list item.
     */
    private Dialog mCreateItemDialog;

    /**
     * POJO to hold data of list item.
     */
    private Item mItem;

    /**
     * Constructor.
     */
    public ListItemsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        Bundle args = getArguments();
        if(args != null) {
            mSelectedListId = args.getString("listID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        try {
            Bundle args = getArguments();
            if(args != null) {
                mSelectedListId = args.getString("listID");
            }

            mList = (ListView) rootView.findViewById(R.id.available_lists);
            registerForContextMenu(mList);

            final Button add = (Button) rootView.findViewById(R.id.add_list_button);
            add.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    onAddButtonClick(add);
                }
            });

            initList();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreateView(): Error.");
        }

        return rootView;
    }

    @Override
    protected void initList() {
        ListsAdapter adapter = (ListsAdapter) getListAdapterInstance();
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            mList.invalidate();
        }

        showWorkInProgress(true, true);

        new GetListItemsOperation(new ICallback<List<Object>>() {
            public void onError(final Throwable error) {
                Utility.showToastNotification("Unable to get list items: " + error.getMessage());
                showWorkInProgress(false, false);
            }

            public void onDone(final List<Object> result) {
                populateItems(result);
            }
        }, getActivity(), mSelectedListId).executeAsync();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.list_items, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.list_item_remove:
                deleteListItem(mSelectedListId, info.position);
                return true;
            case R.id.list_item_change:
                updateItem(mSelectedListId, info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateItem(String listID, int adapterIndex) {
        try {
            mUpdatingItemAdapterIndex = adapterIndex;
            String id = getListAdapterInstance().getItem(adapterIndex).getId();

            showWorkInProgress(true, false);

            new GetItemOperation(new ICallback<Entity>() {

                @Override
                public void onError(final Throwable error) {
                    Utility.showToastNotification("Unable to retrieve item: " + error.getMessage());
                    showWorkInProgress(false, false);
                }

                @Override
                public void onDone(final Entity result) {
                    showWorkInProgress(false, false);
                    if (result != null) {
                        String url = "";
                        if (result.get("Image") != null) {
                            url = ((Entity) result.get("Image")).get("Url").toString();
                        }
                        mItem = new Item(result.get("Id").toString(), result.get("Title").toString());
                        showItemDialog(result.get("Title").toString(), url, false);
                    } else {
                        Utility.showToastNotification("Unable to retrieve item");
                    }
                }
            }, getActivity(), listID, Integer.valueOf(id)).executeAsync();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".updateItem(): Error.");
        }
    }

    private void deleteListItem(String listID, final int adapterIndex) {
        try {
            String id = getListAdapterInstance().getItem(adapterIndex).getId();

            showWorkInProgress(true, false);

            new RemoveListItemOperation(new ICallback<Boolean>() {
                public void onError(Throwable error) {
                    Utility.showToastNotification("Unable to remove item");
                    showWorkInProgress(false, false);
                }

                public void onDone(final Boolean result) {
                    showWorkInProgress(false, false);
                    if (result) {
                        // TODO: check for result value
                        getListAdapterInstance().remove(adapterIndex);
                        getListAdapterInstance().notifyDataSetChanged();

                        mList.invalidate();
                    } else {
                        Utility.showToastNotification("Unable to remove item");
                    }
                }
            }, getActivity(), listID, Integer.valueOf(id)).executeAsync();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".removeItem(): Error.");
        }
    }

    private void readListItem(int position) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ListItemActivity.class);
        intent.putExtra("listGUID", mSelectedListId);
        int itemId = Integer.valueOf(getListAdapterInstance().getItem(position).getId());
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    private void createListItem(final Item item) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    // Create new entity
                    Entity image = new Entity.Builder("SP.FieldUrlValue").set("Description", item.getTitle())
                            .set("Url", item.getImageUrl().toString()).build();
                    Builder builder = new Builder().set("Title", item.getTitle()).set("Image", image);

                    showWorkInProgress(true, false);

                    // Push it to the selected list
                    new CreateListItemOperation(new ICallback<Entity>() {
                        @Override
                        public void onError(Throwable error) {
                            Utility.showToastNotification(getString(R.string.main_item_create_failure));
                            showWorkInProgress(false, false);
                        }

                        @Override
                        public void onDone(final Entity result) {
                            showWorkInProgress(false, false);
                            if (result != null) {
                                String title = result.get("Title").toString();
                                String id = result.get("Id").toString();

                                ListsAdapter adapter = getListAdapterInstance();
                                adapter.add(new Item(id, title));
                                adapter.notifyDataSetChanged();

                                mList.invalidate();
                            } else {
                                Utility.showToastNotification(getString(R.string.main_item_create_failure));
                            }
                        }
                    }, ListItemsFragment.this.getActivity(), mSelectedListId, builder).executeAsync();
                } catch (Exception e) {
                    Utility.showAlertDialog(getString(R.string.main_item_create_failure), getActivity());
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".addNewItem(): Error.");
                }
            }
        });

    }

    @Override
    public void onDone(final String result) {
        showWorkInProgress(false, false);

        // Getting filename from URI
        URI uri = URI.create(result);
        String[] splittedPath = uri.getPath().split("\\/");
        String filename = splittedPath[splittedPath.length - 1];

        if (mItem == null) {
            mItem = new Item().setTitle(filename);
        }
        mItem.setImageUrl(uri);

        // Updating dialog UI
        TextView attachImageButton = (TextView) mCreateItemDialog.findViewById(R.id.dialog_create_item_attach_image);
        attachImageButton.setText(R.string.main_item_create_dialog_attached_image);
        attachImageButton.setBackgroundColor(Color.GREEN);
        attachImageButton.setClickable(false);

        TextView imageUrlButton = (TextView) mCreateItemDialog.findViewById(R.id.dialog_create_item_url);
        imageUrlButton.setText(uri.toString());
        imageUrlButton.setBackgroundColor(Color.GREEN);
        imageUrlButton.setClickable(false);
    }

    @Override
    public void onError(final Throwable error) {
        Utility.showToastNotification("File upload failed: " + error.getMessage());
        showWorkInProgress(false, false);
    }

    public void onAddButtonClick(View button) {
        mItem = new Item();
        showItemDialog(getString(R.string.main_item_image_title_default), getString(R.string.main_item_image_url_default), true);
    }

    private void populateItems(List<Object> items) {
        List<Item> data = new ArrayList<Item>(items.size());
        Iterator<Object> iter = items.iterator();
        while (iter.hasNext()) {
            Entity entity = (Entity) iter.next();
            String title;
            if (entity.get("Title") != null) {
                title = (String) entity.get("Title");
            } else {
                title = "(id) " + entity.get("Id");
            }

            String id = entity.get("Id").toString();
            data.add(new Item(id, title));
        }
        getListAdapterInstance().update(data);

        showWorkInProgress(false, false);

        mList.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.add_list_button).setVisibility(View.VISIBLE);
        ((Button) getView().findViewById(R.id.add_list_button)).setText(getString(R.string.main_item_create));

        mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                readListItem(position);
            }
        });
    }

    /**
     * Shows dialog enabling user to add item to current list.
     */
    public void showItemDialog(String name, String url, final boolean create) {
        try {
            final Dialog dialog = mCreateItemDialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.lists_create_item_dialog);
            dialog.setTitle(getString(R.string.main_item_create_dialog_title));
            dialog.setCanceledOnTouchOutside(false);

            TextView cancelButton = (TextView) dialog.findViewById(R.id.dialog_create_item_cancel);
            TextView saveButton = (TextView) dialog.findViewById(R.id.dialog_create_item_ok);
            TextView attachImageButton = (TextView) dialog.findViewById(R.id.dialog_create_item_attach_image);

            EditText editText = (EditText) dialog.findViewById(R.id.dialog_create_item_name);
            editText.setText(name);
            editText = (EditText) dialog.findViewById(R.id.dialog_create_item_url);
            editText.setText(url);

            OnClickListener listener = new OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dialog_create_item_attach_image: {
                            Utility.openImageIntent(mOutputFileUri, IMAGE_TMP_DIR, ListItemsFragment.this.getActivity(), PICTURE_REQUEST_CODE);
                            break;
                        }
                        case R.id.dialog_create_item_cancel: {
                            dialog.dismiss();
                            break;
                        }
                        case R.id.dialog_create_item_ok: {
                            String name = ((EditText) dialog.findViewById(R.id.dialog_create_item_name)).getText().toString();
                            String address = ((EditText) dialog.findViewById(R.id.dialog_create_item_url)).getText().toString();

                            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(address)) {
                                mItem.setTitle(name);

                                // Set Url from the dialog field if image hasn't been attached.
                                if (mItem.getImageUrl() == null || TextUtils.isEmpty(mItem.getImageUrl().toString())) {
                                    mItem.setImageUrl(URI.create(address));
                                }
                                if (create) {
                                    createListItem(mItem);
                                } else {
                                    updateListItem(mItem);
                                }
                            } else {
                                Utility.showToastNotification("Make shure all fields are filled.");
                            }

                            dialog.dismiss();
                            break;
                        }
                    }
                }
            };

            cancelButton.setOnClickListener(listener);
            saveButton.setOnClickListener(listener);
            attachImageButton.setOnClickListener(listener);

            dialog.show();
        } catch (final Exception e) {
            Utility.showAlertDialog(ListItemsFragment.class.getSimpleName() + ".showCreateItemDialog(): Failed. " + e.toString(),
                    ListItemsFragment.this.getActivity());
        }
    }

    private void updateListItem(final Item item) {
        showWorkInProgress(true, false);
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Entity image = new Entity.Builder("SP.FieldUrlValue").set("Description", item.getTitle())
                            .set("Url", item.getImageUrl().toString()).build();
                    Builder builder = new Builder().set("Title", item.getTitle()).set("Image", image);

                    new UpdateListItemOperation(new ICallback<Boolean>() {
                        public void onError(Throwable error) {
                            Utility.showToastNotification("Failure on update");
                            showWorkInProgress(false, false);
                        }

                        @Override
                        public void onDone(final Boolean result) {
                            showWorkInProgress(false, false);
                            Utility.showToastNotification(result ? "Updated successfully" : "Failure on update");
                            ListsAdapter adapter = getListAdapterInstance();
                            adapter.getItem(mUpdatingItemAdapterIndex).setId(item.getId());
                            adapter.getItem(mUpdatingItemAdapterIndex).setImageUrl(item.getImageUrl());
                            adapter.getItem(mUpdatingItemAdapterIndex).setTitle(item.getTitle());
                            adapter.notifyDataSetChanged();
                        }
                    }, ListItemsFragment.this.getActivity(), mSelectedListId, Integer.valueOf(item.getId()), builder).executeAsync();
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".updateListItem(): Error.");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = mOutputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                // Upload a file
                try {
                    byte[] image = "stub".getBytes();
                    String fileName = "image.jpg";

                    if (!isCamera) {
                        File imageFile = new File(Utility.getRealPathFromURI(selectedImageUri, getActivity()));
                        fileName = imageFile.getName();
                        InputStream imageStream = new FileInputStream(imageFile);
                        image = IOUtils.toByteArray(imageStream);
                        if (imageStream != null) {
                            imageStream.close();
                        }
                    }

                    showWorkInProgress(true, false);

                    new CreateFileOperation(this, getActivity(), IMAGE_LIB_NAME, fileName, image).executeAsync();
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".onActivityResult(): Error.");
                }
            }
        }
    }

    @Override
    protected List<Item> getListData() {
        return null;
    }

    @Override
    protected int getListItemLayoutId() {
        return R.layout.lists_list_item;
    }

    @Override
    protected int getListViewId() {
        return R.id.available_lists;
    }

    @Override
    protected int getProgressViewId() {
        return R.id.lists_progress;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.lists_fragment;
    }

    @Override
    protected int getContentContainerId() {
        return R.id.lists_content_holder;
    }

    @Override
    protected View getListFooterViewInstance() {
        return null;
    }

    @Override
    protected ListsAdapter getListAdapterInstance(List<Item> data) {
        try {
            if (mAdapter == null) {
                mAdapter = new ListsAdapter(getActivity(), getListItemLayoutId(), data != null ? data : getListData());
            }
            return mAdapter;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getListAdapterInstance(List): Error.");
        }
        return null;
    }

}
