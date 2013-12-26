package com.example.sharepoint.client.ui;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.network.tasks.ItemReadTask;
import com.microsoft.opentech.office.network.BaseOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.lists.GetItemOperation;
import com.microsoft.opentech.office.odata.Entity;

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
                Entity result = ((GetItemOperation) operation).getResult();
                showFields(result);
            }
        });
    }

    private void showFields(Entity value) {
        findViewById(R.id.get_fields_text_stub).setVisibility(View.GONE);
        TableLayout table = (TableLayout) findViewById(R.id.item_fields_table);
        table.setVisibility(View.VISIBLE);

        Iterator<Pair<String, Object>> iterator = value.iterator();
        while (iterator.hasNext()) {
            Pair<String, Object> next = iterator.next();
            String title = next.first, text;
            if (next.second == null) {
                text = "(null)";
            } else if (next.second instanceof List) {
                text = "(collection)";
            } else if (next.second instanceof Entity) {
                text = "(complex value)";
            } else {
                text = next.second.toString();
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
