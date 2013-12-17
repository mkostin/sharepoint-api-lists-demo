/**
 *
 */
package com.example.sharepoint.client.network;

import java.util.UUID;

/**
 * Implements abstract operation. Provides common fields for all operations.
 *
 * @param <R> Operation result.
 */
public abstract class BaseOperation<R> {

    /**
     * Implements listener to notify operation creator when operation is completed.
     */
    public interface OnOperaionExecutionListener {

        /**
         * Notifies operation creator that the operation is completed.
         *
         * @param operation The operation.
         * @param executionResult True if operation was executed successfully, otherwise false.
         */
        public void onExecutionComplete(BaseOperation operation, boolean executionResult);
    }

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
    protected OnOperaionExecutionListener mListener;

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     */
    public BaseOperation(OnOperaionExecutionListener listener) {
        mListener = listener;
    }

    /**
     * Executes the operation.
     */
    abstract void execute();

    /**
     * Retrieves listener.
     *
     * @return The listener.
     */
    OnOperaionExecutionListener getListener() {
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
