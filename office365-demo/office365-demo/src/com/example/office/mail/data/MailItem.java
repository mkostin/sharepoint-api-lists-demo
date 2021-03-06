package com.example.office.mail.data;

import java.io.Serializable;
import java.util.Date;

import android.text.TextUtils;

/**
 * Represents single email item
 */
public class MailItem implements Serializable {

    /**
     * Default UID for serializable class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique mail id.
     */
    private String mId;

    /**
     * Sender name.
     */
    private String mName;

    /**
     * Receiving time stamp.
     */
    private Date mDate;

    /**
     * Email subject.
     */
    private String mSubject;

    /**
     * Email body preview.
     */
    private String mPreview;

    /**
     * Indicates if email has attachments.
     */
    private boolean mHasAttachments;
    
    /**
     * Email body content type (text or html)
     */
    private String mContentType;
    
    /**
     * Email body.
     */
    private String mContent;

    /**
     * Default constructor.
     * 
     * @param name Sender name.
     * @param date Receiving time stamp.
     * @param subject Email subject.
     * @param preview Email body.
     * @param hasAttachments Indicates if email has attachments.
     */
    public MailItem(String name, Date date, String subject, String preview, String contentType, String content, String id, boolean hasAttachments) {
        this.mName = name;
        this.mDate = date;
        this.mSubject = subject;
        this.mPreview = preview;
        this.mId = id;
        this.mHasAttachments = hasAttachments;
        this.mContentType = contentType;
        this.mContent = content;
    }

    /**
     * Performs a deep copy of all the fields into this Mail if: <br/>
     * 1. Argument is not <code>null</code> <br/>
     * 2. Argument id is neither <code>null</code> nor empty.
     * 
     * @param source An object to get data from.
     * 
     * @return <code>true</code> if copy has been performed successfully, <code>false</code> otherwise.
     */
    public boolean update(final MailItem source) {
        if (source == null || TextUtils.isEmpty(source.getId())) return false;

        mId = source.getId();
        mName = source.getName();
        mSubject = source.getSubject();
        mPreview = source.getPreview();
        mDate = source.getDate();
        mHasAttachments = source.getHasAttachments();

        return true;
    }

    /**
     * Considers MailItems equal if: <br>
     * 1. <b>id</b> of both objects are NOT null AND NOT empty AND equal (case matters) <br>
     * 2. <b>names</b> of both objects are null or empty OR equal (case matters) <br>
     * 3. <b>subjects</b> of both objects are null or empty OR equal (case matters) <br>
     * 4. <b>dates of</b> of both objects are null OR equal
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) return false;

        if (object instanceof MailItem) {
            String id = ((MailItem) object).getId();
            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(mId) && id.equals(mId)) {
                String name = ((MailItem) object).getName();
                String subject = ((MailItem) object).getSubject();
                Date date = ((MailItem) object).getDate();
                if ((TextUtils.isEmpty(name) && TextUtils.isEmpty(this.mName) || !(TextUtils.isEmpty(name) && name.equals(this.mName)))
                        && (TextUtils.isEmpty(subject) && TextUtils.isEmpty(this.mSubject) || !(TextUtils.isEmpty(subject) && name
                                .equals(this.mSubject))) && (date == null && this.mDate == null || date != null && date.equals(this.mDate))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        String result = mId == null ? "" : mId;
        result = mName == null ? result + mName : result;
        result = mSubject == null ? result + mSubject : result;
        result = mDate == null ? result + mDate.toString() : result;
        return result == null ? 0 : result.hashCode();
    }

    public String getName() {
        return mName;
    }

    public MailItem setName(String name) {
        this.mName = name;
        return this;
    }

    public Date getDate() {
        return mDate;
    }

    public MailItem setDate(Date date) {
        this.mDate = date;
        return this;
    }

    public String getSubject() {
        return mSubject;
    }

    public MailItem setSubject(String subject) {
        this.mSubject = subject;
        return this;
    }

    public String getPreview() {
        return mPreview;
    }

    public MailItem setPreview(String preview) {
        this.mPreview = preview;
        return this;
    }

    public String getId() {
        return mId;
    }

    public boolean getHasAttachments() {
        return mHasAttachments;
    }

    public MailItem setHasAttachments(boolean value) {
        this.mHasAttachments = value;
        return this;
    }
    
    public String getContentType() {
        return mContentType;
    }
    
    public MailItem setContentType(String contentType) {
        this.mContentType = contentType;
        return this;
    }
    
    public String getContent() {
        return mContent;
    }
    
    public MailItem setContent(String content) {
        this.mContent = content;
        return this;
    }
}
