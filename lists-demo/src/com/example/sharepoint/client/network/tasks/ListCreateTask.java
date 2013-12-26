package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.CreateListOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.Entity.Builder;
import com.msopentech.odatajclient.engine.data.ODataEntity;

public class ListCreateTask extends ODataAsyncTask<Void, Entity> {
    public ListCreateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }
    
    @Override
    protected Entity doInBackground(Void... params) {
        try {
            Builder builder = new Entity.Builder("SP.List").set("BaseTemplate", 100).
                    set("Title", "List, created using API").set("Description", "SDK Playground");
            CreateListOperation oper = new CreateListOperation(this.mListener, this.mContext, builder);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }
        
        return null;
    }
}
