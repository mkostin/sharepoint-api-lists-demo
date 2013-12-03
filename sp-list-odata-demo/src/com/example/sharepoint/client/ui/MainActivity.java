package com.example.sharepoint.client.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.ListsMetaODataOperation;
import com.example.sharepoint.client.network.ListsOperation;
import com.example.sharepoint.client.network.auth.AuthType;

/**
 * Sample activity displaying request results.
 */
public class MainActivity extends Activity implements OnOperaionExecutionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ListsReceiveTask().execute();
        new MetadataReceiveTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onExecutionComplete(final BaseOperation operation, boolean executionResult) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    TextView view = null;
                    if(operation instanceof ListsOperation) {
                        view = ((TextView) MainActivity.this.findViewById(R.id.response_view_lists));
                    } else if(operation instanceof ListsMetaODataOperation) {
                        view = ((TextView) MainActivity.this.findViewById(R.id.response_view_metadata));
                    }
                    if(view != null) {
                        view.setText(operation.getResponse());
                    }
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
                }
            }
        });
    }

    public class MetadataReceiveTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ListsMetaODataOperation operMeta = new ListsMetaODataOperation(MainActivity.this, AuthType.Office365, MainActivity.this);
                operMeta.execute();
                return operMeta.getResponse();
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
            }
            return null;
        }
    }

    public class ListsReceiveTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ListsOperation operLists = new ListsOperation(MainActivity.this, AuthType.Office365, MainActivity.this);
                operLists.execute();
                return operLists.getResponse();
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
            }
            return null;
        }
    }
}
