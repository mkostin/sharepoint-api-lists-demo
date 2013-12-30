package com.example.office.mail.data.odata;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

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
     * Unique mail id, generated server-side.
     */
    @SerializedName("Id")
    private String mId;

    /**
     * Version of the item, used to handle conflict resolution when the item is updated
     */
    @SerializedName("ChangeKey")
    private String mChangeKey;

    /**
     * Email subject.
     */
    @SerializedName("Subject")
    private String mSubject;

    /**
     * Body of the mail.
     */
    @SerializedName("Body")
    private ItemBody mBody;

    /**
     * Email body preview.
     */
    @SerializedName("BodyPreview")
    private String mPreview;

    /**
     * Indicates if email has attachments.
     */
    @SerializedName("HasAttachments")
    private boolean mHasAttachments;

    /**
     * Link to mail attachments
     */
    @SerializedName("Attachments@odata.navigationLink")
    private String mAttachmentsLink;

    /**
     * The categories assigned to the item.
     */
    @SerializedName("Categories")
    private List<String> mCategories;

    /**
     * The date and time the item was created.
     */
    @SerializedName("DateTimeCreated")
    private Date mDateTimeCreated;

    /**
     * The date and time the item was last modified.
     */
    @SerializedName("LastModifiedTime")
    private Date mLastModifiedTime;

    /**
     * Message importance.
     */
    @SerializedName("Importance")
    private Importance mImportance;

    /**
     * No-args constructor as described in GSON user guide.
     */
    protected Item() {}

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
        mChangeKey = source.getChangeKey();
        mSubject = source.getSubject();
        mBody = source.getBody();
        mPreview = source.getPreview();
        mHasAttachments = source.getHasAttachments();
        mAttachmentsLink = source.getAttachmentsLink();
        mCategories = source.getCategories();
        mDateTimeCreated = source.getDateTimeCreated();
        mLastModifiedTime = source.getLastModifiedTime();
        mImportance = source.getImportance();

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
     * Sets version of the item, used to handle conflict resolution when the item is updated.
     * 
     * @param changeKey New change key value.
     * @return Current {@link Item} instance.
     */
    public Item setChangeKey(String changeKey) {
        this.mChangeKey = changeKey;
        return this;
    }

    /**
     * Gets version of the item, used to handle conflict resolution when the item is updated.
     * 
     * @return Version key of the item.
     */
    public String getChangeKey() {
        return mChangeKey;
    }

    /**
     * Sets subject of the item.
     * 
     * @param subject New subject value.
     * @return Current {@link Item} instance.
     */
    public Item setSubject(String subject) {
        this.mSubject = subject;
        return this;
    }

    /**
     * Gets subject of the item.
     * 
     * @return Subject of the item.
     */
    public String getSubject() {
        return mSubject;
    }

    /**
     * Sets body of the item.
     * 
     * @param body New body value.
     * @return Current {@link Item} instance.
     */
    public Item setBody(ItemBody body) {
        this.mBody = body;
        return this;
    }

    /**
     * Gets body of the item.
     * 
     * @return Body of the item.
     */
    public ItemBody getBody() {
        return mBody;
    }

    /**
     * Sets preview of the item’s body.
     * 
     * @param preview New preview value.
     * @return Current {@link Item} instance.
     */
    public Item setPreview(String preview) {
        this.mPreview = preview;
        return this;
    }

    /**
     * Gets first few lines of the item’s body, in plain text
     * 
     * @return Body preview.
     */
    public String getPreview() {
        return mPreview;
    }

    /**
     * Sets a value indicating whether the item has at least one attachment.
     * 
     * @param hasAttachments A value indicating whether the item has at least one attachment
     * @return Current {@link Item} instance.
     */
    public Item setHasAttachments(boolean hasAttachments) {
        this.mHasAttachments = hasAttachments;
        return this;
    }

    /**
     * Gets a value indicating whether the item has at least one attachment.
     * 
     * @return A value indicating whether the item has at least one attachment.
     */
    public boolean getHasAttachments() {
        return mHasAttachments;
    }

    /**
     * Sets the link to attachments to the item.
     * 
     * @param attachmentsLink New attachments link value.
     * @return Current {@link Item} instance.
     */
    public Item setAttachmentsLink(String attachmentsLink) {
        this.mAttachmentsLink = attachmentsLink;
        return this;
    }

    /**
     * Gets the link to attachments to the item.
     * 
     * @return The link to attachments to the item.
     */
    public String getAttachmentsLink() {
        return mAttachmentsLink;
    }

    /**
     * Sets the categories assigned to the item.
     * 
     * @param categories New categories list.
     * @return Current {@link Item} instance.
     */
    public Item setCategories(List<String> categories) {
        this.mCategories = categories;
        return this;
    }

    /**
     * Gets the categories assigned to the item.
     * 
     * @return The categories assigned to the item.
     */
    public List<String> getCategories() {
        return mCategories;
    }

    /**
     * Sets the date and time the item was created.
     * 
     * @param dateTimeCreated New date and time the item was created value.
     * @return Current {@link Item} instance.
     */
    public Item setDateTimeCreated(Date dateTimeCreated) {
        this.mDateTimeCreated = dateTimeCreated;
        return this;
    }

    /**
     * Gets the date and time the item was created.
     * 
     * @return The date and time the item was created.
     */
    public Date getDateTimeCreated() {
        return mDateTimeCreated;
    }

    /**
     * Sets the date and time the item was last modified.
     * 
     * @param lastModifiedTime New the date and time the item was last modified value.
     * @return Current {@link Item} instance.
     */
    public Item setLastModifiedTime(Date lastModifiedTime) {
        this.mLastModifiedTime = lastModifiedTime;
        return this;
    }

    /**
     * Gets the date and time the item was last modified.
     * 
     * @return The date and time the item was last modified.
     */
    public Date getLastModifiedTime() {
        return mLastModifiedTime;
    }

    /**
     * Sets the importance of the item.
     * 
     * @param importance New importance value.
     * @return Current {@link Item} instance.
     */
    public Item setImportance(Importance importance) {
        this.mImportance = importance;
        return this;
    }

    /**
     * Gets the importance of the item.
     * 
     * @return The importance of the item.
     */
    public Importance getImportance() {
        return mImportance;
    }
}
