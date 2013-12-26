package com.example.sharepoint.client.network.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.opentech.office.odata.async.ICallback;

public abstract class ODataAsyncTask<IN, OUT> extends AsyncTask<IN, Void, OUT> {

    protected final ICallback<OUT> mListener;
    
    protected final Context mContext;
    
    public ODataAsyncTask(ICallback<OUT> listener, Context context) {
        mListener = listener;
        mContext = context;
    }
}
