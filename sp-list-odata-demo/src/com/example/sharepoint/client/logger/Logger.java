package com.example.sharepoint.client.logger;

import com.example.sharepoint.client.Configuration;


/**
 * Helper class to log messages.
 */
public final class Logger {

    /**
     * The writer.
     */
    private static IWriter sWriter;

    /**
     * The formatter.
     */
    private static IFormatter sFormatter;

    /**
     * Static constructor.
     */
    static {
        sWriter = WriterFactory.getWriter();
        sFormatter = new SimpleExceptionFormatter();

        if (!Configuration.LOG_ENABLED) {
        	Logger.deleteLogs();
        }
    }

    /**
     * Initializes logger. Parameters won't be assigned if one of the parameters
     * have null value.
     *
     * @param initWriter Writer.
     * @param initFormatter Formatter.
     */
    public static void init(IWriter initWriter, IFormatter initFormatter) {
        if (initWriter == null || initFormatter == null) {
            return;
        }

        sWriter = initWriter;
        sFormatter = initFormatter;
    }

    /**
     * Retrieves all logged entities united to the string.
     *
     * @return Logged content.
     */
    public static synchronized String getContent() {
        return Configuration.LOG_ENABLED ? sWriter.getContent() : "";
    }

    /**
     * Logs exception with specified message.
     *
     * @param exception The exception.
     * @param friendlyMessage The friendly message.
     */
    public static void logApplicationException(Exception exception, String friendlyMessage) {
        if (Configuration.LOG_ENABLED) logEntry(exception, friendlyMessage);
    }

    /**
     * Logs message.
     *
     * @param friendlyMessage The friendly message.
     */
    public static void logMessage(String friendlyMessage) {
        if (Configuration.LOG_ENABLED) logEntry(null, friendlyMessage);
    }

    /**
     * Logs trace message.
     *
     * @param friendlyMessage The friendly message.
     */
    public static void logTraceMessage(String friendlyMessage) {
        if(!Configuration.TRACE_ENABLED || !Configuration.LOG_ENABLED) {
            return;
        }

        logEntry(null, friendlyMessage);
    }

    /**
     * Logs entry.
     *
     * @param exception The exception.
     * @param friendlyMessage The friendly message.
     */
    private static synchronized void logEntry(Exception exception, String friendlyMessage) {
        if (Configuration.LOG_ENABLED) sWriter.write(sFormatter.format(exception, friendlyMessage));
    }

    /**
     * Deletes logfile.
     */
    public static void deleteLogs() {
    	try {
        	if (!Configuration.LOG_ENABLED) {
        	    FileWriter.deleteLogFile();
            }
        } catch (final Exception e) {
        	e.printStackTrace();
        }
    }
}
