package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.CreateListItemOperation;
import com.microsoft.opentech.office.odata.EntityBuilder;
import com.msopentech.odatajclient.engine.data.ODataEntity;


public class ItemCreateTask extends ODataAsyncTask<String, ODataEntity> {

    public ItemCreateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataEntity doInBackground(String... params) {
        try {
            EntityBuilder builder = EntityBuilder.newEntity(null).set("Title", "test"); // type is unknown, it will be set during operation execution
            CreateListItemOperation oper = new CreateListItemOperation(mListener, mContext, params[0], builder);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return null;
    }
}
