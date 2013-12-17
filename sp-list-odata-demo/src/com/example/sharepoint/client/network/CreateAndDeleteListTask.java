package com.example.sharepoint.client.network;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;

public class CreateAndDeleteListTask extends ODataAsyncTask<Void, Boolean> {

    public CreateAndDeleteListTask(OnOperaionExecutionListener listener, Context ctx) {
        super(listener, ctx);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            CreateListOperation createOper = new CreateListOperation(null, mContext);
            createOper.execute();

            RemoveListOperation deleteOper = new RemoveListOperation(mListener, mContext, createOper.getResult());
            deleteOper.execute();

            return deleteOper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return false;
    }
}
