package com.example.sharepoint.client.preferences;

import com.example.sharepoint.client.ListsDemoApplication;
import com.example.sharepoint.client.utils.LocalPersistence;
import com.example.sharepoint.client.utils.Utility;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;

/**
 * Implements authentication preferences.
 */
public class AuthPreferences {

    /**
     * The name of the file where the bookmarks are saved.
     */
    private static final String FILENAME = "com.example.sharepoint.client";

    /**
     * Stores user {@linkplain ISharePointCredentials} into SharedPreferences.
     *
     * @param credentials Authentication credentials.
     */
    public static void storeCredentials(ISharePointCredentials credentials){
        try {
            LocalPersistence.writeObjectToFile(ListsDemoApplication.getAppContext(), credentials, FILENAME);
        } catch (final Exception e) {
            Utility.showAlertDialog(AuthPreferences.class.getSimpleName() + ".storeCredentials(): Failed. " + e.toString(), ListsDemoApplication.getAppContext());
        }
    }

    /**
     * Returns user {@linkplain ISharePointCredentials} from SharedPreferences.
     *
     * @return Initialized {@linkplain ISharePointCredentials} instance. In case of exception returns <code>null</code>.
     */
    public static ISharePointCredentials loadCredentials(){
        try {
            ISharePointCredentials credentials = null;
            try{
                credentials = (ISharePointCredentials) LocalPersistence.readObjectFromFile(ListsDemoApplication.getAppContext(), FILENAME);
            } catch(Exception e) {
                Utility.showAlertDialog(e.toString(), ListsDemoApplication.getAppContext());
            }
            return credentials;
        } catch (final Exception e) {
            Utility.showAlertDialog(AuthPreferences.class.getSimpleName() + ".loadCredentials(): Failed. " + e.toString(), ListsDemoApplication.getAppContext());
        }
        return null;
    }

    /**
     * Private constructor to prevent creating new instance of the class.
     */
    private AuthPreferences(){
    }
}
