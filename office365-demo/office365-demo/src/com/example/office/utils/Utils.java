package com.example.office.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.office.Constants.UI;
import com.example.office.mail.data.BoxedMailItem;

/**
 * Helper class with helpful functions.
 */
public final class Utils {

    /**
     * Private constructor to prevent creating new instance of the class.
     */
    private Utils() {}

    public static final List<BoxedMailItem> boxMail(List<BoxedMailItem> list, UI.Screen box) {
        List<BoxedMailItem> result = new ArrayList<BoxedMailItem>();
        for (BoxedMailItem boxedItem : list) {
            if (boxedItem.getBox() == box) result.add(boxedItem);
        }
        return result;
    }

}
