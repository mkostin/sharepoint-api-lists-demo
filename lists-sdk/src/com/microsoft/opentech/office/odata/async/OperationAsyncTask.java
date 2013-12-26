package com.microsoft.opentech.office.odata.async;

import com.microsoft.opentech.office.network.BaseOperation;

import android.os.AsyncTask;

public class OperationAsyncTask<R> extends AsyncTask<BaseOperation<R>, Void, Void> {

    private OfficeFuture<R> mFuture;

    public OperationAsyncTask(OfficeFuture<R> future) {
        if (future == null) {
            mFuture = new OfficeFuture<R>();
        } else {
            mFuture = future;
        }
    }

    @Override
    protected Void doInBackground(BaseOperation<R>... operation) {
        try {
            if (operation == null || operation.length != 1 || operation[0] == null) {
                mFuture.triggerError(new IllegalArgumentException("operation cannot be null"));
                return null;
            }

            BaseOperation<R> oper = operation[0];
            oper.execute();
            mFuture.setResult(oper.getResult());

        } catch (Throwable t) {
            mFuture.triggerError(t);
        }

        return null;
    }
}
