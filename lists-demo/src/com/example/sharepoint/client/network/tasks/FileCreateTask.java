package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.files.CreateFileOperation;

public class FileCreateTask extends ODataAsyncTask<Object, String> {
    public FileCreateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            CreateFileOperation oper = new CreateFileOperation(this.mListener, this.mContext, "Shared%20Documents", "image.txt", (byte[]) params[0]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return null;
    }
}
