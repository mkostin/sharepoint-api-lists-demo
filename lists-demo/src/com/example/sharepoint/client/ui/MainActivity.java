package com.example.sharepoint.client.ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.impl.cookie.BasicClientCookie;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.R;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.CreateListTask;
import com.example.sharepoint.client.network.ListReadTask;
import com.example.sharepoint.client.network.ListsOperation;
import com.example.sharepoint.client.network.ListsReceiveTask;
import com.example.sharepoint.client.network.RemoveListTask;
import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.BaseOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.auth.AbstractCookieAuthenticator;
import com.microsoft.opentech.office.network.auth.AbstractOfficeAuthenticator;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;
import com.microsoft.opentech.office.network.auth.SharePointCredentials;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataValue;

/**
 * Sample activity displaying request results.
 */
public class MainActivity extends Activity implements OnOperaionExecutionListener {

    /**
     * Maps list names and guids
     */
    private HashMap<String, String> guids;

    private ArrayAdapter<String> listsAdapter;

    private ArrayAdapter<String> itemsAdapter;

    ISharePointCredentials mCreds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration.setServerBaseUrl(Constants.SP_BASE_URL);

        authWithCookie();
        //authWithOAuth();

        new ListsReceiveTask(this, this).execute();
    }

    private void authWithCookie() {
        Configuration.setAuthenticator(new AbstractCookieAuthenticator() {
            protected List<BasicClientCookie> getCookies() {
                ArrayList<BasicClientCookie> cookies = new ArrayList<BasicClientCookie>();

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 100);
                Date date = calendar.getTime();

                int idx = Constants.COOKIE_RT_FA.indexOf("=");
                BasicClientCookie rtFa = new BasicClientCookie(Constants.COOKIE_RT_FA.substring(0, idx), Constants.COOKIE_RT_FA.substring(idx + 1));
                rtFa.setExpiryDate(date);
                rtFa.setDomain(URI.create(Constants.SP_BASE_URL).getHost());
                rtFa.setPath("/");
                cookies.add(rtFa);

                idx = Constants.COOKIE_FED_AUTH.indexOf("=");
                BasicClientCookie fedAuth = new BasicClientCookie(Constants.COOKIE_FED_AUTH.substring(0, idx), Constants.COOKIE_FED_AUTH
                        .substring(idx + 1));
                fedAuth.setExpiryDate(date);
                fedAuth.setDomain(URI.create(Constants.SP_BASE_URL).getHost());
                fedAuth.setPath("/");
                cookies.add(fedAuth);

                return cookies;
            }
        });
    }

    private void authWithOAuth() {
        mCreds = new SharePointCredentials("f60c80ab-eafb-424b-a54b-853f67e43d3e", "1v3taiQI2nsFvccdJZVatjFKReLWcOkYaYum4+LfjkI=",
                Constants.SP_SITE_URL, "https://www.akvelon.com/company/about-akvelon", "akvelon.com");

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
            case R.id.remove_list:
                String title = (String) ((TextView) info.targetView).getText();
                removeList(title);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void viewList(String title) {
        try {

            ODataCollectionValue items = new ListReadTask(null, this).execute(guids.get(title)).get();

            if (items == null) {
                Toast.makeText(this, "Unable to get list items", Toast.LENGTH_LONG).show();
                return;
            }

            displayItemsToView(items);

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".viewList(): Error.");
            Toast.makeText(this, "Unable to get list items", Toast.LENGTH_LONG).show();
        }
    }

    public void removeList(String title) {
        try {
            boolean result = new RemoveListTask(null, this).execute(guids.get(title)).get();
            if (!result) {
                showErrorMessage("Unable to remove list");
                return;
            }

            ListView lists = (ListView) MainActivity.this.findViewById(R.id.available_lists);
            ArrayAdapter<String> newAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            for (int i = 0; i < listsAdapter.getCount(); ++i) {
                if (!listsAdapter.getItem(i).equals(title)) {
                    newAdapter.add(listsAdapter.getItem(i));
                }
            }

            lists.setAdapter(newAdapter);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".removeList(): Error.");
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onExecutionComplete(final BaseOperation operation, final boolean executionResult) {
        runOnUiThread(new Runnable() {
            public void run() {
                displayListsToView(operation, executionResult);
            }
        });
    }

    public void addNewList(View button) {
        try {
            ODataEntity newList = new CreateListTask(null, this).execute().get();
            if (newList == null) {
                showErrorMessage("Unable to create list");
                return;
            }

            String title = newList.getProperty("d").getComplexValue().get("Title").getPrimitiveValue().toString();
            guids.put(title, newList.getProperty("d").getComplexValue().get("Id").getPrimitiveValue().toString());

            listsAdapter.add(title);
            listsAdapter.notifyDataSetChanged();
            MainActivity.this.findViewById(R.id.available_lists).invalidate();

        } catch (Exception e) {
            showErrorMessage("Unable to create list");
        }
    }

    private void showErrorMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Displays lists to ListView
     *
     * @param operation
     * @param executionResult
     */
    private void displayListsToView(@SuppressWarnings("rawtypes") final BaseOperation operation, final boolean executionResult) {
        try {
            if (executionResult != true) {
                ((TextView) MainActivity.this.findViewById(R.id.pending_request_text_stub)).setText("Error");
                return;
            }

            listsAdapter = fillAdapter(((ListsOperation) operation).getResult());

            showLists();

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
            ((TextView) MainActivity.this.findViewById(R.id.pending_request_text_stub)).setText("Error");
        }
    }

    /**
     *
     */
    private void showLists() {
        ListView lists = (ListView) MainActivity.this.findViewById(R.id.available_lists);
        lists.setAdapter(listsAdapter);
        lists.setVisibility(View.VISIBLE);
        MainActivity.this.findViewById(R.id.add_list_button).setVisibility(View.VISIBLE);
        MainActivity.this.findViewById(R.id.pending_request_text_stub).setVisibility(View.GONE);
        registerForContextMenu(lists);

        lists.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String title = (String) ((TextView) view).getText();
                    viewList(title);
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".onItemClick(): Error.");
                }
            }
        });
    }

    private ArrayAdapter<String> fillAdapter(ODataCollectionValue items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        Iterator<ODataValue> iter = items.iterator();
        boolean fillGuids = guids == null;
        if (fillGuids) {
            guids = new HashMap<String, String>();
        }

        while (iter.hasNext()) {
            ODataComplexValue value = (ODataComplexValue) iter.next().asComplex();
            String title = value.get("Title").getPrimitiveValue().toString();
            if (fillGuids) {
                String guid = value.get("Id").getPrimitiveValue().toString();
                guids.put(title, guid);
            }
            adapter.add(title);
        }

        return adapter;
    }

    private void displayItemsToView(ODataCollectionValue items) {

        itemsAdapter = fillAdapter(items);

        ListView lists = (ListView) MainActivity.this.findViewById(R.id.available_lists);
        lists.setAdapter(itemsAdapter);
        unregisterForContextMenu(lists);
        lists.setOnItemClickListener(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (((ListView) findViewById(R.id.available_lists)).getAdapter() == itemsAdapter) {
                showLists();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}