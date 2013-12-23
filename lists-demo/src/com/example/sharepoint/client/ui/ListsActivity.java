package com.example.sharepoint.client.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.R;
import com.example.sharepoint.client.adapters.ListsAdapter;
import com.example.sharepoint.client.data.Item;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.example.sharepoint.client.network.auth.CookieAuthenticator;
import com.example.sharepoint.client.network.auth.NTLMAuthenticator;
import com.example.sharepoint.client.network.auth.SharePointCredentials;
import com.example.sharepoint.client.network.tasks.FileCreateTask;
import com.example.sharepoint.client.network.tasks.ItemCreateTask;
import com.example.sharepoint.client.network.tasks.ItemRemoveTask;
import com.example.sharepoint.client.network.tasks.ListCreateTask;
import com.example.sharepoint.client.network.tasks.ListReadTask;
import com.example.sharepoint.client.network.tasks.ListRemoveTask;
import com.example.sharepoint.client.network.tasks.ListsReceiveTask;
import com.example.sharepoint.client.preferences.AuthPreferences;
import com.example.sharepoint.client.utils.Utility;
import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.BaseOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.auth.AbstractOfficeAuthenticator;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;
import com.microsoft.opentech.office.network.files.CreateFileOperation;
import com.microsoft.opentech.office.network.lists.GetListsOperation;
import com.microsoft.opentech.office.odata.ComplexValue;
import com.microsoft.opentech.office.odata.EntityBuilder;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataValue;

/**
 * Sample activity displaying request results.
 */
public class ListsActivity extends Activity implements OnOperaionExecutionListener {

    /**
     * Reauest code for the operation of image retrieval either from gallery or from camera.
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
     * Adapter to hold lists.
     */
    private ListsAdapter mListsAdapter;

    /**
     * Adapter to hold list items.
     */
    private ListsAdapter mItemsAdapter;

    /**
     * Curently selected list id.
     */
    private String mSelectedListId;

    /**
     * Uri of the image faile attached to the list item during creation.
     */
    private Uri mOutputFileUri;

    /**
     * Instance of the dialog used to create list item.
     */
    private Dialog mCreateItemDialog;

    /**
     * POJO to hold data of newly created list item.
     */
    private Item mNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        mList = (ListView) findViewById(R.id.available_lists);
        registerForContextMenu(mList);

        Configuration.setServerBaseUrl(Constants.SP_BASE_URL);
        authSetUp();

        new ListsReceiveTask(this, this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        authSetUp();
    }

    private void authSetUp() {
        SharePointCredentials creds = (SharePointCredentials) AuthPreferences.loadCredentials(this);
        AuthType authType = creds == null ? AuthType.UNDEFINED : creds.getAuthType();
        switch (authType) {
            case AD:
            case BASIC:
            case UNDEFINED:
            case COOKIE:
                Configuration.setAuthenticator(new CookieAuthenticator());
                break;
            case OAUTH:
                // mCreds = new SharePointCredentials("f60c80ab-eafb-424b-a54b-853f67e43d3e", "1v3taiQI2nsFvccdJZVatjFKReLWcOkYaYum4+LfjkI=",
                // Constants.SP_SITE_URL, "https://www.akvelon.com/company/about-akvelon", "www.akvelon.com");

                creds = new SharePointCredentials("60188dfc-3250-44c5-8434-8f106c9e529e", "Epn9cpyL7qxyCvPJgjNv6RNYZDAEq0vDefhJ+3hi16A=",
                        Constants.SP_SITE_URL, "https://www.akvelon.com/company/about-akvelon", "www.akvelon.com");
                creds.setAuthType(AuthType.OAUTH);

                AuthPreferences.storeCredentials(creds, this);

                Configuration.setAuthenticator(new AbstractOfficeAuthenticator() {
                    @Override
                    protected ISharePointCredentials getCredentials() {
                        return AuthPreferences.loadCredentials(ListsActivity.this);
                    }

                    @Override
                    protected Activity getActivity() {
                        return ListsActivity.this;
                    }
                });
                break;
            case NTLM:
                Configuration.setAuthenticator(new NTLMAuthenticator());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.lists, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.remove:
                if (mList.getAdapter() == mListsAdapter) {
                    String title = mListsAdapter.getItem(info.position).getTitle();
                    deleteList(title, info.position);
                } else {
                    deleteListItem(mSelectedListId, info.position);
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteList(String title, int adapterIndex) {
        try {
            String id = mListsAdapter.getItem(adapterIndex).getId();
            boolean result = new ListRemoveTask(null, this).execute(id).get();
            if (!result) {
                Utility.showToastNotification("Unable to remove list");
                return;
            }

            mListsAdapter.remove(adapterIndex);
            mListsAdapter.notifyDataSetChanged();

            mList.invalidate();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".removeList(): Error.");
        }
    }

    private void deleteListItem(String listID, int adapterIndex) {
        try {
            String id = mItemsAdapter.getItem(adapterIndex).getId();
            boolean result = new ItemRemoveTask(null, this).execute(listID, id).get();
            if (!result) {
                Utility.showToastNotification("Unable to remove item");
                return;
            }

            mItemsAdapter.remove(adapterIndex);
            mItemsAdapter.notifyDataSetChanged();

            mList.invalidate();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".removeItem(): Error.");
        }
    }

    public void readList(String title, int index) {
        try {
            mSelectedListId = mListsAdapter.getItem(index).getId();
            ODataCollectionValue items = new ListReadTask(null, this).execute(mSelectedListId).get();

            if (items == null) {
                Utility.showToastNotification("Unable to get list items");;
                return;
            }

            populateItems(items);

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".viewList(): Error.");
            Utility.showToastNotification("Unable to get list items");
        }
    }

    private void readListItem(int position) {
        Intent intent = new Intent(ListsActivity.this, ListItemActivity.class);
        intent.putExtra("listGUID", mSelectedListId);
        int itemId = Integer.valueOf(mItemsAdapter.getItem(position).getId());
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void createList() {
        try {
            ODataEntity newList = new ListCreateTask(null, this).execute().get();
            if (newList == null) {
                Utility.showAlertDialog(getString(R.string.main_list_create_failure), this);
                return;
            }

            addEntity(newList, mListsAdapter);
        } catch (Exception e) {
            Utility.showToastNotification(getString(R.string.main_list_create_failure));
        }
    }

    private void createListItem(final Item item) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    // Create new entity
                    ComplexValue imageMeta = new ComplexValue().set("type", "SP.FieldUrlValue");
                    ComplexValue image = new ComplexValue()
                        .set("__metadata", imageMeta)
                        .set("Description", item.getTitle())
                        .set("Url", item.getImageUrl().toString());
                    EntityBuilder builder = EntityBuilder.newEntity(null).set("Title", item.getTitle()).set("Image", image);

                    // Push it to the selected list
                    ODataEntity newItem = new ItemCreateTask(null, ListsActivity.this).execute(mSelectedListId, builder).get();
                    if (newItem == null) {
                        Utility.showToastNotification(getString(R.string.main_item_create_failure));
                        return;
                    }

                    addEntity(newItem, mItemsAdapter);
                } catch (Exception e) {
                    Utility.showAlertDialog(getString(R.string.main_item_create_failure), ListsActivity.this);
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".addNewItem(): Error.");
                }
            }
        });

    }

    /**
     * Adds entity to provided adapter.
     *
     * @param entity Item to add.
     * @param adapter Adapter to insert item to.
     */
    private void addEntity(ODataEntity entity, ListsAdapter adapter) {
        String title = entity.getProperty("d").getComplexValue().get("Title").getPrimitiveValue().toString();
        String id = entity.getProperty("d").getComplexValue().get("Id").getPrimitiveValue().toString();

        adapter.add(new Item(id, title));
        adapter.notifyDataSetChanged();

        mList.invalidate();
    }

    private ListsAdapter fillAdapter(ODataCollectionValue items, boolean isListsAdapter) {
        ListsAdapter adapter = new ListsAdapter(this, R.layout.list_item, null);

        if (isListsAdapter) {
            mListsAdapter = adapter;
        } else {
            mItemsAdapter = adapter;
        }

        Iterator<ODataValue> iter = items.iterator();

        while (iter.hasNext()) {
            ODataComplexValue value = (ODataComplexValue) iter.next().asComplex();
            String title;

            if (!value.get("Title").hasNullValue()) {
                title = value.get("Title").getPrimitiveValue().toString();
            } else {
                title = "(id) " + value.get("Id").getPrimitiveValue().toCastValue();
            }

            String id = value.get("Id").getPrimitiveValue().toString();
            if (isListsAdapter) {
                mListsAdapter.add(new Item(id, title));
            } else {
                mItemsAdapter.add(new Item(id, title));
            }
        }

        return adapter;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onExecutionComplete(final BaseOperation operation, final boolean executionResult) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(operation instanceof CreateFileOperation){
                    if (executionResult) {
                        // Getting filename from URI
                        URI uri = URI.create((String) operation.getResult());
                        String[] splittedPath = uri.getPath().split("\\/");
                        String filename = splittedPath[splittedPath.length - 1];

                        if(mNewItem == null) {
                            mNewItem = new Item().setTitle(filename);
                        }
                        mNewItem.setImageUrl(uri);

                        // Updating dialog UI
                        TextView attachImageButton = (TextView) mCreateItemDialog.findViewById(R.id.dialog_create_item_attach_image);
                        attachImageButton.setText(R.string.main_item_create_dialog_attached_image);
                        attachImageButton.setBackgroundColor(Color.GREEN);
                        attachImageButton.setClickable(false);

                        TextView imageUrlButton = (TextView) mCreateItemDialog.findViewById(R.id.dialog_create_item_url);
                        imageUrlButton.setText(uri.toString());
                        imageUrlButton.setBackgroundColor(Color.GREEN);
                        imageUrlButton.setClickable(false);
                    } else {
                        Utility.showToastNotification("File upload failed");
                    }
                } else if(operation instanceof GetListsOperation) {
                    populateLists(operation, executionResult);
                }
            }
        });
    }

    public void onAddButtonClick(View button) {
        if (mList.getAdapter() == mListsAdapter) {
            createList();
        } else {
            showCreateItemDialog();
        }
    }

    /**
     *
     */
    private void showLists() {
        mList.setAdapter(mListsAdapter);

        mList.setVisibility(View.VISIBLE);
        findViewById(R.id.add_list_button).setVisibility(View.VISIBLE);
        findViewById(R.id.pending_request_text_stub).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.list_label)).setText(R.string.main_list_label_lists);


        ((Button) findViewById(R.id.add_list_button)).setText(getString(R.string.main_list_create));

        mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String title = mListsAdapter.getItem(position).getTitle();
                    readList(title, position);
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".onItemClick(): Error.");
                }
            }
        });
    }

    /**
     * Fills current lists list with an update from server.
     *
     * @param operation Server operation
     * @param executionResult execution result flag.
     */
    private void populateLists(@SuppressWarnings("rawtypes") final BaseOperation operation, final boolean executionResult) {
        try {
            if (executionResult != true) {
                ((TextView) findViewById(R.id.pending_request_text_stub)).setText(R.string.main_status_error);
                return;
            }

            mListsAdapter = fillAdapter(((GetListsOperation) operation).getResult(), true);

            showLists();

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
            ((TextView) findViewById(R.id.pending_request_text_stub)).setText(R.string.main_status_error + R.string.main_status_error_retrieve_lists);
        }
    }

    private void populateItems(ODataCollectionValue items) {
        mItemsAdapter = fillAdapter(items, false);

        mList.setAdapter(mItemsAdapter);
        mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                readListItem(position);
            }
        });

        ((Button) findViewById(R.id.add_list_button)).setText(getString(R.string.main_item_create));
        ((TextView)findViewById(R.id.list_label)).setText(R.string.main_list_label_items);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ListAdapter adapter = mList.getAdapter();
            if (adapter == mItemsAdapter) {
                showLists();
            }
            if (adapter == mListsAdapter) {
                return super.onKeyDown(keyCode, event);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Shows dialog enabling user to add item to current list.
     */
    public void showCreateItemDialog() {
        try {
            final Dialog dialog = mCreateItemDialog = new Dialog(this);
            dialog.setContentView(R.layout.create_item_dialog);
            dialog.setTitle(getString(R.string.main_item_create_dialog_title));
            dialog.setCanceledOnTouchOutside(false);

            TextView cancelButton = (TextView) dialog.findViewById(R.id.dialog_create_item_cancel);
            TextView saveButton = (TextView) dialog.findViewById(R.id.dialog_create_item_ok);
            TextView attachImageButton = (TextView) dialog.findViewById(R.id.dialog_create_item_attach_image);

            OnClickListener listener = new OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.dialog_create_item_attach_image: {
                            Utility.openImageIntent(mOutputFileUri, IMAGE_TMP_DIR, ListsActivity.this, PICTURE_REQUEST_CODE);
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
                                mNewItem = new Item().setTitle(name);

                                // Set Url from the dialog field if image hasn't been attached.
                                if(mNewItem.getImageUrl() == null || TextUtils.isEmpty(mNewItem.getImageUrl().toString())) {
                                    mNewItem.setImageUrl(URI.create(address));
                                }
                                createListItem(mNewItem);
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
            Utility.showAlertDialog(ListsActivity.class.getSimpleName() + ".showCreateItemDialog(): Failed. " + e.toString(), ListsActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
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
                        File imageFile = new File(Utility.getRealPathFromURI(selectedImageUri, this));
                        fileName = imageFile.getName();
                        InputStream imageStream = new FileInputStream(imageFile);
                        image = IOUtils.toByteArray(imageStream);
                        if (imageStream != null) {
                            imageStream.close();
                        }
                    }

                    String result = new FileCreateTask(this, this).execute(IMAGE_LIB_NAME, fileName, image).get();
                    if (result == null) {
                        Utility.showToastNotification("File upload failed");
                    }

                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".onActivityResult(): Error.");
                }
            }
        }
    }
}