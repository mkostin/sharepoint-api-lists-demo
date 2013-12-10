package com.example.sharepoint.client.network;

import android.content.Context;
import android.os.AsyncTask;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ListCreateEntityTask extends AsyncTask<ODataEntity, Void, Boolean> {

    private OnOperaionExecutionListener listener;

    private Context context;

    public ListCreateEntityTask(OnOperaionExecutionListener listener, Context ctx) {
        this.listener = listener;
        this.context = ctx;
    }

    @Override
    protected Boolean doInBackground(ODataEntity... params) {
        try {
            ListCreateEntityOperation oper = new ListCreateEntityOperation(listener, AuthType.Office365, context, params[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return false;
    }

}
