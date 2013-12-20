package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.GetListItemsOperation;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;

public class ListReadTask extends ODataAsyncTask<String, ODataCollectionValue> {

    public ListReadTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataCollectionValue doInBackground(String... params) {
        try {
            GetListItemsOperation oper = new GetListItemsOperation(mListener, mContext, params[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return null;
    }

}
