package com.example.sharepoint.client;

import android.app.Application;
import android.content.Context;

import com.example.sharepoint.client.utils.Utility;

/**
 * The application class, keeps stored application context.
 */
public class ListsDemoApplication extends Application {
    /**
     * A stored application context.
     */
    private static Context mContext;

    public void onCreate() {
        try {
            super.onCreate();
            ListsDemoApplication.mContext = getApplicationContext();
        } catch (Exception e) {
            Utility.showAlertDialog(ListsDemoApplication.class.getSimpleName() + ".onCreate(): Failed. " + e.toString(), ListsDemoApplication.this);
        }
    }

    /**
     * Returns the stored application context.
     *
     * @return The application context.
     */
    public static Context getContext() {
        return ListsDemoApplication.mContext;
    }
}
