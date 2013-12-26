package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.CreateListItemOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.Entity.Builder;


public class ItemCreateTask extends ODataAsyncTask<Object, Entity> {

    public ItemCreateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected Entity doInBackground(Object... params) {
        try {
            CreateListItemOperation oper = new CreateListItemOperation(mListener, mContext, (String) params[0], (Builder) params[1]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return null;
    }
}
