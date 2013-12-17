package com.example.sharepoint.client.network;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ListEntityCUDTask extends ODataAsyncTask<ODataEntity, Boolean> {

    public ListEntityCUDTask(OnOperaionExecutionListener listener, Context ctx) {
        super(listener, ctx);
    }

    @Override
    protected Boolean doInBackground(ODataEntity... params) {
        try {
            ListCreateEntityOperation createOper = new ListCreateEntityOperation(mListener, mContext, params[0]);
            createOper.execute();

            ODataComplexValue createdEntity = createOper.getResult();

            UpdateListItemOperation updateOper = new UpdateListItemOperation(mListener, mContext, createdEntity);
            updateOper.execute();

            ListDeleteEntityOperation deleteOper = new ListDeleteEntityOperation(mListener, mContext, updateOper.getResult());
            deleteOper.execute();

            return deleteOper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return false;
    }

}
