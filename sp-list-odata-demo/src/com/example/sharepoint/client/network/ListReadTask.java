package com.example.sharepoint.client.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.auth.AuthType;

public class ListReadTask extends AsyncTask<String, Void, Integer> {

    private final OnOperaionExecutionListener listener;
    
    private final Context context;
    
    public ListReadTask(OnOperaionExecutionListener listener, Context ctx) {
        this.listener = listener;
        context = ctx;
    }
    
    @Override
    protected Integer doInBackground(String... params) {
        try {
            ListReadOperation oper = new ListReadOperation(listener, AuthType.Office365, context, params[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        
        return 0;
    }

}
