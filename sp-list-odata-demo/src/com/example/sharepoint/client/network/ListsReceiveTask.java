package com.example.sharepoint.client.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;

public class ListsReceiveTask extends AsyncTask<Void, Void, String> {
    /**
     * 
     */
    private final OnOperaionExecutionListener listener;
    
    private final Context context;

    /**
     * @param listener
     */
    public ListsReceiveTask(OnOperaionExecutionListener listener, Context ctx) {
        this.listener = listener;
        context = ctx;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ListsOperation operLists = new ListsOperation(this.listener, Constants.AUTH_TYPE, context);
            operLists.execute();
            return operLists.getResponse();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return null;
    }
}