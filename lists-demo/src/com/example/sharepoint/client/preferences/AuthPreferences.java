package com.example.sharepoint.client.preferences;

import android.content.Context;

import com.example.sharepoint.client.ListsDemoApplication;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.utils.LocalPersistence;
import com.example.sharepoint.client.utils.Utility;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;

/**
 * Implements authentication preferences.
 */
public class AuthPreferences {

    /**
     * The name of the file where the authentication dta is saved.
     */
    private static final String AUTH_PREFERENCES_FILENAME = "com.example.sharepoint.client";

    /**
     * Options preferences file.
     */
    protected static final String OPTIONS_PREFERENCES_FILENAME = "com.example.sharepoint.client.options";

    /**
     * Stores user {@linkplain ISharePointCredentials} into SharedPreferences.
     *
     * @param credentials Authentication credentials.
     * @param contex Application context.
     */
    public static void storeCredentials(ISharePointCredentials credentials, Context contex){
        try {
            LocalPersistence.writeObjectToFile(contex, credentials, AUTH_PREFERENCES_FILENAME);
        } catch (final Exception e) {
            Logger.logApplicationException(e, AuthPreferences.class.getSimpleName() + ".storeCredentials(): Error.");
            Utility.showAlertDialog(AuthPreferences.class.getSimpleName() + ".storeCredentials(): Failed. " + e.toString(), ListsDemoApplication.getContext());
        }
    }

    /**
     * Returns user {@linkplain ISharePointCredentials} from SharedPreferences.
     *
     * @param contex Application context.
     *
     * @return Initialized {@linkplain ISharePointCredentials} instance. In case of exception returns <code>null</code>.
     */
    public static ISharePointCredentials loadCredentials(Context contex){
        try {
            ISharePointCredentials credentials = null;
            try{
                credentials = (ISharePointCredentials) LocalPersistence.readObjectFromFile(contex, AUTH_PREFERENCES_FILENAME);
            } catch(Exception e) {
                Utility.showAlertDialog(e.toString(), ListsDemoApplication.getContext());
            }
            return credentials;
        } catch (final Exception e) {
            Utility.showAlertDialog(AuthPreferences.class.getSimpleName() + ".loadCredentials(): Failed. " + e.toString(), ListsDemoApplication.getContext());
        }
        return null;
    }

    /**
     * Private constructor to prevent creating new instance of the class.
     */
    private AuthPreferences(){
    }
}
