package com.example.office.mail.data;

import java.io.Serializable;
import java.util.List;

import com.example.office.logger.Logger;
import com.example.office.utils.NetworkUtils;

/**
 * Implements mails configuration class. Stores list of {@link BoxedMailItem} received from the server and time when it was received.
 */
public class MailConfig implements Serializable {

    /**
     * Default UID for serializable class.
     */
    private static final long serialVersionUID = 4L;

    /**
     * Time, configuration was retrieved from server side.
     */
    private long mTimeStamp;

    /**
     * Valid SIM number for the configuration.
     */
    private String mSimNumber;

    /**
     * Notification mails.
     */
    private List<BoxedMailItem> mMailItems;

    /**
     * Creates new instance of the class.
     *
     * @param timeStamp Time stamp when application configuration was updated last time.
     */
    public MailConfig(long timeStamp) {
        mTimeStamp = timeStamp;
        mSimNumber = NetworkUtils.getCurrentSimCardNumber();
    }

    /**
     * Retrieves email with specified id <b>(case matters!)</b> if it exists.
     *
     * @param id Id of the email.
     *
     * @return {@linkplain MailItem} if one exists with specified id, <code>null</code> otherwise.
     */
    public BoxedMailItem getMailById(String id) {
        try {
            if (mMailItems == null || id == null) return null;
            for (BoxedMailItem mail : mMailItems) {
                if (mail != null && id.equals(mail.getId())) return mail;
            }
            return null;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getMailById(): Failed.");
        }
        return null;
    }

    /**
     * Updates email with specified id <b>(case matters!)</b> if it exists.
     *
     * @param id Id of the email.
     *
     * @return <code>true</code> if item with specified id exists and has been updated, <code>null</code> otherwise.
     */
    public boolean updateMailById(String id, BoxedMailItem newItem) {
        try {
            if (mMailItems == null || id == null) return false;
            for (int i = 0; i < mMailItems.size(); i++) {
                BoxedMailItem mail = mMailItems.get(i);
                if (mail != null && id.equals(mail.getId())) {
                    mMailItems.set(i, newItem);
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getMailById(): Failed.");
        }
        return false;
    }
    
    /**
     * Removes email with specified id <b>(case matters!)</b> if it exists.
     * @param id Id of the email.
     */
    public void removeMailById(String id) {
        try {
            if (mMailItems == null || id == null) return;
            for (int i = 0; i < mMailItems.size(); i++) {
                BoxedMailItem mail = mMailItems.get(i);
                if (mail != null && id.equals(mail.getId())) {
                    mMailItems.remove(mail);
                    return;
                }
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getMailById(): Failed.");
        }
    }

    /**
     * Time configuration was retrieved from server side.
     *
     * @return Time, configuration was retrieved from server side.
     */
    public long getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * Sets time configuration was retrieved from server side.
     *
     * @param timeStamp Time, configuration was retrieved from server side.
     */
    public void setTimeStamp(long timeStamp) {
        mTimeStamp = timeStamp;
    }

    /**
     * Retrieves valid SIM number for current configuration.
     *
     * @return Valid SIM number for current configuration.
     */
    public String getSimNumber() {
        return mSimNumber;
    }

    /**
     * Sets new mails.
     *
     * @param mails New mails.
     */
    public void setMails(List<BoxedMailItem> mails) {
        mMailItems = mails;
    }

    /**
     * Returns mails.
     *
     * @return mails.
     */
    public List<BoxedMailItem> getMails() {
        return mMailItems;
    }
}