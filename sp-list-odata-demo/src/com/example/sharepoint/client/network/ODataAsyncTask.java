package com.example.sharepoint.client.network;

import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;

import android.content.Context;
import android.os.AsyncTask;

public abstract class ODataAsyncTask<IN, OUT> extends AsyncTask<IN, Void, OUT> {

    protected final OnOperaionExecutionListener mListener;
    
    protected final Context mContext;
    
    public ODataAsyncTask(OnOperaionExecutionListener listener, Context context) {
        mListener = listener;
        mContext = context;
    }
}
