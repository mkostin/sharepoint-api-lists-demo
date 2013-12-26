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
    public static final String COOKIE_FED_AUTH = "FedAuth=77u/PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48U1A+RmFsc2UsMGguZnxtZW1iZXJzaGlwfDEwMDM3ZmZlODc5NDU4NjJAbGl2ZS5jb20sMCMuZnxtZW1iZXJzaGlwfHYtbWF4a29zQG1pY3Jvc29mdC5jb20sMTMwMzI4NzgwNTY0NTg0NTM5LEZhbHNlLFhZb2p3eTdtVDB1WHVtMEV5QmltWVRneUZjZHZsNERSOEIzVHNRam02ZndIamFGS0lhTWFVbjNET0FDbDMyaTZqQ3FZZUVHRFVPcmpxdVA0bHI4cjVkR0Q5aTVsSHdMR0tqeHFuSUVoVGl1RGFlM1hzNkQ2NnNWL3M0WGloditjd1pjMkFiOXMzM1U5NExWa2E2Y0ROR3QrWWU0OUhBVmlRUzc5VUQydWFSSUk0OER0Z1BsY0xveEZXUzJNNEQ3YWZUaHpDbEN5TGlTTUNJNXF2T090QlFQTGlJaENiY0hrSmJzS1FsaGQ2bzN5TjBkU2VmelVJMHhiTUdnaldvK2ZNcnN4TkVwK1F4Q0ZmSzNRZC9sd0pqUitlUWRkODJ2emxjaTNNYTA0Y0tIYitZbXBYN0lNSDl4MVFtSU9QSmJqRU5zT2cxalNpWFJ1cUl4ZXdsV1E4QT09LGh0dHBzOi8vbWljcm9zb2Z0LnNoYXJlcG9pbnQuY29tL3RlYW1zL01TT3BlblRlY2gtQ0xBL3Rlc3RzaXRlL19sYXlvdXRzLzE1L3N0YXJ0LmFzcHg8L1NQPg==";

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
