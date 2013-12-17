package com.example.sharepoint.client.network;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;

public class ListsReceiveTask extends ODataAsyncTask<Void, ODataCollectionValue> {
    public ListsReceiveTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataCollectionValue doInBackground(Void... params) {
        try {
            ListsOperation operLists = new ListsOperation(this.mListener, mContext);
            operLists.execute();
            return operLists.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return null;
    }
}