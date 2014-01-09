package com.microsoft.opentech.office.mail.data.odata;

import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import com.microsoft.opentech.office.core.IBuilder;

/**
 * Represents single email item
 */
public class EmailMessage extends Item {

    private static final long serialVersionUID = -3278115309640554439L;

    /**
     * The folder the message belongs to. This is a link as per current spec but current implementation gives id.
     */
    protected String mParentFolder;

    /**
     * The conversation the message is part of. This is a link as per current spec but current implementation gives id.
     */
    protected String mConversation;

    /**
     * The unique body of the message within the conversation.
     */
    protected ItemBody mUniqueBody;

    /**
     * The To recipients. Each recipient is expressed as a string that contains both the routing type and the address:
     * <RoutingType>:<Address> Example: SMTP:user@contoso.com If the routing type is omitted, SMTP is assumed. Spec says it must be string
     * but endpoints return the struct above for now. TODO Do not forget change type to List<String> when endpoints will send data correctly
     */
    protected List<User> mToRecipients;

    /**
     * The Cc recipients. Spec says it must be string but endpoints return the struct above for now. TODO Do not forget change type to
     * List<String> when endpoints will send data correctly
     */
    protected List<User> mCcRecipents;

    /**
     * The Bcc recipients. Spec says it must be string but endpoints return the struct above for now. TODO Do not forget change type to
     * List<String> when endpoints will send data correctly
     */
    protected List<User> mBccRecipients;

    /**
     * The sender. Spec says it must be string but endpoints return the struct above for now. TODO Do not forget change type to String when
     * endpoints will send data correctly
     */
    protected User mSender;

    /**
     * The “on behalf” address in delegate scenarios: [Sender] on behalf of [From] Spec says it must be string but endpoints return the
     * struct above for now. TODO Do not forget change type to String when endpoints will send data correctly
     */
    protected User mFrom;

    /**
     * The addresses replies to this message should be addressed to. Spec says it must be string but endpoints return the struct above for
     * now. TODO Do not forget change type to List<String> when endpoints will send data correctly
     */
    protected List<User> mReplyTo;

    /**
     * Specifies whether a delivery receipt is requested by the sender.
     */
    protected boolean mIsDeliveryReceiptRequested;

    /**
     * Specifies whether a read receipts if requested by the sender.
     */
    protected boolean mIsReadReceiptRequested;

    /**
     * Specifies whether this message has been read.
     */
    protected boolean mIsRead;

    /**
     * Specifies whether this message is a draft.
     */
    protected boolean mIsDraft;

    /**
     * Specifies the date and time when this message was received.
     */
    protected Date mDateTimeReceived;

    /**
     * Specifies the date and time when this message was sent.
     */
    protected Date mDateTimeSent;

    protected EmailMessage() {}

    /**
     * Performs a deep copy of all the fields into this Mail. See detailed description here: {@link Item#update(Item)}.
     * 
     * @param source An object to get data from.
     * 
     * @return <code>true</code> if copy has been performed successfully, <code>false</code> otherwise.
     */
    public boolean update(final EmailMessage source) {
        if (!super.update(source)) {
            return false;
        }

        mParentFolder = source.getParentFolder();
        mConversation = source.getConversation();
        mUniqueBody = source.getUniqueBody();
        mToRecipients = source.getToRecipients();
        mCcRecipents = source.getCcRecipents();
        mBccRecipients = source.getBccRecipients();
        mSender = source.getSender();
        mFrom = source.getFrom();
        mReplyTo = source.getReplyTo();
        mIsDeliveryReceiptRequested = source.getIsDeliveryReceiptRequested();
        mIsReadReceiptRequested = source.getIsReadReceiptRequested();
        mIsRead = source.getIsRead();
        mIsDraft = source.getIsDraft();
        mDateTimeReceived = source.getDateTimeReceived();
        mDateTimeSent = source.getDateTimeSent();

        return true;
    }

    /**
     * Considers MailItems equal if: <br>
     * 1. <b>id</b> of both objects are NOT null AND NOT empty AND equal (case matters) <br>
     * 2. <b>sender addresses</b> of both objects are null or empty OR equal (case matters) <br>
     * 3. <b>subjects</b> of both objects are null or empty OR equal (case matters) <br>
     * 4. <b>dates of</b> of both objects are null OR equal
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) return false;

        if (object instanceof EmailMessage) {
            String id = ((EmailMessage) object).getId();
            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(getId()) && id.equals(getId())) {
                User sender = ((EmailMessage) object).getSender();
                if ((sender == null && this.getSender() != null) || (sender != null && getSender() == null)) {
                    return false;
                }

                if (sender == null && getSender() == null) {
                    return true;
                }

                String senderAddress = sender.getAddress();

                String subject = ((EmailMessage) object).getSubject();
                Date receivedDate = ((EmailMessage) object).getDateTimeReceived();
                if ((TextUtils.isEmpty(senderAddress) && TextUtils.isEmpty(this.getSender().getAddress()) || !(TextUtils.isEmpty(senderAddress) && sender
                        .equals(this.mSender)))
                        && (TextUtils.isEmpty(subject) && TextUtils.isEmpty(this.getSubject()) || !(TextUtils.isEmpty(subject) && sender
                                .equals(this.getSubject())))
                        && (receivedDate == null && this.mDateTimeReceived == null || receivedDate != null
                                && receivedDate.equals(this.mDateTimeReceived))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        String result = getId() == null ? "" : getId();
        result = mSender == null ? result + mSender : result;
        result = getSubject() == null ? result + getSubject() : result;
        result = mDateTimeReceived == null ? result + mDateTimeReceived.toString() : result;
        return result == null ? 0 : result.hashCode();
    }

    /**** Getters and setters ****/

    /**
     * Gets folder the message belongs to. This is a link as per current spec but current implementation gives id.
     * 
     * @return Message parent folder.
     */
    public String getParentFolder() {
        return mParentFolder;
    }

    /**
     * Gets the conversation the message is part of. This is a link as per current spec but current implementation gives id.
     * 
     * @return The conversation the message is part of.
     */
    public String getConversation() {
        return mConversation;
    }

    /**
     * Gets the unique body of the message within the conversation.
     * 
     * @return Unique body of the message within the conversation.
     */
    public ItemBody getUniqueBody() {
        return mUniqueBody;
    }

    /**
     * Gets ToRecipients field value. Each recipient is expressed as a string that contains both the routing type and the address:
     * <RoutingType>:<Address> Example: SMTP:user@contoso.com If the routing type is omitted, SMTP is assumed.
     * 
     * @return List of ToRecipients.
     */
    public List<User> getToRecipients() {
        return mToRecipients;
    }


    /**
     * Gets the Cc recipients.
     * 
     * @return The Cc recipients.
     */
    public List<User> getCcRecipents() {
        return mCcRecipents;
    }

    /**
     * Gets the Bcc recipients.
     * 
     * @return The Bcc recipients.
     */
    public List<User> getBccRecipients() {
        return mBccRecipients;
    }

    /**
     * Gets the message sender.
     * 
     * @return Message sender.
     */
    public User getSender() {
        return mSender;
    }

    /**
     * Gets the “on behalf” user in delegate scenarios: [Sender] on behalf of [From].
     * 
     * @return “On behalf” user in delegate scenarios: [Sender] on behalf of [From].
     */
    public User getFrom() {
        return mFrom;
    }

    /**
     * Gets the addresses replies to this message should be addressed to.
     * 
     * @return List of the addresses replies to this message should be addressed to.
     */
    public List<User> getReplyTo() {
        return mReplyTo;
    }

    /**
     * Gets a value indicating whether a delivery receipt is requested by the sender.
     * 
     * @return The value indicating whether a delivery receipt is requested by the sender.
     */
    public boolean getIsDeliveryReceiptRequested() {
        return mIsDeliveryReceiptRequested;
    }

    /**
     * Gets a value whether a read receipts if requested by the sender.
     * 
     * @return The value indicating whether a read receipts if requested by the sender. 
     */
    public boolean getIsReadReceiptRequested() {
        return mIsReadReceiptRequested;
    }

    /**
     * Gets a value indicating whether this message has been read.
     * 
     * @return The value indicating whether this message has been read.
     */
    public boolean getIsRead() {
        return mIsRead;
    }

    /**
     * Gets a value indicating Specifies whether this message is a draft.
     * 
     * @return The value indicating Specifies whether this message is a draft.
     */
    public boolean getIsDraft() {
        return mIsDraft;
    }

    /**
     * Gets the date and time when this message was received.
     * 
     * @return The date and time when this message was received.
     */
    public Date getDateTimeReceived() {
        return mDateTimeReceived;
    }

    /**
     * Gets the date and time when this message was sent.
     * 
     * @return The date and time when this message was sent.
     */
    public Date getDateTimeSent() {
        return mDateTimeSent;
    }

    public static class Builder implements IBuilder<EmailMessage> {
        
        private EmailMessage mMail;
        
        public Builder(Item.Builder parentBuilder) throws IllegalStateException {
            mMail = new EmailMessage();
            mMail.update(parentBuilder.build());
        }
        
        public Builder setParentFolder(String parentFolder) {
            mMail.mParentFolder = parentFolder;
            return this;
        }
        
        public Builder setConversation(String conversation) {
            mMail.mConversation = conversation;
            return this;
        }
        
        public Builder setUniqueBody(ItemBody uniqueBody) {
            mMail.mUniqueBody = uniqueBody;
            return this;
        }
        
        public Builder setToRecipients(List<User> toRecipients) {
            mMail.mToRecipients = toRecipients;
            return this;
        }
        
        public Builder setCcRecipents(List<User> ccRecipents) {
            mMail.mCcRecipents = ccRecipents;
            return this;
        }

        public Builder setBccRecipients(List<User> bccRecipients) {
            mMail.mBccRecipients = bccRecipients;
            return this;
        }
        
        public Builder setSender(String sender) {
            mMail.mSender.setAddress(sender);
            return this;
        }
        
        public Builder setFrom(String from) {
            mMail.mFrom.setAddress(from);
            return this;
        }
        
        public Builder setReplyTo(List<User> replyTo) {
            mMail.mReplyTo = replyTo;
            return this;
        }

        public Builder setIsDeliveryReceiptRequested(boolean isDeliveryReceiptRequested) {
            mMail.mIsDeliveryReceiptRequested = isDeliveryReceiptRequested;
            return this;
        }

        public Builder setIsReadReceiptRequested(boolean isReadReceiptRequested) {
            mMail.mIsReadReceiptRequested = isReadReceiptRequested;
            return this;
        }
        
        public Builder setIsRead(boolean isRead) {
            mMail.mIsRead = isRead;
            return this;
        }

        public Builder setIsDraft(boolean isDraft) {
            mMail.mIsDraft = isDraft;
            return this;
        }

        public Builder setDateTimeReceived(Date dateTimeReceived) {
            mMail.mDateTimeReceived = dateTimeReceived;
            return this;
        }

        public Builder setDateTimeSent(Date dateTimeSent) {
            mMail.mDateTimeSent = dateTimeSent;
            return this;
        }
    
        @Override
        public EmailMessage build() throws IllegalStateException {
            if (mMail.mParentFolder == null) {
                throw new IllegalStateException("You must specify parent folder");
            }
            
            if (mMail.mConversation == null) {
                throw new IllegalStateException("You must specify conversation link");
            }
            
            if (mMail.mDateTimeReceived == null) {
                throw new IllegalStateException("You must specify date message has been received");
            }
            
            if (mMail.mDateTimeSent == null) {
                throw new IllegalStateException("You must specify date message has been sent");
            }
            
            return mMail;
        }
    }
}
