package com.example.sharepoint.client.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.auth.AuthType;

public class MetadataReceiveTask extends AsyncTask<Void, Void, String> {
    /**
     * 
     */
    private final OnOperaionExecutionListener listener;
    
    private final Context context;

    /**
     * @param mainActivity
     */
    public MetadataReceiveTask(OnOperaionExecutionListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ListsMetaODataOperation operMeta = new ListsMetaODataOperation(listener, AuthType.Office365, context);
            operMeta.execute();
            return operMeta.getResponse();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return null;
    }
}