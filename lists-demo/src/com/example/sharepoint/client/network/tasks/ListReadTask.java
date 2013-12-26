package com.example.sharepoint.client.network.tasks;

import java.util.List;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.GetListItemsOperation;

public class ListReadTask extends ODataAsyncTask<String, List<Object>> {

    public ListReadTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected List<Object> doInBackground(String... params) {
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
