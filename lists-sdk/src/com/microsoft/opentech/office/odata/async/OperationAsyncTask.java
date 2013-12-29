package com.microsoft.opentech.office.odata.async;

import com.microsoft.opentech.office.network.BaseOperation;

import android.os.AsyncTask;

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
     * @param future Future to set result of operation.
     */
    public OperationAsyncTask(OfficeFuture<R> future) throws IllegalArgumentException {
        if (future == null) {
            throw new IllegalArgumentException("future cannot be null");
        }
        
        mFuture = future;
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
