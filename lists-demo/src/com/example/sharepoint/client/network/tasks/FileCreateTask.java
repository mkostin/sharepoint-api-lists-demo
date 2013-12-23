package com.example.sharepoint.client.network.tasks;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.files.CreateFileOperation;

/**
 * Creates a file based on provided library name, file name and file contents.
 */
public class FileCreateTask extends ODataAsyncTask<Object, String> {

    /**
     * Default constructor
     *
     * @param listener Callback object to handle execution results.
     * @param context Application context.
     */
    public FileCreateTask(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    /**
     * Params: </br>
     * 0 - String: library mane </br>
     * 1 - String: file name </br>
     * 2 - byte[]: file data
     */
    @Override
    protected String doInBackground(Object... params) {
        try {
            CreateFileOperation oper = new CreateFileOperation(this.mListener, this.mContext, (String) params[0], (String) params[1], (byte[]) params[2]);
            oper.execute();
            return oper.getResult();
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
        }

        return null;
    }
}
