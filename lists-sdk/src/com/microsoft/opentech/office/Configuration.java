package com.microsoft.opentech.office;

import com.microsoft.opentech.office.network.auth.DefaultAuthenticationFactory;
import com.microsoft.opentech.office.network.auth.DefaultRequestAuthenticationFactory;
import com.microsoft.opentech.office.network.auth.IAuthenticator;
import com.microsoft.opentech.office.network.auth.ISharePointCredentials;

/**
 * Configuration wrapper.
 */
public final class Configuration {

    /**
     * Base URL to access SharePoint and related services.
     */
    private static String sServerBaseUrl = null;

    /**
     * Currently used authentication method.
     */
    @Deprecated
    private static IAuthenticator sAuthenticationCredentials = null;

    /**
     * Currently used authentication credentials.
     */
    private static ISharePointCredentials sCredentials = null;

    /**
     * Sets base URL for application.
     *
     * @param serverBaseUrl New base URL value.
     */
    public static void setServerBaseUrl(String serverBaseUrl) {
        sServerBaseUrl = serverBaseUrl;
        if (!sServerBaseUrl.endsWith("/")) {
            sServerBaseUrl += "/";
        }
    }

    /**
     * Gets current value of base URL.
     *
     * @return Current base URL.
     */
    public static String getServerBaseUrl() {
        return sServerBaseUrl;
    }

    /**
     * Sets currently used authentication method.
     *
     * @param factory New authentication method.
     */
    public static void setAuthenticator(IAuthenticator creds) {
        com.msopentech.odatajclient.engine.utils.Configuration.setHttpClientFactory(new DefaultAuthenticationFactory());
        com.msopentech.odatajclient.engine.utils.Configuration.setHttpUriRequestFactory(new DefaultRequestAuthenticationFactory());
        sAuthenticationCredentials = creds;
    }

    /**
     * Gets currently used authentication method.
     *
     * @return Current authentication method.
     */
    public static IAuthenticator getAuthenticator() {
        return sAuthenticationCredentials;
    }

    /**
     * Sets currently used authentication credentials.
     *
     * @param factory New authentication credentials.
     */
    @Deprecated
    public static void setCredentials(ISharePointCredentials creds) {
        sCredentials = creds;
    }

    /**
     * Gets currently used authentication credentials.
     *
     * @return Current authentication credentials.
     */
    @Deprecated
    public static ISharePointCredentials getCredentials() {
        return sCredentials;
    }
}