package com.example.sharepoint.client.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.example.sharepoint.client.ListsDemoApplication;
import com.example.sharepoint.client.R;

/**
 * Implements common utility methods.
 */
public class Utility {

    /**
     * Shows alert dialog with provided message.
     *
     * @param error Error message to be displayed.
     * @param context Application context.
     */
    public static void showAlertDialog(String error, Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(ListsDemoApplication.getAppContext().getResources().getString(R.string.alert_dialog_title));

            builder.setMessage(error);
            builder.setPositiveButton(ListsDemoApplication.getAppContext().getResources().getString(R.string.alert_dialog_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (final Exception e) {
            Log.d(Utility.class.getSimpleName(), "showAlertDialog(): Failed.", e);
        }
    }

    /**
     * Shows a toast notification.
     *
     * @param text Message to be displayed.
     */
    public static void showToastNotification(String text) {
        try {
            Toast.makeText(ListsDemoApplication.getAppContext(), text, Toast.LENGTH_LONG).show();
        } catch (final Exception e) {
            Log.d(Utility.class.getSimpleName(), "showToastNotification(): Failed.", e);
        }
    }
}
