package com.microsoft.opentech.office.core.auth;

/**
 * Interface for a class providing a set of resources required to access SharePoint.
 */
public interface ISharePointCredentials {

    /**
     * Gets client id.
     * 
     * @return Client id.
     */
    abstract String getClientId();

    /**
     * Gets client secret.
     * 
     * @return Client secret.
     */
    abstract String getClientSecret();

    /**
     * Gets redirect url.
     * 
     * @return Redirect url.
     */
    abstract String getRedirectUrl();

    /**
     * Gets domain name.
     * 
     * @return Domain name.
     */
    abstract String getDomain();

    /**
     * Gets URL.
     * 
     * @return URL.
     */
    abstract String getUrl();

    /**
     * Gets access code.
     * 
     * @return Access code.
     */
    abstract String getAccessCode();

    /**
     * Sets access code.
     * 
     * @param accessCode New access code.
     * @return Current {@link ISharePointCredentials} instance.
     */
    abstract ISharePointCredentials setAccessCode(String accessCode);

    /**
     * Gets refresh token.
     * 
     * @return Refresh token.
     */
    public String getRefreshToken();

    /**
     * Sets refresh token.
     * 
     * @param token New refresh token.
     * @return Current {@link ISharePointCredentials} instance.
     */
    public ISharePointCredentials setRefreshToken(String token);

    /**
     * Gets authorization token.
     * 
     * @return Authorization token.
     */
    abstract String getToken();

    /**
     * Sets new authorization token.
     * 
     * @param token New authorization token.
     * @return Current {@link ISharePointCredentials} instance.
     */
    abstract ISharePointCredentials setToken(String token);
}
