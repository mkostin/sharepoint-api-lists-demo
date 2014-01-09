package com.microsoft.opentech.office.core.action.async;

/**
 * Provides ability to user to set listeners for operation completion.
 */
public interface IOperationCallback<RESULT> {

    /**
     * Invoked when operation finished successfully.
     *
     * @param result Operation result.
     */
    public void onDone(RESULT result);

    /**
     * Invoked when an error occurred during operation execution.
     *
     * @param error Error cause.
     */
    public void onError(Throwable error);

}
