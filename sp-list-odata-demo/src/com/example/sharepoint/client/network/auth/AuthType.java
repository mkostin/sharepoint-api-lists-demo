package com.example.sharepoint.client.network.auth;

/**
 * Authentication type for connections.
 */
public enum AuthType {
    Undefined,
    Basic,
    AD,
    Office365,
    NTLM;

    /**
     * @return String with CRM authentication type.
     */
    public String getAuthenticationType() {
        return String.valueOf(this.ordinal() - 1);
    }

    /**
     * Checks that this authentication type used with new SOAP protocol started from '2013 series' products.
     *
     * @return <code>true</code> if '2013' authentication type, <code>false</code> otherwise.
     */
    public boolean isOffice2013() {
        return this == Office365 || this == NTLM;
    }
}
