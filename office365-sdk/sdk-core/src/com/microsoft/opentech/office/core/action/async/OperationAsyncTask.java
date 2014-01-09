package com.microsoft.opentech.office.core.action.async;

import java.util.concurrent.Future;

import android.os.AsyncTask;

import com.microsoft.opentech.office.core.action.BaseOperation;

/**
 * Runs given operation asynchronously and sets its result to given {@link OfficeFuture}
 *
 * @param <R> Result of operation.
 */
public class OperationAsyncTask<R> extends AsyncTask<BaseOperation<R>, Void, Void> {

    /**
     * Future to set result of operation.
     */
    private OfficeFuture<R> mFuture;

    /**
     * Creates a new instance of {@link OperationAsyncTask}
     *
     * @param callback Callback to be invoked when task is finished.
     */
    public OperationAsyncTask(IOperationCallback<R> callback) throws IllegalArgumentException {
        if (callback == null) {
            throw new IllegalStateException("Callback cannot be null");
        }

        mFuture = new OfficeFuture<R>(callback);
    }

    /**
     * Returns {@link Future} of this async task.
     *
     * @return {@link Future} of this async task.
     */
    public Future<R> getFuture() {
        return mFuture;
    }

    @Override
    protected Void doInBackground(BaseOperation<R>... operation) {
        try {
            if (operation == null || operation.length != 1 || operation[0] == null) {
                mFuture.triggerError(new IllegalArgumentException("operation cannot be null"));
                return null;
            }

            BaseOperation<R> oper = operation[0];
            mFuture.setResult(oper.execute());

        } catch (Throwable t) {
            mFuture.triggerError(t);
        }

        return null;
    }
}
