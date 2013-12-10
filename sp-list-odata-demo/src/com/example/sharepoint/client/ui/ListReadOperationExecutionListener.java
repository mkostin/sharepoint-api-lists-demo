package com.example.sharepoint.client.ui;

import android.content.Context;
import android.widget.Toast;

import com.example.sharepoint.client.network.BaseOperation;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.ListReadOperation;

public class ListReadOperationExecutionListener implements OnOperaionExecutionListener {

    private Context context;
    
    public ListReadOperationExecutionListener(Context ctx) {
        context = ctx;
    }

    @Override
    public void onExecutionComplete(BaseOperation operation, boolean executionResult) {
         Toast.makeText(context, "Fields count: " + String.valueOf(((ListReadOperation)operation).getResult()), Toast.LENGTH_LONG).show();
    }

}
