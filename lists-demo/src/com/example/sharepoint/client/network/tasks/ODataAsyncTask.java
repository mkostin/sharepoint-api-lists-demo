package com.example.sharepoint.client.network.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;

public abstract class ODataAsyncTask<IN, OUT> extends AsyncTask<IN, Void, OUT> {

    protected final OnOperaionExecutionListener mListener;
    
    protected final Context mContext;
    
    public ODataAsyncTask(OnOperaionExecutionListener listener, Context context) {
        mListener = listener;
        mContext = context;
    }
}
