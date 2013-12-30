package com.example.office.ui.animate;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.office.adapters.SearchableAdapter;
import com.example.office.logger.Logger;
import com.example.office.ui.ListFragment;
import com.example.office.ui.animate.actions.AnimationAction;

/**
 * Fragment animating list items with custom actions.
 */
public abstract class AnimatedListFragment<T, A extends SearchableAdapter<T>> extends ListFragment<T, A> {

    /**
     * Animation listener specific for this fragment.
     */
    protected AnimatedListViewTouchListener mListener;

    /**
     * Constructor.
     */
    public AnimatedListFragment() {}

    /**
     * Adds touch listener for given ListView.
     *
     * @param view ListView which touch listener will be applied to.
     */
    protected abstract List<AnimationAction> getAnimationActions();

    /**
     * Resource id of the layout used to draw a list item.
     *
     * @return Resource id.
     */
    protected abstract int getListItemLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        try {
            // Setting up animation actions.
            final ListView mailListView = (ListView) rootView.findViewById(getListViewId());
            mListener = new AnimatedListViewTouchListener(getActivity(), mailListView);
            for (AnimationAction action : getAnimationActions()) {
                mListener.addAnimationAction(action);
            }
            mailListView.setOnTouchListener(mListener);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreateView(): Error.");
        }

        return rootView;
    }
}
