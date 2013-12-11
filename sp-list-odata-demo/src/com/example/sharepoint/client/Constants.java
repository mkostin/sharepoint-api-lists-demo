package com.example.sharepoint.client;

import com.example.sharepoint.client.network.auth.AuthType;

/**
 * Stores application public constants such as endpoint URLs, default values, etc.
 */
public class Constants {

    /**
     * Base URL to access SharePoint and all related services.
     */
    public static final String SP_BASE_URL = "https://microsoft.sharepoint.com/teams/MSOpenTech-CLA/testsite/_api/";

    /**
     * Endpoint to retrieve metadata. Usually requires adding "$metadata" to the base URL.
     */
    public static final String SP_METADATA = SP_BASE_URL;

    /**
     * Endpoint to retrieve Lists.
     */
    public static final String SP_LISTS_URL = SP_BASE_URL + "web/lists";

    /**
     * Application logging TAG.
     */
    public static final String APP_TAG = "Office365ListsDemo";

    /**
     * NTLM authentication required cookie.
     */
    public static final String COOKIE_FED_AUTH = "FedAuth=cookie-value-1";

    /**
     * NTLM authentication required cookie.
     */
    public static final String COOKIE_RT_FA = "rtFa=cookie-value-2";

    /**
     * User name for authentication (whatever type is used).
     * Valid formats: <br/>
     * - domain\\username <br/>
     * - username@domain.com
     */
    public static final String USERNAME = "name@domain";

    /**
     * Password for authentication (whatever type is used).
     */
    public static final String PASSWORD = "password";

    public static AuthType AUTH_TYPE = AuthType.Office365;

}
