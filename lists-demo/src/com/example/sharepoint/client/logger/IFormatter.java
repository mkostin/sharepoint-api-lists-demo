package com.example.sharepoint.client.logger;

/**
 * Common interface for all formatters. Declared common functions.
 */
public interface IFormatter {

    /**
     * Formats exception with specified message to log it.
     * 
     * @param exception Exception object.
     * @param message Friendly message.
     * 
     * @return Formatted string.
     */
    public String format(Exception exception, String message);
}
