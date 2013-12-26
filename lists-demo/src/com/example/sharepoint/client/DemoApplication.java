package com.example.sharepoint.client;

import android.app.Application;
import android.content.Context;

import com.example.sharepoint.client.event.bus.EventBus;
import com.example.sharepoint.client.utils.Utility;

/**
 * The application class, keeps stored application context.
 */
public class DemoApplication extends Application {
    /**
     * A stored application context.
     */
    private static Context sContext;

    public void onCreate() {
        try {
            super.onCreate();
            DemoApplication.sContext = getApplicationContext();
            EventBus.init();
        } catch (Exception e) {
            Utility.showAlertDialog(DemoApplication.class.getSimpleName() + ".onCreate(): Failed. " + e.toString(), DemoApplication.this);
        }
    }

    /**
     * Returns the stored application context.
     *
     * @return The application context.
     */
    public static Context getContext() {
        return DemoApplication.sContext;
    }
}
