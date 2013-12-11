package com.example.sharepoint.client.ui;

import android.app.Activity;
import android.widget.Toast;

import com.example.sharepoint.client.network.BaseOperation;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.ListReadOperation;

public class ListReadOperationExecutionListener implements OnOperaionExecutionListener {

    private Activity context;

    public ListReadOperationExecutionListener(Activity ctx) {
        context = ctx;
    }

    @Override
    public void onExecutionComplete(final BaseOperation operation, boolean executionResult) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context, "Items count: " + ((ListReadOperation) operation).getItemsCount(), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

}
