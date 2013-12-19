package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.operations.ListsMetadataOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;

public class MetadataReceiveTask extends ODataAsyncTask<Void, String> {

    public MetadataReceiveTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ListsMetadataOperation operMeta = new ListsMetadataOperation(mListener, mContext);
            operMeta.execute();
            return operMeta.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return null;
    }
}