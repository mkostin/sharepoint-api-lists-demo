package com.example.sharepoint.client.network.tasks;

import java.util.List;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.GetListsOperation;

public class ListsReceiveTask extends ODataAsyncTask<Void, List<Object>> {
    public ListsReceiveTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected List<Object> doInBackground(Void... params) {
        try {
            GetListsOperation operLists = new GetListsOperation(mListener, mContext);
            operLists.execute();
            return operLists.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        return null;
    }
}