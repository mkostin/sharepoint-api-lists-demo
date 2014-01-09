package com.microsoft.opentech.office.mail.data.odata;

import java.util.List;

/**
 * Represents messages document
 */
public class MessagesDocument {

    /**
     * Contains link to context for current document.
     */
    private String contextLink;

    /**
     * Contains collection of emails returned by endpoint.
     */
    private List<EmailMessage> messages;

    /**
     * Default constructor.
     */
    public MessagesDocument() {}

    /**
     * Gets link to context for current document.
     * 
     * @return Link to context for current document.
     */
    public String getMetadataLink() {
        return contextLink;
    }

    /**
     * Sets link to context for current document.
     * 
     * @param value New link value.
     */
    public void setMetadataLink(String value) {
        contextLink = value;
    }

    /**
     * Gets collection of emails returned by endpoint.
     * 
     * @return Collection of emails returned by endpoint.
     */
    public List<EmailMessage> getMessages() {
        return messages;
    }

    /**
     * Sets messages collection for current {@link MessagesDocument} instance.
     * 
     * @param value New messages collection value.
     */
    public void setMessages(List<EmailMessage> value) {
        messages = value;
    }
}
