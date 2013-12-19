package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.RemoveListItemOperation;

public class ItemRemoveTask extends ODataAsyncTask<String, Boolean> {

    public ItemRemoveTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            RemoveListItemOperation oper = new RemoveListItemOperation(mListener, mContext, (String)params[0], Integer.valueOf((String)params[1]));
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return false;
    }

}
