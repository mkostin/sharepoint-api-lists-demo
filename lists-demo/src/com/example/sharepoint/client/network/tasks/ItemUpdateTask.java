package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.UpdateListItemOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.IEntityBuilder;

public class ItemUpdateTask extends ODataAsyncTask<Object, Boolean> {
    public ItemUpdateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    /**
     * Params: </br> 0 - String: list GUID </br> 1 - Integer: item id </br> 2 - Builder
     */
    @Override
    protected Boolean doInBackground(Object... params) {
        try {
            UpdateListItemOperation oper = new UpdateListItemOperation(mListener, mContext, (String) params[0], (Integer) params[1],
                    (IEntityBuilder<Entity>) params[2]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return false;
    }
}
