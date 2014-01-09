package com.microsoft.opentech.office.core.auth;

import com.microsoft.opentech.office.core.auth.method.IAuthenticator;


/**
 * Configuration wrapper.
 */
public final class Configuration {

    /**
     * Base URL to access SharePoint and related services.
     */
    private static String sServerBaseUrl = null;

    /**
     * Currently used authentication strategy.
     */
    private static IAuthenticator sAuthenticator = null;

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
     * Sets currently used authentication strategy.
     *
     * @param auth New authentication strategy.
     */
    public static void setAuthenticator(IAuthenticator auth) {
        com.msopentech.odatajclient.engine.utils.Configuration.setHttpClientFactory(new DefaultAuthenticationFactory());
        com.msopentech.odatajclient.engine.utils.Configuration.setHttpUriRequestFactory(new DefaultRequestAuthenticationFactory());
        sAuthenticator = auth;
    }

    /**
     * Gets currently used authentication strategy.
     *
     * @return Current authentication strategy.
     */
    public static IAuthenticator getAuthenticator() {
        return sAuthenticator;
    }

}