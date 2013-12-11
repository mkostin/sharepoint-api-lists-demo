package com.example.sharepoint.client.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ListUpdateTask extends AsyncTask<ODataEntity, Void, Boolean> {
    
    private final OnOperaionExecutionListener listener;
    
    private final Context context;
    
    public ListUpdateTask(OnOperaionExecutionListener listener, Context ctx) {
        this.listener = listener;
        context = ctx;
    }

    @Override
    protected Boolean doInBackground(ODataEntity... params) {
        try {
            ListUpdateOperation oper = new ListUpdateOperation(listener, Constants.AUTH_TYPE, context, params[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        
        return false;
    }

}
