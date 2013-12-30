package com.example.office.ui.animate.actions;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.ui.animate.AnimatedListViewTouchListener;

public abstract class ListItemAnimationAction<T> extends AnimationAction {

    /**
     * ID of email which this operation is related to
     */
    protected T currentItem;

    /**
     * Listener where current operation is attached to
     */
    private AnimatedListViewTouchListener listener;

    protected void cancel() {
        listener.notifyActionCanceled();
    }

    protected void complete() {
        listener.notifyActionCompleted();
    }

    protected ListItemAnimationAction(AnimatedListViewTouchListener listener, Direction direction, float partition, int iconId, int bgColor) {
        super(direction, partition, iconId, bgColor);
        this.listener = listener;
    }

    @SuppressWarnings("unchecked")
    public boolean onFinish(Object... params) {
        try {
            ArrayAdapter<T> adapter = (ArrayAdapter<T>) ((HeaderViewListAdapter) listener.getView().getAdapter()).getWrappedAdapter();
            adapter.remove(currentItem);
            adapter.notifyDataSetChanged();
            updateFooter();
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".updateCurrentBox(): Error.");
        }

        return false;
    }

    /**
     * Updates footer to hide/show it and display number of shown mails
     */
    @SuppressWarnings("unchecked")
    private void updateFooter() {
        try {
            ListView listView = listener.getView();
            View footer = listView.getChildAt(listView.getChildCount() - 1);
            ArrayAdapter<T> adapter = (ArrayAdapter<T>) ((HeaderViewListAdapter) listView.getAdapter()).getWrappedAdapter();
            if (adapter.getCount() <= 0) {
                listView.removeFooterView(footer);
            } else {
                if (listView.getFooterViewsCount() == 0) {
                    listView.addFooterView(footer);
                }
                ((TextView) footer.findViewById(R.id.footer_mail_count)).setText(String.valueOf(adapter.getCount()));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".updateFooter(): Error.");
        }
    }

    /**
     * Gets an email associated with given position in ListView.
     * 
     * @param position ListView position.
     * @return Appropriate email.
     */
    @SuppressWarnings("unchecked")
    protected T getItemByListViewPosition(int position) {
        try {
            int adapterPosition = listener.getView().getPositionForView(listener.getActiveView(position));
            return (T) listener.getView().getAdapter().getItem(adapterPosition);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getEmailByListViewPosition(): Error.");
        }

        return null;
    }
}
