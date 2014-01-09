package com.example.office.mail.data;

import com.example.office.Constants.UI;
import com.example.office.Constants.UI.Screen;
import com.microsoft.opentech.office.mail.data.odata.EmailMessage;
import com.microsoft.opentech.office.mail.data.odata.Importance;
import com.microsoft.opentech.office.mail.data.odata.Item;

/**
 * Represents mail item assignable to a specific box.
 */
public class BoxedMailItem extends EmailMessage {

    /**
     * Default UID for serializable class.
     */
    private static final long serialVersionUID = 3L;

    /**
     * Box that this items belongs to. Default value is {@link UI.Screen#MAILBOX}.
     */
    private UI.Screen mBox = Screen.MAILBOX;

    /**
     * Default constructor.
     */
    public BoxedMailItem(EmailMessage source, UI.Screen box) {
        super.update(source);
        this.mBox = box;
    }

    /**
     * Performs a deep copy of all the fields into this Mail. See detailed description here: {@link Item#update(Item)}.
     * 
     * @param source An object to get data from.
     * 
     * @return <code>true</code> if copy has been performed successfully, <code>false</code> otherwise.
     */
    public boolean update(final BoxedMailItem source) {
        if (!super.update(source)) {
            return false;
        }

        mBox = source.getBox();

        return true;
    }

    /**
     * Gets box this message is belong to.
     * 
     * @return Box this message is belong to.
     */
    public UI.Screen getBox() {
        return mBox;
    }

    /**
     * Sets box this message is belong to.
     * 
     * @param box New box value.
     * @return Current {@link BoxedMailItem} instance.
     */
    public BoxedMailItem setBox(UI.Screen box) {
        this.mBox = box;
        return this;
    }
    
    public BoxedMailItem setImportance(Importance importance) {
        this.mImportance = importance;
        return this;
    }
    
    public BoxedMailItem setIsRead(boolean isRead) {
        this.mIsRead = isRead;
        return this;
    }
}
