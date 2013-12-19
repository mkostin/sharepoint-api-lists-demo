package com.example.sharepoint.client.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.data.Item;
import com.example.sharepoint.client.logger.Logger;

/**
 * Adapter for displaying MailItem in ListView
 */
public class ListsAdapter extends ArrayAdapter<Item> {

    /**
     * Layout inflater
     */
    private LayoutInflater mInflater;

    /**
     * Resource id for single ListView item
     */
    private int mItemResource;

    /**
     * Current filter query;
     */
    private CharSequence mCurrentFilter;

    /**
     * A vector that contains an actual set of mSuggestions. It is updated each time the content of mUrlEditTextView is changed.
     */
    private List<Item> mFilteredData;
    /**
     * A data reference which is used to populate mSuggestions. It is never allocated internally and always refers to the merged storage of
     * PersistenceManager.
     */
    private List<Item> mOriginalData;

    /**
     * Default constructor.
     *
     * @param context Application context.
     * @param resource List item resource id.
     * @param data Data to populate.
     */
    public ListsAdapter(Context context, int resource, List<Item> data) {
        super(context, resource, data != null ? data : new ArrayList<Item>());
        try {
            mInflater = LayoutInflater.from(context);
            mItemResource = resource;
            mOriginalData = new ArrayList<Item>(data);
            mFilteredData = new ArrayList<Item>();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".constructor(): Error.");
        }
    }

    /**
     * Updates adapter with new underlying data. No update takes place if provided data is <code>null</code>. Provide an empty list if you
     * would like to clean up all current items.
     *
     * @return <code>true</code> if there were no errors, <code>false</code> otherwise.
     */
    public boolean update(List<Item> data) {
        try {
            if (data != null) {
                mOriginalData = new ArrayList<Item>(data);
                mFilteredData = new ArrayList<Item>();

                return updateAndNotify(mOriginalData);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".update(): Error.");
        }
        return false;
    }

    /**
     * Clears current data, updates it with a new one and notifies adapter of the change.
     *
     * @param data Data update.
     */
    private boolean updateAndNotify(List<Item> data) {
        try {
            clear();
            if (data != null) {
                for (Item item : data) {
                    add(item);
                }
            }
            notifyDataSetChanged();
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".updateAndNotify(): Error.");
        }
        return false;
    }

    /**
     * Sets a text for a view with additional verification.
     *
     * @param view TextView to set text into.
     * @param text String to set.
     */
    public void setViewText(TextView view, String text) {
        try {
            if (view != null && text != null) {
                view.setText(text);
            }
        } catch (Exception e) {}
    };

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * Constructs and returns View for filling ListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ItemHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(mItemResource, null);

                holder = new ItemHolder();
                holder.id = (TextView) convertView.findViewById(R.id.item_id);
                holder.title = (TextView) convertView.findViewById(R.id.item_title);

                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }

            Item item = (Item) getItem(position);
            if (item != null) {
                String id = getContext().getString(R.string.data_id_unknown);
                if (!TextUtils.isEmpty(item.getId())) {
                    id = item.getId();
                }

                String title = item.getTitle() == null ? getContext().getString(R.string.data_title_unknown) : item.getTitle();

                setViewText(holder.id, id);
                setViewText(holder.title, title);

                //holder.image.setVisibility(item.getImage() ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {}
        return convertView;
    }

    /**
     * Represents an inner structure of single ListView item
     */
    private class ItemHolder {
        TextView id;
        TextView title;
        ImageView image;
    }

    /**
     * Removes item from the selected position in the adapter.
     *
     * @param position Item position.
     */
    public void remove(int position) {
        try {
            remove(getItem(position));
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "remove(): Error.");
        }
    }

    /**
     * An instance of Filter for handling mSuggestions. No filtering would be applied in case of error.
     */
    Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            try {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    if (constraint.equals(mCurrentFilter)) {
                        // No need to double-filter.
                        return null;
                    } else if (constraint.equals("") && !TextUtils.isEmpty(mCurrentFilter)) {
                        // Check if this request is an empty string while previous filter is NOT so original data has to be restored.
                        mCurrentFilter = constraint;
                        return getOriginalFilter();
                    }
                }

                mCurrentFilter = constraint;
                if (!TextUtils.isEmpty(constraint)) {
                    mFilteredData.clear();
                    String id = null;
                    String title = null;
                    for (Item item : mOriginalData) {
                        title = item.getTitle();
                        id = item.getId();
                        if (title.contains(constraint) || id.contains(constraint)) {
                            mFilteredData.add(item);
                        }
                    }

                    filterResults.values = mFilteredData;
                    filterResults.count = mFilteredData.size();

                    return filterResults;
                } else {
                    return getOriginalFilter();
                }
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".mFilter.performFiltering(): Error.");
            }
            return getOriginalFilter();
        }

        /**
         * Returns filter that imposes no filtering on original data.
         *
         * @return List filter, or <code>null</code> in case of error.
         */
        private FilterResults getOriginalFilter() {
            try {
                FilterResults filterResults = new FilterResults();
                filterResults.values = mOriginalData;
                filterResults.count = mOriginalData.size();
                return filterResults;
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".mFilter.getOriginalFilter(): Error.");
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                if (results != null) {
                    @SuppressWarnings("unchecked")
                    List<Item> list = (List<Item>) results.values;
                    updateAndNotify(list);
                }
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".mFilter.publishResults(): Error.");
            }
        }
    };

}
