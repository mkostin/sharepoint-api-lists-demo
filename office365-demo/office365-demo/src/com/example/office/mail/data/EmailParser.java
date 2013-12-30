package com.example.office.mail.data;

import java.util.List;

import com.example.office.mail.data.odata.EmailMessage;
import com.example.office.mail.data.odata.MessagesDocument;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Provides methods for parsing emails from json
 */
public class EmailParser {

    /**
     * Represents object retrieved from server
     */
    private MessagesDocument document;

    /**
     * Emails array
     */
    private List<EmailMessage> emails;

    /**
     * Creates {@link EmailParser} from raw JSON string
     * 
     * @param json JSON string retrieved from server
     * @throws JSONSyntaxException Thrown when json is incorrect
     */
    public EmailParser(String json) throws JsonSyntaxException {
        Gson gson = new GsonBuilder().setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ssZ").create();
        document = gson.fromJson(json, MessagesDocument.class);
        emails = document.getMessages();
    }

    /**
     * Gets total number of emails
     * 
     * @return Number of emails
     */
    public int getEmailsCount() {
        if (emails == null) return 0;
        return emails.size();
    }

    /**
     * Gets email by index
     * 
     * @param index Index of email in array
     * @return Email with given index from array
     * @throws ArrayIndexOutOfBoundsException Thrown if email with such index is not present in array
     */
    public EmailMessage getEmail(int index) throws ArrayIndexOutOfBoundsException {
        return emails.get(index);
    }

}
