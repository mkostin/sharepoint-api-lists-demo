package com.example.office.lists.ui;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.ui.BaseFragment;
import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.lists.network.GetItemOperation;

/**
 * Email details fragment.
 */
public class ListItemFragment extends BaseFragment implements IOperationCallback<Entity> {

    private Future<Entity> mGetItemFuture = null;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.lists_list_item_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        try {
            ActionBarActivity activity = (ActionBarActivity) getActivity();
            activity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            Bundle extras = getActivity().getIntent().getExtras();
            mGetItemFuture = new GetItemOperation(this, getActivity(), extras.getString("listGUID"), extras.getInt("itemId")).executeAsync();

            ((ActionBarActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreateView(): Error.");
        }

        return rootView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mGetItemFuture != null) {
                mGetItemFuture.cancel(true);
                mGetItemFuture = null;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void onDone(final Entity result) {
        showFields(result);
        mGetItemFuture = null;
    }

    public void onError(Throwable error) {
        mGetItemFuture = null;
    }

    private void showFields(Entity value) {
        getView().findViewById(R.id.get_fields_text_stub).setVisibility(View.GONE);
        TableLayout table = (TableLayout) getView().findViewById(R.id.item_fields_table);
        table.setVisibility(View.VISIBLE);

        Iterator<Pair<String, Object>> iterator = value.iterator();
        while (iterator.hasNext()) {
            Pair<String, Object> next = iterator.next();
            String title = next.first, text;
            if (next.second == null) {
                text = "(null)";
            } else if (next.second instanceof List) {
                text = "(collection)";
            } else if (next.second instanceof Entity) {
                text = "(complex value)";
            } else {
                text = next.second.toString();
            }

            TableRow row = new TableRow(getActivity());
            TextView titleView = new TextView(getActivity());
            titleView.setText(title);
            row.addView(titleView);

            TextView textView = new TextView(getActivity());
            textView.setText(text);
            row.addView(textView);

            table.addView(row);
        }
    }
}
