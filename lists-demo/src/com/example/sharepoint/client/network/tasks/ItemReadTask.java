package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.GetItemOperation;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ItemReadTask extends ODataAsyncTask<Object, ODataEntity> {
    public ItemReadTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }
    
    @Override
    protected ODataEntity doInBackground(Object... params) {
        try {
            GetItemOperation oper = new GetItemOperation(mListener, mContext, (String)params[0], (Integer)params[1]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        
        return null;
    }
}
