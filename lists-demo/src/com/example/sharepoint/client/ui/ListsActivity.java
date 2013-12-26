package com.example.sharepoint.client.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

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
import com.example.sharepoint.client.event.AuthTypeChangedEvent;
import com.example.sharepoint.client.event.bus.EventBus;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.example.sharepoint.client.network.auth.CookieAuthenticator;
import com.example.sharepoint.client.network.auth.NTLMAuthenticator;
import com.example.sharepoint.client.network.auth.SharePointCredentials;
import com.example.sharepoint.client.preferences.AuthPreferences;
import com.example.sharepoint.client.utils.Utility;
import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.auth.AbstractOfficeAuthenticator;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;
import com.microsoft.opentech.office.network.files.CreateFileOperation;
import com.microsoft.opentech.office.network.lists.CreateListItemOperation;
import com.microsoft.opentech.office.network.lists.CreateListOperation;
import com.microsoft.opentech.office.network.lists.GetItemOperation;
import com.microsoft.opentech.office.network.lists.GetListItemsOperation;
import com.microsoft.opentech.office.network.lists.GetListsOperation;
import com.microsoft.opentech.office.network.lists.RemoveListItemOperation;
import com.microsoft.opentech.office.network.lists.RemoveListOperation;
import com.microsoft.opentech.office.network.lists.UpdateListItemOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.Entity.Builder;
import com.microsoft.opentech.office.odata.async.ICallback;
import com.squareup.otto.Subscribe;

/**
 * Sample activity displaying request results.
 */
public class ListsActivity extends Activity implements ICallback<String> {

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
     * POJO to hold data of list item.
     */
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        mList = (ListView) findViewById(R.id.available_lists);
        registerForContextMenu(mList);

        Configuration.setServerBaseUrl(Constants.SP_BASE_URL);
        authAndRefresh();
    }

    private void refreshLists() {
        if (mListsAdapter != null) {
            mListsAdapter.clear();
            mListsAdapter.notifyDataSetChanged();
            mList.invalidate();
        }

        setExecutionStatus(true, getString(R.string.pending_request));
        new GetListsOperation(null, this).executeAsync().setCallback(new ICallback<List<Object>>() {
            public void onError(Throwable error) {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        setExecutionStatus(false, getString(R.string.main_status_error));
                    }
                });
            }

            public void onDone(final List<Object> result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        populateLists(result);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        try {
            EventBus.getInstance().register(this);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onResume(): Failed.");
        } finally {
            super.onResume();
        }
    }

    @Override
    protected void onStop() {
        try {
            EventBus.getInstance().unregister(this);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onPause(): Failed.");
        } finally {
            super.onStop();
        }
    }

    @Subscribe
    public void onAuthTypeChanged(AuthTypeChangedEvent event) {
        authAndRefresh();
    }

    public void authAndRefresh() {
        SharePointCredentials creds = (SharePointCredentials) AuthPreferences.loadCredentials();

        // First timer
        if (creds == null) {
            creds = new SharePointCredentials("f60c80ab-eafb-424b-a54b-853f67e43d3e", "1v3taiQI2nsFvccdJZVatjFKReLWcOkYaYum4+LfjkI=",
                    Constants.SP_SITE_URL, "https://www.akvelon.com/company/about-akvelon", "www.akvelon.com");
            AuthPreferences.storeCredentials(creds);
        }

        AuthType authType = creds == null ? AuthType.UNDEFINED : creds.getAuthType();
        switch (authType) {
            case AD:
            case BASIC:
            case UNDEFINED:
            case COOKIE:
                Configuration.setAuthenticator(new CookieAuthenticator());
                refreshLists();
                break;
            case OAUTH:
                Configuration.setAuthenticator(new AbstractOfficeAuthenticator() {
                    @Override
                    protected ISharePointCredentials getCredentials() {
                        return AuthPreferences.loadCredentials();
                    }

                    @Override
                    protected Activity getActivity() {
                        return ListsActivity.this;
                    }
                });
                refreshLists();
                break;
            case NTLM:
                if (TextUtils.isEmpty(creds.getLogin()) || TextUtils.isEmpty(creds.getPassword())) {
                    showNTLMLoginDialog();
                }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (mList.getAdapter() == mListsAdapter) {
            getMenuInflater().inflate(R.menu.lists, menu);
        } else {
            getMenuInflater().inflate(R.menu.list_items, menu);
        }
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
            case R.id.change:
                updateItem(mSelectedListId, info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void updateItem(String listID, int adapterIndex) {
        try {
            String id = mItemsAdapter.getItem(adapterIndex).getId();

            new GetItemOperation(null, this, listID, Integer.valueOf(id)).executeAsync().setCallback(new ICallback<Entity>() {

                @Override
                public void onError(final Throwable error) {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Utility.showToastNotification("Unable to retrieve item: " + error.getMessage());
                        }
                    });
                }

                @Override
                public void onDone(final Entity result) {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            if(result != null) {
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
                    });
                }
            });
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".updateItem(): Error.");
        }
    }

    public void deleteList(String title, final int adapterIndex) {

        String id = mListsAdapter.getItem(adapterIndex).getId();
        new RemoveListOperation(null, this, id).executeAsync().setCallback(new ICallback<Boolean>() {
            public void onError(final Throwable error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utility.showToastNotification("Unable to remove list: " + error.getMessage());
                    }
                });
            }
            public void onDone(Boolean result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //TODO: check for result value
                        mListsAdapter.remove(adapterIndex);
                        mListsAdapter.notifyDataSetChanged();

                        mList.invalidate();
                    }
                });
            }
        });
    }

    private void deleteListItem(String listID, final int adapterIndex) {
        try {
            String id = mItemsAdapter.getItem(adapterIndex).getId();
            new RemoveListItemOperation(null, this, listID, Integer.valueOf(id)).executeAsync().setCallback(new ICallback<Boolean>() {
                    public void onError(Throwable error) {
                        runOnUiThread( new Runnable() {
                            @Override
                            public void run() {
                                Utility.showToastNotification("Unable to remove item");
                            }
                        });
                    }
                    public void onDone(final Boolean result) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                if(!result) {
                                    //TODO: check for result value
                                    mItemsAdapter.remove(adapterIndex);
                                    mItemsAdapter.notifyDataSetChanged();

                                    mList.invalidate();
                                } else {
                                    Utility.showToastNotification("Unable to remove item");
                                }
                            }
                        });
                    }
            });
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".removeItem(): Error.");
        }
    }

    public void readList(String title, int index) {
        mSelectedListId = mListsAdapter.getItem(index).getId();
        new GetListItemsOperation(null, this, mSelectedListId).executeAsync().setCallback(new ICallback<List<Object>>() {
            public void onError(final Throwable error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utility.showToastNotification("Unable to get list items: " + error.getMessage());
                    }
                });
            }
            public void onDone(final List<Object> result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        populateItems(result);
                    }
                });
            }
        });
    }

    private void readListItem(int position) {
        Intent intent = new Intent(ListsActivity.this, ListItemActivity.class);
        intent.putExtra("listGUID", mSelectedListId);
        int itemId = Integer.valueOf(mItemsAdapter.getItem(position).getId());
        intent.putExtra("itemId", itemId);
        startActivity(intent);
    }

    public void createList() {

        Builder builder = new Entity.Builder("SP.List").set("BaseTemplate", 100).
                set("Title", "List, created using API").set("Description", "SDK Playground");

        new CreateListOperation(null, this, builder).executeAsync().setCallback(new ICallback<Entity>() {
            public void onError(Throwable error) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utility.showToastNotification(getString(R.string.main_list_create_failure));
                    }
                });
            }
            public void onDone(final Entity result) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        addEntity(result, mListsAdapter);
                    }
                });
            }
        });
    }

    private void createListItem(final Item item) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    // Create new entity
                    Entity image = new Entity.Builder("SP.FieldUrlValue").set("Description", item.getTitle())
                            .set("Url", item.getImageUrl().toString()).build();
                    Builder builder = new Builder().set("Title", item.getTitle()).set("Image", image);

                    // Push it to the selected list
                    new CreateListItemOperation(null, ListsActivity.this, mSelectedListId, builder).executeAsync().setCallback( new ICallback<Entity>() {

                        @Override
                        public void onError(Throwable error) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Utility.showToastNotification(getString(R.string.main_item_create_failure));
                                }
                            });
                        }

                        @Override
                        public void onDone(final Entity result) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if(result != null) {
                                        addEntity(result, mItemsAdapter);
                                    } else {
                                        Utility.showToastNotification(getString(R.string.main_item_create_failure));
                                    }
                                }
                            });
                        }
                    });
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
    private void addEntity(Entity entity, ListsAdapter adapter) {
        String title = entity.get("Title").toString();
        String id = entity.get("Id").toString();

        adapter.add(new Item(id, title));
        adapter.notifyDataSetChanged();

        mList.invalidate();
    }

    private ListsAdapter fillAdapter(List<Object> items, boolean isListsAdapter) {
        ListsAdapter adapter = new ListsAdapter(this, R.layout.list_item, null);

        if (isListsAdapter) {
            mListsAdapter = adapter;
        } else {
            mItemsAdapter = adapter;
        }

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
            if (isListsAdapter) {
                mListsAdapter.add(new Item(id, title));
            } else {
                mItemsAdapter.add(new Item(id, title));
            }
        }

        return adapter;
    }

    @Override
    public void onDone(final String result) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
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
        });
    }

    @Override
    public void onError(final Throwable error) {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                Utility.showToastNotification("File upload failed: " + error.getMessage());
            }
        });
    }

    public void onAddButtonClick(View button) {
        if (mList.getAdapter() == mListsAdapter) {
            createList();
        } else {
            mItem = new Item();
            showItemDialog(getString(R.string.main_item_image_title_default), getString(R.string.main_item_image_url_default), true);
        }
    }

    /**
     * Show data from lists adapter.
     */
    private void showLists() {
        mList.setAdapter(mListsAdapter);

        mList.setVisibility(View.VISIBLE);
        findViewById(R.id.add_list_button).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.list_label)).setText(R.string.main_list_label_lists);

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
    private void populateLists(List<Object> lists) {
        try {
            setExecutionStatus(true, null);
            mListsAdapter = fillAdapter(lists, true);
            showLists();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".populateLists(): Error.");
            setExecutionStatus(false, getString(R.string.main_status_error) + getString(R.string.main_status_error_retrieve_lists));
        }
    }

    private void setExecutionStatus(boolean visible, String message) {
        TextView status = (TextView) findViewById(R.id.pending_request_text_stub);
        if (visible) {
            status.setVisibility(View.GONE);
        } else {
            status.setVisibility(View.VISIBLE);
            status.setText(TextUtils.isEmpty(message) ? "" : message);
        }
    }

    private void populateItems(List<Object> items) {
        mItemsAdapter = fillAdapter(items, false);

        mList.setAdapter(mItemsAdapter);
        mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                readListItem(position);
            }
        });

        ((Button) findViewById(R.id.add_list_button)).setText(getString(R.string.main_item_create));
        ((TextView) findViewById(R.id.list_label)).setText(R.string.main_list_label_items);
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
     * Shows NTLM authentication dialog.
     */
    public void showNTLMLoginDialog() {
        try {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.auth_ntlm_dialog);
            dialog.setTitle(getString(R.string.main_login_dialog_login));
            dialog.setCanceledOnTouchOutside(false);

            TextView cancelButton = (TextView) dialog.findViewById(R.id.main_login_dialog_cancel_button);
            TextView loginButton = (TextView) dialog.findViewById(R.id.main_login_dialog_login_button);

            final SharePointCredentials creds = (SharePointCredentials) AuthPreferences.loadCredentials();
            String login = null, pass = null;
            if (creds != null) {
                login = creds.getLogin();
                pass = creds.getPassword();
            }

            EditText editText = (EditText) dialog.findViewById(R.id.main_login_dialog_login);
            editText.setText(TextUtils.isEmpty(login) ? "" : login);
            editText = (EditText) dialog.findViewById(R.id.main_login_dialog_password);
            editText.setText(TextUtils.isEmpty(pass) ? "" : pass);

            OnClickListener listener = new OnClickListener() {
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.main_login_dialog_cancel_button: {
                            dialog.dismiss();
                            break;
                        }
                        case R.id.main_login_dialog_login_button: {
                            String login = ((EditText) dialog.findViewById(R.id.main_login_dialog_login)).getText().toString();
                            String pass = ((EditText) dialog.findViewById(R.id.main_login_dialog_password)).getText().toString();

                            if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(pass)) {
                                creds.setPassword(pass);
                                creds.setLogin(login);

                                AuthPreferences.storeCredentials(creds);

                                refreshLists();
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
            loginButton.setOnClickListener(listener);

            dialog.show();
        } catch (final Exception e) {
            Utility.showAlertDialog(ListsActivity.class.getSimpleName() + ".showCreateItemDialog(): Failed. " + e.toString(), ListsActivity.this);
        }
    }

    /**
     * Shows dialog enabling user to add item to current list.
     */
    public void showItemDialog(String name, String url, final boolean create) {
        try {
            final Dialog dialog = mCreateItemDialog = new Dialog(this);
            dialog.setContentView(R.layout.create_item_dialog);
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
            Utility.showAlertDialog(ListsActivity.class.getSimpleName() + ".showCreateItemDialog(): Failed. " + e.toString(), ListsActivity.this);
        }
    }

    private void updateListItem(final Item item) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Entity image = new Entity.Builder("SP.FieldUrlValue").set("Description", item.getTitle())
                            .set("Url", item.getImageUrl().toString()).build();
                    Builder builder = new Builder().set("Title", item.getTitle()).set("Image", image);

                    new UpdateListItemOperation(null, ListsActivity.this, mSelectedListId, Integer.valueOf(item.getId()), builder).executeAsync()
                            .setCallback(new ICallback<Boolean>() {
                                public void onError(Throwable error) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Utility.showToastNotification("Failure on update");
                                        }
                                    });
                                }

                                @Override
                                public void onDone(final Boolean result) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Utility.showToastNotification(result ? "Updated successfully" : "Failure on update");
                                        }
                                    });

                                }
                            });
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".updateListItem(): Error.");
                }
            }
        });
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

                    //TODO: Fix constructor arguments usage
                    new CreateFileOperation(null, this, IMAGE_LIB_NAME, fileName, image).executeAsync().setCallback(this);
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".onActivityResult(): Error.");
                }
            }
        }
    }
}