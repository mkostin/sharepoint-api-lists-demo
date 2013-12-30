package com.example.office.mail.data.odata;

import java.util.Date;
import java.util.List;

import android.text.TextUtils;

import com.example.office.logger.Logger;
import com.google.gson.annotations.SerializedName;

/**
 * Represents single email item
 */
public class EmailMessage extends Item {

    /**
     * Default UID for serializable class.
     */
    private static final long serialVersionUID = 2L;

    /**
     * The folder the message belongs to. This is a link as per current spec but current implementation gives id.
     */
    @SerializedName("ParentFolderId")
    private String mParentFolder;

    /**
     * The conversation the message is part of. This is a link as per current spec but current implementation gives id.
     */
    @SerializedName("ConversationIndex")
    private String mConversation;

    /**
     * The unique body of the message within the conversation.
     */
    @SerializedName("UniqueBody")
    private ItemBody mUniqueBody;

    /**
     * The To recipients. Each recipient is expressed as a string that contains both the routing type and the address:
     * <RoutingType>:<Address> Example: SMTP:user@contoso.com If the routing type is omitted, SMTP is assumed. Spec says it must be string
     * but endpoints return the struct above for now. TODO Do not forget change type to List<String> when endpoints will send data correctly
     */
    @SerializedName("ToRecipients")
    private List<User> mToRecipients;

    /**
     * The Cc recipients. Spec says it must be string but endpoints return the struct above for now. TODO Do not forget change type to
     * List<String> when endpoints will send data correctly
     */
    @SerializedName("CcRecipients")
    private List<User> mCcRecipents;

    /**
     * The Bcc recipients. Spec says it must be string but endpoints return the struct above for now. TODO Do not forget change type to
     * List<String> when endpoints will send data correctly
     */
    @SerializedName("BccRecipients")
    private List<User> mBccRecipients;

    /**
     * The sender. Spec says it must be string but endpoints return the struct above for now. TODO Do not forget change type to String when
     * endpoints will send data correctly
     */
    @SerializedName("Sender")
    private User mSender;

    /**
     * The “on behalf” address in delegate scenarios: [Sender] on behalf of [From] Spec says it must be string but endpoints return the
     * struct above for now. TODO Do not forget change type to String when endpoints will send data correctly
     */
    @SerializedName("From")
    private User mFrom;

    /**
     * The addresses replies to this message should be addressed to. Spec says it must be string but endpoints return the struct above for
     * now. TODO Do not forget change type to List<String> when endpoints will send data correctly
     */
    @SerializedName("ReplyTo")
    private List<User> mReplyTo;

    /**
     * Specifies whether a delivery receipt is requested by the sender.
     */
    @SerializedName("IsDeliveryReceiptRequested")
    private boolean mIsDeliveryReceiptRequested;

    /**
     * Specifies whether a read receipts if requested by the sender.
     */
    @SerializedName("IsReadReceiptRequested")
    private boolean mIsReadReceiptRequested;

    /**
     * Specifies whether this message has been read.
     */
    @SerializedName("IsRead")
    private boolean mIsRead;

    /**
     * Specifies whether this message is a draft.
     */
    @SerializedName("IsDraft")
    private boolean mIsDraft;

    /**
     * Specifies the date and time when this message was received.
     */
    @SerializedName("DateTimeReceived")
    private Date mDateTimeReceived;

    /**
     * Specifies the date and time when this message was sent.
     */
    @SerializedName("DateTimeSent")
    private Date mDateTimeSent;

    /**
     * Default constructor.
     */
    public EmailMessage() {}

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
            try {
                String id = ((EmailMessage) object).getId();
                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(getId()) && id.equals(getId())) {
                    // String sender = ((EmailMessage) object).getSender();
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
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".equals(): Error.");
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
     * Sets folder the message belongs to. This is a link as per current spec but current implementation gives id.
     * 
     * @param parentFolder New parent folder value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setParentFolder(String parentFolder) {
        this.mParentFolder = parentFolder;
        return this;
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
     * Sets the conversation the message is part of. This is a link as per current spec but current implementation gives id.
     * 
     * @param conversation New message conversation.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setConversation(String conversation) {
        this.mConversation = conversation;
        return this;
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
     * Sets the unique body of the message within the conversation.
     * 
     * @param uniqueBody New unique body of the message.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setUniqueBody(ItemBody uniqueBody) {
        this.mUniqueBody = uniqueBody;
        return this;
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
     * Sets ToRecipients field value. Each recipient is expressed as a string that contains both the routing type and the address:
     * <RoutingType>:<Address> Example: SMTP:user@contoso.com If the routing type is omitted, SMTP is assumed.
     * 
     * @param toRecipients New ToRecipients field value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setToRecipients(List<User> toRecipients) {
        this.mToRecipients = toRecipients;
        return this;
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
     * Sets the Cc recipients.
     * 
     * @param ccRecipents Cc recipients list.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setCcRecipents(List<User> ccRecipents) {
        this.mCcRecipents = ccRecipents;
        return this;
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
     * Sets the Bcc recipients.
     * 
     * @param ccRecipents Bcc recipients list.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setBccRecipients(List<User> bccRecipients) {
        this.mBccRecipients = bccRecipients;
        return this;
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
     * Sets the message sender.
     * 
     * @param sender New sender value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setSender(String sender) {
        this.mSender.setAddress(sender);
        return this;
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
     * Sets the “on behalf” user in delegate scenarios: [Sender] on behalf of [From].
     * 
     * @param from New "From" value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setFrom(String from) {
        this.mFrom.setAddress(from);
        return this;
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
     * Sets the addresses replies to this message should be addressed to.
     * 
     * @param replyTo New list of the addresses replies to this message should be addressed to.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setReplyTo(List<User> replyTo) {
        this.mReplyTo = replyTo;
        return this;
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
     * Sets a value indicating whether a delivery receipt is requested by the sender.
     * 
     * @param isDeliveryReceiptRequested New delivery receipt indicator value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setIsDeliveryReceiptRequested(boolean isDeliveryReceiptRequested) {
        this.mIsDeliveryReceiptRequested = isDeliveryReceiptRequested;
        return this;
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
     * Sets a value whether a read receipts if requested by the sender.
     * 
     * @param isReadReceiptRequested New value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setIsReadReceiptRequested(boolean isReadReceiptRequested) {
        this.mIsReadReceiptRequested = isReadReceiptRequested;
        return this;
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
     * Sets a value indicating whether this message has been read.
     * 
     * @param isRead New value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setIsRead(boolean isRead) {
        this.mIsRead = isRead;
        return this;
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
     * Sets a value indicating Specifies whether this message is a draft.
     * 
     * @param isDraft New value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setIsDraft(boolean isDraft) {
        this.mIsDraft = isDraft;
        return this;
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
     * Sets the date and time when this message was received.
     * 
     * @param dateTimeReceived New received date value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setDateTimeReceived(Date dateTimeReceived) {
        this.mDateTimeReceived = dateTimeReceived;
        return this;
    }

    /**
     * Gets the date and time when this message was sent.
     * 
     * @return The date and time when this message was sent.
     */
    public Date getDateTimeSent() {
        return mDateTimeSent;
    }

    /**
     * Sets the date and time when this message was sent.
     * 
     * @param dateTimeSent New sent date value.
     * @return Current {@link EmailMessage} instance.
     */
    public EmailMessage setDateTimeSent(Date dateTimeSent) {
        this.mDateTimeSent = dateTimeSent;
        return this;
    }
}
