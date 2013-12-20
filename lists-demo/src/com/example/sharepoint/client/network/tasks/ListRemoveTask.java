package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.RemoveListOperation;

public class ListRemoveTask extends ODataAsyncTask<String, Boolean> {

    public ListRemoveTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Boolean doInBackground(String... guid) {
        try {
            RemoveListOperation oper = new RemoveListOperation(mListener, mContext, guid[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return false;
    }

}
