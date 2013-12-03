package com.example.sharepoint.client.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.example.com.example.odata_test_client.R;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.BaseOperation;
import com.example.sharepoint.client.network.BaseOperation.OnOperaionExecutionListener;
import com.example.sharepoint.client.network.ListsRequestHttpOperation;

/**
 * Sample activity displaying request results.
 */
public class MainActivity extends Activity implements OnOperaionExecutionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ListsReceiveTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onExecutionComplete(BaseOperation operation, boolean executionResult) {
        final ListsRequestHttpOperation oper = (ListsRequestHttpOperation) operation;
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    ((TextView) MainActivity.this.findViewById(R.id.response_view)).setText(oper.getResult());
                } catch (Exception e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + ".run(): Error.");
                }
            }
        });
    }

    public class ListsReceiveTask extends AsyncTask<Void, Void, String> {

        Context context;

        public ListsReceiveTask(Context ctx) {
            super();
            context = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ListsRequestHttpOperation oper = new ListsRequestHttpOperation(MainActivity.this, context);
                oper.execute();
                return oper.getResult();
            } catch (Exception e) {
                Logger.logApplicationException(e, getClass().getSimpleName() + ".doInBackground(): Error.");
            }

            return null;
        }

    }
}
