package com.example.sharepoint.client.ui;

import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.network.tasks.ItemReadTask;
import com.microsoft.opentech.office.network.BaseOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.GetItemOperation;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;

public class ListItemActivity extends Activity implements OnOperaionExecutionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_item);

        Bundle extras = getIntent().getExtras();

        new ItemReadTask(this, this).execute(extras.getString("listGUID"), extras.getInt("itemId"));
    }

    @Override
    public void onExecutionComplete(final BaseOperation operation, final boolean executionResult) {
        runOnUiThread(new Runnable() {
            public void run() {
                ODataEntity result = ((GetItemOperation) operation).getResult();
                ODataComplexValue fieldContainer = result.getProperty("d").getComplexValue();
                showFields(fieldContainer);
            }
        });
    }

    private void showFields(ODataComplexValue value) {
        findViewById(R.id.get_fields_text_stub).setVisibility(View.GONE);
        TableLayout table = (TableLayout) findViewById(R.id.item_fields_table);
        table.setVisibility(View.VISIBLE);

        Iterator<ODataProperty> iterator = value.iterator();
        while (iterator.hasNext()) {
            ODataProperty next = iterator.next();
            String title = next.getName(), text;
            if (next.hasNullValue()) {
                text = "(null)";
            } else if (next.hasCollectionValue()) {
                text = "(collection)";
            } else if (next.hasComplexValue()) {
                text = "(complex value)";
            } else {
                text = next.getPrimitiveValue().toString();
            }

            TableRow row = new TableRow(this);
            TextView titleView = new TextView(this);
            titleView.setText(title);
            row.addView(titleView);

            TextView textView = new TextView(this);
            textView.setText(text);
            row.addView(textView);

            table.addView(row);
        }
    }

}
