package com.example.sharepoint.client;


/**
 * Stores application public constants such as endpoint URLs, default values, etc.
 */
public class Constants {

    //public static final String SP_SITE_URL = "http://sphvm-7052/";
    public static final String SP_SITE_URL = "https://microsoft.sharepoint.com/teams/MSOpenTech-CLA/testsite/";
    //public static final String SP_SITE_URL = "https://akvelon.sharepoint.com/sites/Sandbox/";

    /**
     * Base URL to access SharePoint and all related services.
     */
    public static final String SP_BASE_URL = SP_SITE_URL + "_api/";

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
    public static final String COOKIE_FED_AUTH = "FedAuth=77u/PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U1A+RmFsc2UsMGguZnxtZW1iZXJzaGlwfDEwMDM3ZmZlODc5NDU4NjJAbGl2ZS5jb20sMCMuZnxtZW1iZXJzaGlwfHYtbWF4a29zQG1pY3Jvc29mdC5jb20sMTMwMzIzNTQ2NDQ0MDY4NTk0LEZhbHNlLEcrSmQ2VFNYOVExYTZJTkdyODlwZHlXRmkxcnBCSm5OUWhnRGJzc1A5QUVhUDRJQTRLQzlzOXh0TXQvVWV1OUtSNkdWdzVJNWhFcVU2Kzk3V2NwMlNVRStoT3BkYkpRdzFJMi8vdXpGd2VIV1BCRm1kSFd4K3RUemRNb2k2YlRvdXRkOS9OWDc5cDUzUkk4RGNseE95L3ZKeU1WQlRGUFFtOWxqQm5Rek9RdWFnSFdXYWVtRDZEbXlJZjU2aGtpaFFJWWNUYjUzSWJDRklXUGVDMlZLaW9mREpDWHlwVTR6NEFSclFQRGxWSmowWkhFYVF5TEh4M014RWZXdElIZWtiK0Z6VXAybXU4dklIa2ZGQUZZS2FzakpwUWlQQmoyaUtQR29BYnlpQk9WbEZBa1BTRWxkU1VjWjA2QThSZTFrVjhzWUt6dy9jS3p2YjU5MzdwUm15UT09LGh0dHBzOi8vbWljcm9zb2Z0LnNoYXJlcG9pbnQuY29tL3RlYW1zL01TT3BlblRlY2gtQ0xBL3Rlc3RzaXRlLzwvU1A+";

    /**
     * NTLM authentication required cookie.
     */
    public static final String COOKIE_RT_FA = "rtFa=btHSSMLPPaqSjpN1ZN8U91B/V2iaHSc3ubUJtmn8E00D2zONyhkloc9dyL0wIPDbn+OkAsENZfTSseRlQ5eRfV2c53qjNPrHOOf9v63fOcLYYRgAcutqGjhPG3OzBqWI4vFbhT/CmR2bGSEPEuo79oDQRJzwLPgE7Zfiyla0tyYp3riPudFeDYwdedWvlsUsU9wCnR2UQsOPgr/jhDta/qxuJ+0uDdIln+DyX58GSrckMPpi97qkmTvuYAGhtt3CxCZFEXBb5bfIeCMNDh3iTq8xHT54UMZfw4Ea8Ohm29PDAe9ywinLpoLj+6oWYLNkQ6UEGRDTLsdSBTNaky6Q8nMlwgx30SjhX8JH8/unQIvjge/KLPypMVxEDom4CUe9IAAAAA==";

    /**
     * User name for authentication (whatever type is used).
     * Valid formats: <br/>
     * - domain\\username <br/>
     * - username@domain.com
     */
    public static final String USERNAME = "username@domain";

    /**
     * Password for authentication (whatever type is used).
     */
    public static final String PASSWORD = "your-pass";


}
