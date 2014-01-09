package com.microsoft.opentech.office.mail.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.mail.data.odata.EmailMessage;
import com.microsoft.opentech.office.mail.data.odata.Importance;
import com.microsoft.opentech.office.mail.data.odata.Item;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class EmailParser {

    private static final String SUBJECT_KEY = "Subject";

    private static final String ID_KEY = "Id";

    private static final String CHANGE_KEY_KEY = "ChangeKey";

    private static final String HAS_ATTACHMENTS_KEY = "HasAttachments";

    private static final String ATTACHMENTS_KEY = "Attachments@odata.navigationLink";

    private static final String DATE_TIME_CREATED_KEY = "DateTimeCreated";

    private static final String IMPORTANCE_KEY = "Importance";

    private static final String PARENT_FOLDER_KEY = "ParentFolderId";

    private static final String CONVERSATION_KEY = "ConversationIndex";

    private static final String DELIVERY_RECEIPT_REQUEST_KEY = "IsDeliveryReceiptRequested";

    private static final String READ_RECEIPT_REQUEST_KEY = "IsReadReceiptRequested";

    private static final String IS_READ_KEY = "IsRead";

    private static final String IS_DRAFT_KEY = "IsDraft";

    private static final String DATE_TIME_RECEIVED_KEY = "DateTimeReceived";

    private static final String DATE_TIME_SENT_KEY = "DateTimeSent";
    
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'", Locale.US); // TODO make sure 'Z' is constant literal

    private List<EmailMessage> mMessages;

    public EmailParser(List<ODataEntity> entities) throws ParseException {
        mMessages = new ArrayList<EmailMessage>();
        for (ODataEntity odataEntity : entities) {
            Entity entity = Entity.from(odataEntity).build();
            Item.Builder itemBuilder = new Item.Builder()
                             .setSubject((String)entity.get(SUBJECT_KEY))
                             .setId((String)entity.get(ID_KEY))
                             .setChangeKey((String)entity.get(CHANGE_KEY_KEY))
                             .setHasAttachments((Boolean)entity.get(HAS_ATTACHMENTS_KEY))
                             .setAttachmentsLink((String)entity.get(ATTACHMENTS_KEY))
                             .setDateTimeCreated(formatter.parse((String)entity.get(DATE_TIME_CREATED_KEY)))
                             .setImportance(Importance.fromString((String)entity.get(IMPORTANCE_KEY)));
            
            mMessages.add(new EmailMessage.Builder(itemBuilder)
                            .setParentFolder((String)entity.get(PARENT_FOLDER_KEY))
                            .setConversation((String)entity.get(CONVERSATION_KEY))
//                            .setIsDeliveryReceiptRequested((Boolean)entity.get(DELIVERY_RECEIPT_REQUEST_KEY))
                            .setIsReadReceiptRequested((Boolean)entity.get(READ_RECEIPT_REQUEST_KEY))
                            .setIsRead((Boolean)entity.get(IS_READ_KEY))
                            .setIsDraft((Boolean)entity.get(IS_DRAFT_KEY))
                            .setDateTimeReceived(formatter.parse((String)entity.get(DATE_TIME_RECEIVED_KEY)))
                            .setDateTimeSent(formatter.parse((String)entity.get(DATE_TIME_SENT_KEY)))
                            .build());
        }
    }

    public int getEmailsCount() {
        return mMessages.size();
    }

    public List<EmailMessage> getEmails() {
        return mMessages;
    }

}
