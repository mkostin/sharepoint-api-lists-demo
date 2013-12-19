package com.example.sharepoint.client.data;

import java.io.Serializable;
import java.net.URI;

import android.text.TextUtils;

/**
 * The base entity type that all other entity types (Event, EmailMessage, etc.) derive from. It defines properties that are common to all
 * entity types.
 */
public class Item implements Serializable {

    /**
     * Default UID for serializable class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique id, generated server-side.
     */
    private String mId;

    /**
     * Item title.
     */
    private String mTitle;

    /**
     * Item image.
     */
    private URI mImageUrl;

    public Item() {
    }

    /**
     * Basic constructor.
     *
     * @param id Item id.
     * @param title Item title.
     */
    public Item(String id, String title) {
        mId = id;
        mTitle = title;
    }

    /**
     * Performs a deep copy of all the fields into this Item if: <br/>
     * 1. Argument is not <code>null</code> <br/>
     * 2. Argument id is neither <code>null</code> nor empty.
     *
     * @param source An object to get data from.
     *
     * @return <code>true</code> if copy has been performed successfully, <code>false</code> otherwise.
     */
    public boolean update(final Item source) {
        if (source == null || TextUtils.isEmpty(source.getId())) return false;

        mId = source.getId();
        mTitle = source.getTitle();

        return true;
    }

    /**** Getters and setters ****/

    /**
     * Sets a mail id.
     *
     * @param id New id value.
     * @return Current {@link Item} instance.
     */
    public Item setId(String id) {
        this.mId = id;
        return this;
    }

    /**
     * Gets an unique mail id.
     *
     * @return Mail id.
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets item title.
     *
     * @param title title value.
     *
     * @return Current {@link Item} instance.
     */
    public Item setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * Gets item title.
     *
     * @return Item title.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets image url.
     *
     * @param url image url.
     *
     * @return Current {@link Item} instance.
     */
    public Item setImageUrl(URI url) {
        this.mImageUrl = url;
        return this;
    }

    /**
     * Gets image url.
     *
     * @return Item image url.
     */
    public URI getImageUrl() {
        return mImageUrl;
    }
}