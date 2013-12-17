package com.example.sharepoint.client.network;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ListReadTask extends ODataAsyncTask<String, ODataEntity> {

    public ListReadTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataEntity doInBackground(String... params) {
        try {
            ListReadOperation oper = new ListReadOperation(mListener, mContext, params[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return null;
    }

}
