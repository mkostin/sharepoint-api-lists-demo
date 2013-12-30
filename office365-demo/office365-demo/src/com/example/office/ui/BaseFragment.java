package com.example.office.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base abstract fragment.
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Layout inflater to inflate footer when mails list is being populated
     */
    protected LayoutInflater mInflater;

    /**
     * Default constructor.
     */
    public BaseFragment() {
    }

    /**
     * Resource id of the fragment containing the list. Should be implemented/overridden.
     *
     * @return Resource id.
     */
    protected abstract int getFragmentLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = getLayoutInflater(savedInstanceState);
        View rootView = inflater.inflate(getFragmentLayoutId(), container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * To make super.onKeyDown() be called after your code return <code>false</code>. Otherwise return <code>true</code> and
     * <code>true</code> will be returned as a result of activity method.
     *
     * @param keyCode Key code.
     * @param event Key event.
     *
     * @return <code>true</code> to call super implementation, <code>false</code> otherwise.
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

