package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.operations.ListsMetadataOperation;
import com.microsoft.opentech.office.odata.async.ICallback;

public class MetadataReceiveTask extends ODataAsyncTask<Void, String> {

    public MetadataReceiveTask(ICallback<String> listener, Context context) {
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