package com.example.sharepoint.client.ui;

import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.R;
import com.example.sharepoint.client.adapters.ListsAdapter;
import com.example.sharepoint.client.data.Item;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.CookieAuthenticator;
import com.example.sharepoint.client.network.tasks.ItemCreateTask;
import com.example.sharepoint.client.network.tasks.ItemRemoveTask;
import com.example.sharepoint.client.network.tasks.ListCreateTask;
import com.example.sharepoint.client.network.tasks.ListReadTask;
import com.example.sharepoint.client.network.tasks.ListRemoveTask;
import com.example.sharepoint.client.network.tasks.ListsReceiveTask;
import com.example.sharepoint.client.utils.Utility;
import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.BaseOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.auth.AbstractOfficeAuthenticator;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;
import com.microsoft.opentech.office.network.auth.SharePointCredentials;
import com.microsoft.opentech.office.network.lists.GetListsOperation;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataValue;

/**
 * Sample activity displaying request results.
 */
public class MainActivity extends Activity implements OnOperaionExecutionListener {

    /**
     * List with items retrieved from SP.
     */
    private ListView mList;

    private ListsAdapter mListsAdapter;

    private ListsAdapter mItemsAdapter;

    private String mSelectedListId;

    ISharePointCredentials mCreds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = (ListView) findViewById(R.id.available_lists);
        registerForContextMenu(mList);

        Configuration.setServerBaseUrl(Constants.SP_BASE_URL);
        Configuration.setAuthenticator(new CookieAuthenticator());

        new ListsReceiveTask(this, this).execute();
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
        Intent intent = new Intent(MainActivity.this, ViewListItemActivity.class);
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
            showErrorMessage(getString(R.string.main_list_create_failure));
        }
    }

    private void createListItem() {
        try {
            ODataEntity newItem = new ItemCreateTask(null, this).execute(mSelectedListId).get();
            if (newItem == null) {
                Utility.showToastNotification(getString(R.string.main_item_create_failure));
                return;
            }

            addEntity(newItem, mItemsAdapter);
        } catch (Exception e) {
            Utility.showAlertDialog(getString(R.string.main_item_create_failure), this);
            Logger.logApplicationException(e, getClass().getSimpleName() + ".addNewItem(): Error.");
        }
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

        if(isListsAdapter) {
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

    private void showErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onExecutionComplete(final BaseOperation operation, final boolean executionResult) {
        runOnUiThread(new Runnable() {
            public void run() {
                populateLists(operation, executionResult);
            }
        });
    }

    public void onAddButtonClick(View button) {
        if (mList.getAdapter() == mListsAdapter) {
            createList();
        } else {
            createListItem();
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

        //registerForContextMenu(mList);

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
                ((TextView) findViewById(R.id.pending_request_text_stub)).setText("Error");
                return;
            }

            mListsAdapter = fillAdapter(((GetListsOperation) operation).getResult(), true);

            showLists();

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
            ((TextView) findViewById(R.id.pending_request_text_stub)).setText("Error");
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
    }

    private void authWithOAuth() {
        // mCreds = new SharePointCredentials("f60c80ab-eafb-424b-a54b-853f67e43d3e", "1v3taiQI2nsFvccdJZVatjFKReLWcOkYaYum4+LfjkI=",
        //        Constants.SP_SITE_URL, "https://www.akvelon.com/company/about-akvelon", "www.akvelon.com");
        mCreds = new SharePointCredentials("60188dfc-3250-44c5-8434-8f106c9e529e", "Epn9cpyL7qxyCvPJgjNv6RNYZDAEq0vDefhJ+3hi16A=",
                Constants.SP_SITE_URL, "https://www.akvelon.com/company/about-akvelon", "www.akvelon.com");

        Configuration.setAuthenticator(new AbstractOfficeAuthenticator() {

            @Override
            protected ISharePointCredentials getCredentials() {
                return mCreds;
            }

            @Override
            protected Activity getActivity() {
                return MainActivity.this;
            }
        });
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
}