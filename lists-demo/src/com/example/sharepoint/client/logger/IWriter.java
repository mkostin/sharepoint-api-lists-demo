package com.example.sharepoint.client.logger;

/**
 * Common interface for all log writers.
 */
public interface IWriter {

    /**
     * Writes string to the log.
     * 
     * @param stringToWrite String to log.
     */
    void write(String stringToWrite);

    /**
     * Retrieves all logged entities united to the string.
     * 
     * @return String with logged content.
     */
    String getContent();
}
