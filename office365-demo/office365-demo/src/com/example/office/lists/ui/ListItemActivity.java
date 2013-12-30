package com.example.office.lists.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.ui.BaseActivity;

public class ListItemActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_list_item_activity);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreate(): Error.");
        }
    }

}
