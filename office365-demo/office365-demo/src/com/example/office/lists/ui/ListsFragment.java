package com.example.office.lists.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.example.office.Constants;
import com.example.office.R;
import com.example.office.events.bus.EventBus;
import com.example.office.lists.adapters.ListsAdapter;
import com.example.office.lists.auth.AuthType;
import com.example.office.lists.auth.CookieAuthenticator;
import com.example.office.lists.auth.NTLMAuthenticator;
import com.example.office.lists.auth.SharePointCredentials;
import com.example.office.lists.data.Item;
import com.example.office.lists.events.AuthTypeChangedEvent;
import com.example.office.lists.storage.AuthPreferences;
import com.example.office.logger.Logger;
import com.example.office.ui.BaseActivity;
import com.example.office.ui.IFragmentNavigator;
import com.example.office.ui.ListFragment;
import com.example.office.utils.Utility;
import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.Configuration;
import com.microsoft.opentech.office.core.auth.ISharePointCredentials;
import com.microsoft.opentech.office.core.auth.method.AbstractOfficeAuthenticator;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.Entity.Builder;
import com.microsoft.opentech.office.lists.network.CreateListOperation;
import com.microsoft.opentech.office.lists.network.GetListsOperation;
import com.microsoft.opentech.office.lists.network.RemoveListOperation;
import com.squareup.otto.Subscribe;

/**
 * Lists fragment containing logic to display and manage SharePoint Lists.
 */
public class ListsFragment extends ListFragment<Item, ListsAdapter> {

    private static final String FRAGMENT_TAG = "ListItemsFragment";

    /**
     * List with items retrieved from SP.
     */
    private ListView mList;

    /**
     * Constructor.
     */
    public ListsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        try {
            mList = (ListView) rootView.findViewById(R.id.available_lists);
            registerForContextMenu(mList);

            final Button add = (Button) rootView.findViewById(R.id.add_list_button);
            add.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    onAddButtonClick(add);
                }
            });

            Configuration.setServerBaseUrl(Constants.SP_BASE_URL);
            authAndRefresh(rootView);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreateView(): Error.");
        }

        return rootView;
    }

    private void refreshLists(final View root) {
        ListsAdapter adapter = (ListsAdapter) getListAdapterInstance();
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
            mList.invalidate();
        }

        showWorkInProgress(true, true);

        new GetListsOperation(new IOperationCallback<List<Object>>() {
            public void onError(Throwable error) {
                Utility.showToastNotification(getString(R.string.main_status_error));
                showWorkInProgress(false, false);
            }

            public void onDone(final List<Object> result) {
                populateLists(result);
            }
        }, getActivity()).executeAsync();
    }

    @Override
    public void onResume() {
        try {
            EventBus.getInstance().register(this);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onResume(): Failed.");
        } finally {
            super.onResume();
        }
    }

    @Override
    public void onStop() {
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
        authAndRefresh(getView());
    }

    public void authAndRefresh(View view) {
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
                refreshLists(view);
                break;
            case OAUTH:
                Configuration.setAuthenticator(new AbstractOfficeAuthenticator() {
                    @Override
                    protected ISharePointCredentials getCredentials() {
                        return AuthPreferences.loadCredentials();
                    }

                    @Override
                    protected Activity getActivity() {
                        return ListsFragment.this.getActivity();
                    }
                });
                refreshLists(view);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.lists, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.list_remove:
                String title = getListAdapterInstance().getItem(info.position).getTitle();
                deleteList(title, info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteList(String title, final int adapterIndex) {
        final ListsAdapter adapter = getListAdapterInstance();
        String id = adapter.getItem(adapterIndex).getId();

        showWorkInProgress(true, false);

        new RemoveListOperation(new IOperationCallback<Boolean>() {
            public void onError(final Throwable error) {
                Utility.showToastNotification("Unable to remove list: " + error.getMessage());
                showWorkInProgress(false, false);
            }

            public void onDone(Boolean result) {
                showWorkInProgress(false, false);

                // TODO: check for result value
                adapter.remove(adapterIndex);
                adapter.notifyDataSetChanged();

                mList.invalidate();
            }
        }, getActivity(), id).executeAsync();
    }

    public void readList(String title, int index) {
        Fragment listItemsFragment = new ListItemsFragment();

        Bundle args = new Bundle();
        args.putString("listID", getListAdapterInstance().getItem(index).getId());
        listItemsFragment.setArguments(args);

        ((IFragmentNavigator)getActivity()).setCurrentFragmentTag(FRAGMENT_TAG);

        FragmentManager fragmentManager = ((BaseActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
            .add(R.id.content_pane, listItemsFragment, FRAGMENT_TAG)
            .addToBackStack(FRAGMENT_TAG);
        fragmentTransaction.commit();

        //TODO: verify if this is required.
        ((ActionBarActivity) getActivity()).supportInvalidateOptionsMenu();
    }

    public void createList() {

        Builder builder = new Entity.Builder("SP.List").set("BaseTemplate", 100).set("Title", "List, created using API")
                .set("Description", "SDK Playground");

        new CreateListOperation(new IOperationCallback<Entity>() {
            public void onError(Throwable error) {
                Utility.showToastNotification(getString(R.string.main_list_create_failure));
            }

            public void onDone(final Entity result) {
                if(result != null) {
                    String title = result.get("Title").toString();
                    String id = result.get("Id").toString();

                    ListsAdapter adapter = getListAdapterInstance();
                    adapter.add(new Item(id, title));
                    adapter.notifyDataSetChanged();

                    mList.invalidate();
                }
            }
        }, getActivity(), builder).executeAsync();
    }

    public void onAddButtonClick(View button) {
        createList();
    }

    /**
     * Show data from lists adapter.
     */
    private void showLists() {
        showWorkInProgress(false,  false);

        mList.setVisibility(View.VISIBLE);
        getView().findViewById(R.id.add_list_button).setVisibility(View.VISIBLE);

        ((Button) getView().findViewById(R.id.add_list_button)).setText(getString(R.string.main_list_create));

        mList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String title = getListAdapterInstance().getItem(position).getTitle();
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
            List<Item> data = new ArrayList<Item>(lists.size());
            Iterator<Object> iter = lists.iterator();
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

            showLists();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".populateLists(): Error.");
        }
    }

    /**
     * Shows NTLM authentication dialog.
     */
    public void showNTLMLoginDialog() {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.lists_auth_ntlm_dialog);
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

                                refreshLists(getView());
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
            Utility.showAlertDialog(ListsFragment.class.getSimpleName() + ".showCreateItemDialog(): Failed. " + e.toString(),
                    ListsFragment.this.getActivity());
        }
    }

    @Override
    protected ListsAdapter getListAdapterInstance(List<Item> data) {
        try {
            if (mAdapter == null) {
                mAdapter = new ListsAdapter(getActivity(), getListItemLayoutId(), data != null ? data : getListData());
            }
            return (ListsAdapter) mAdapter;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getListAdapterInstance(List): Error.");
        }
        return null;
    }

    @Override
    protected List<Item> getListData() {
        //TODO: add persistence and use it here
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
    protected void initList() {
        //TODO: use as soon as persistence is added
    }
}
