package com.example.sharepoint.client.ui;

import android.app.Activity;
import android.widget.Toast;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.RemoveListOperation;

public class ListCreationExecutionListener implements OnOperaionExecutionListener {

    private Activity context;

    public ListCreationExecutionListener(Activity ctx) {
        context = ctx;
    }

    @Override
    public void onExecutionComplete(final BaseOperation operation, boolean executionResult) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Toast.makeText(context, ((RemoveListOperation) operation).getResult() ? "List created and removed successfully"
                            : "Fail on list creation or removing", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
                }
            }
        });
    }

}
