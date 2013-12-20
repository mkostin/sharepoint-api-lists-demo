package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.CreateListOperation;
import com.microsoft.opentech.office.odata.EntityBuilder;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ListCreateTask extends ODataAsyncTask<Void, ODataEntity> {
    public ListCreateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }
    
    @Override
    protected ODataEntity doInBackground(Void... params) {
        try {
            EntityBuilder builder = EntityBuilder.newEntity("SP.List").set("BaseTemplate", 100).
                    set("Title", "List, created using API").set("Description", "SDK Playground");
            CreateListOperation oper = new CreateListOperation(this.mListener, this.mContext, builder.build().asODataEntity());
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        
        return null;
    }
}
