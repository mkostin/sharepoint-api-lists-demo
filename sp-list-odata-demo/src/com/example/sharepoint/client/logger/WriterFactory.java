package com.example.sharepoint.client.logger;

/**
 * Implements Factory pattern to return specified writer for the Logger.
 */
public class WriterFactory {

    /**
     * Private constructor to prevent creating an instance of the class.
     */
    private WriterFactory() {
    }

    /**
     * Retrieves more appropriated writer.
     * 
     * @return Writer instance.
     */
    public static IWriter getWriter() {
        return FileWriter.getInstance();
    }
}
