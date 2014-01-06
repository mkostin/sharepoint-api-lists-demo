package com.example.office.lists.storage;


import com.example.office.OfficeApplication;
import com.example.office.lists.events.CredentialsStoredEvent;
import com.example.office.logger.Logger;
import com.example.office.storage.LocalPersistence;
import com.example.office.utils.Utility;
import com.microsoft.opentech.office.core.network.auth.ISharePointCredentials;

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
     */
    public static void storeCredentials(ISharePointCredentials credentials){
        try {
            LocalPersistence.writeObjectToFile(OfficeApplication.getContext(), credentials, AUTH_PREFERENCES_FILENAME);
            new CredentialsStoredEvent(credentials).submit();
        } catch (final Exception e) {
            Logger.logApplicationException(e, AuthPreferences.class.getSimpleName() + ".storeCredentials(): Error.");
            Utility.showAlertDialog(AuthPreferences.class.getSimpleName() + ".storeCredentials(): Failed. " + e.toString(), OfficeApplication.getContext());
        }
    }

    /**
     * Returns user {@linkplain ISharePointCredentials} from SharedPreferences.
     *
     * @return Initialized {@linkplain ISharePointCredentials} instance. In case of exception returns <code>null</code>.
     */
    public static ISharePointCredentials loadCredentials(){
            try{
            return (ISharePointCredentials) LocalPersistence.readObjectFromFile(OfficeApplication.getContext(), AUTH_PREFERENCES_FILENAME);
        } catch (final Exception e) {
            Logger.logApplicationException(e, AuthPreferences.class.getSimpleName() + ".loadCredentials(): Error.");
            Utility.showAlertDialog(AuthPreferences.class.getSimpleName() + ".loadCredentials(): Failed. " + e.toString(), OfficeApplication.getContext());
        }
        return null;
    }

    /**
     * Private constructor to prevent creating new instance of the class.
     */
    private AuthPreferences(){
    }
}
