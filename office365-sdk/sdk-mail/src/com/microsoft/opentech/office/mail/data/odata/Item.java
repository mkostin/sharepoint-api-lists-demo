package com.microsoft.opentech.office.mail.data.odata;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import com.microsoft.opentech.office.core.IBuilder;

/**
 * The base entity type that all other entity types (Event, EmailMessage, etc.) derive from. It defines properties that are common to all
 * entity types.
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 4577232057493254824L;

    /**
     * Unique mail id, generated server-side.
     */
    protected String mId;

    /**
     * Version of the item, used to handle conflict resolution when the item is updated
     */
    protected String mChangeKey;

    /**
     * Email subject.
     */
    protected String mSubject;

    /**
     * Body of the mail.
     */
    protected ItemBody mBody;

    /**
     * Email body preview.
     */
    protected String mPreview;

    /**
     * Indicates if email has attachments.
     */
    protected boolean mHasAttachments;

    /**
     * Link to mail attachments
     */
    protected String mAttachmentsLink;

    /**
     * The categories assigned to the item.
     */
    protected List<String> mCategories;

    /**
     * The date and time the item was created.
     */
    protected Date mDateTimeCreated;

    /**
     * The date and time the item was last modified.
     */
    protected Date mLastModifiedTime;

    /**
     * Message importance.
     */
    protected Importance mImportance;

    /**
     * Default constructor.
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

    /**** Getters ****/

    /**
     * Gets an unique item id.
     * 
     * @return Item id.
     */
    public String getId() {
        return mId;
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
     * Gets subject of the item.
     * 
     * @return Subject of the item.
     */
    public String getSubject() {
        return mSubject;
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
     * Gets first few lines of the item’s body, in plain text
     * 
     * @return Body preview.
     */
    public String getPreview() {
        return mPreview;
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
     * Gets the link to attachments to the item.
     * 
     * @return The link to attachments to the item.
     */
    public String getAttachmentsLink() {
        return mAttachmentsLink;
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
     * Gets the date and time the item was created.
     * 
     * @return The date and time the item was created.
     */
    public Date getDateTimeCreated() {
        return mDateTimeCreated;
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
     * Gets the importance of the item.
     * 
     * @return The importance of the item.
     */
    public Importance getImportance() {
        return mImportance;
    }

    public static class Builder implements IBuilder<Item> {
        
        private Item mItem;
        
        public Builder() {
            mItem = new Item();
        }
        
        public Builder setId(String id) {
            mItem.mId = id;
            return this;
        }
        
        public Builder setChangeKey(String changeKey) {
            mItem.mChangeKey = changeKey;
            return this;
        }

        public Builder setSubject(String subject) {
            mItem.mSubject = subject;
            return this;
        }
        
        public Builder setBody(ItemBody body) {
            mItem.mBody = body;
            return this;
        }

        public Builder setHasAttachments(boolean hasAttachments) {
            mItem.mHasAttachments = hasAttachments;
            return this;
        }
        
        public Builder setPreview(String preview) {
            mItem.mPreview = preview;
            return this;
        }
        
        public Builder setAttachmentsLink(String attachmentsLink) {
            mItem.mAttachmentsLink = attachmentsLink;
            return this;
        }
        
        public Builder setCategories(List<String> categories) {
            mItem.mCategories = categories;
            return this;
        }
        
        public Builder setDateTimeCreated(Date dateTimeCreated) {
            mItem.mDateTimeCreated = dateTimeCreated;
            return this;
        }
        
        public Builder setLastModifiedTime(Date lastModifiedTime) {
            mItem.mLastModifiedTime = lastModifiedTime;
            return this;
        }
        
        public Builder setImportance(Importance importance) {
            mItem.mImportance = importance;
            return this;
        }
        
        public Item build() throws IllegalStateException {
            if (mItem.mId == null) {
                throw new IllegalStateException("You must specify id for item");
            }
            
            if (mItem.mChangeKey == null) {
                throw new IllegalStateException("You must specify change key");
            }
            
            if (mItem.mAttachmentsLink == null) {
                throw new IllegalStateException("You must specify attachments navigation link");
            }
            
            if (mItem.mDateTimeCreated == null) {
                throw new IllegalStateException("You must specify date when item has been created");
            }
            
            if (mItem.mImportance == null) {
                throw new IllegalStateException("You must specify importance");
            }
            
            return mItem;
        }
    }
}
