package com.example.office.lists.adapters;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.office.R;
import com.example.office.adapters.SearchableAdapter;
import com.example.office.lists.data.Item;

/**
 * Adapter for displaying SharePoint List items in ListView.
 */
public class ListsAdapter extends SearchableAdapter<Item> {

    /**
     * Default constructor.
     *
     * @param context Application context.
     * @param resource List item resource id.
     * @param data Data to populate.
     */
    public ListsAdapter(Context context, int resource, List<Item> data) {
        super(context, resource, data);
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

                // holder.image.setVisibility(item.getImage() ? View.VISIBLE : View.GONE);
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

    @Override
    protected boolean isMatch(Item item, CharSequence constraint) {
        if (item != null && !TextUtils.isEmpty(constraint)) {
            String title = item.getTitle();
            if (title.contains(constraint)) {
                return true;
            }
        }
        return false;
    }

}
