package com.example.office.ui;

/**
 * Allows backwards communication from fragment.
 */
public interface IFragmentNavigator {

    /**
     * Sets current fragment tag.
     *
     * @param tag Fragment tag.
     */
    public void setCurrentFragmentTag(String tag);

    /**
     * Gets current fragment tag.
     */
    public String getCurrentFragmentTag();
}