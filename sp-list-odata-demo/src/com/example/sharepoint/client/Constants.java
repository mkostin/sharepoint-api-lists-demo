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
    public static final String COOKIE_FED_AUTH = "FedAuth=77u/PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U1A+RmFsc2UsMGguZnxtZW1iZXJzaGlwfDEwMDM3ZmZlODc5NDU4NjJAbGl2ZS5jb20sMCMuZnxtZW1iZXJzaGlwfHYtbWF4a29zQG1pY3Jvc29mdC5jb20sMTMwMzE5MTA3MjM0NzcwNTExLEZhbHNlLFdlTDNPYVE3Q1hiVmVvMHl6cmdtUFErRElBN3hDV3VhMkk4RWR0SlpKcnVtQ3FGcVFaTHRRZTlkT2R0MnowNFFRYmlyMngyWEFsQk9FR2pKTE5yZys2UDBMYnVMcVBTNUpRTXNTZXV6M25mZkwrUk9DTDNPeEM5Mm5ubDdMcnZFQTcyTzBDZ2d4MElwK2FWK21Zcy9zcTE1NHNDd2tKQW5Da2JidlhNNmNERkloWWE3M1B6eTRTaDM1cVNzVjBwNTU4VHN3NG01VHU3aHgyTVQxWFRnUWNiVHBqbFNCZzBqSzRlSy9jY2o3eW1hVzg4bUN1TXkvSURWa05BMDRjQmpsU21vTWNvZ2g5Z3ZCV3NuaDdpSk9lRVJaQmpsWk1MN2MrTzFXMUIrbkp1OGY0d0FsdERwWEpRQTdwYVRpeDMzdmlwL1F5cU1LTFJQVU9GS05iU0hIZz09LGh0dHBzOi8vbWljcm9zb2Z0LnNoYXJlcG9pbnQuY29tL19sYXlvdXRzLzE1L2ltYWdlcy9mYXZpY29uLmljbz9yZXY9MzE8L1NQPg==";

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
    public static final String USERNAME = "name@domain";

    /**
     * Password for authentication (whatever type is used).
     */
    public static final String PASSWORD = "password";

    public static AuthType AUTH_TYPE = AuthType.Office365;

}
