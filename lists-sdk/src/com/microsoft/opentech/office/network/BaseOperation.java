/**
 *
 */
package com.microsoft.opentech.office.network;

import java.io.IOException;
import java.util.UUID;

import com.microsoft.opentech.office.odata.async.ICallback;

/**
 * Implements abstract operation. Provides common fields for all operations.
 *
 * @param <R> Operation result.
 */
public abstract class BaseOperation<R> {

    /**
     * Unique ID for operation.
     */
    protected String mOperationId = UUID.randomUUID().toString();

    /**
     * Error message if operation failed.
     */
    protected String mErrorMessage = null;

    /**
     * Value to be returned after operation execution is finished.
     */
    protected R mResult = null;

    /**
     * Listener to get notifications when operation will be completed.
     */
    protected ICallback<R> mListener;

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     */
    public BaseOperation(ICallback<R> listener) {
        mListener = listener;
    }

    /**
     * Executes the operation.
     * @throws IOException when an I/O error occurred during operation execution.
     * @throws RuntimeException when runtime error occurred during operation execution.
     */
    public abstract void execute() throws RuntimeException, IOException;

    /**
     * Retrieves listener.
     *
     * @return The listener.
     */
    protected ICallback<R> getListener() {
        return mListener;
    }

    /**
     * Retrieves the error message.
     *
     * @return Error Message or null.
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    /**
     * @return whether operation failed or not
     */
    public boolean hasError() {
        return mErrorMessage != null;
    }

    /**
     * Retrieves response.
     *
     * @return Server response or null.
     */
    public R getResult() {
        return mResult;
    }
}
