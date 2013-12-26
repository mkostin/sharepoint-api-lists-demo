package com.microsoft.opentech.office.network.auth;

/**
 * Interface for a class providing a set of resources required to access SharePoint.
 */
public interface ISharePointCredentials {

    abstract String getClientId();

    abstract String getClientSecret();

    abstract String getRedirectUrl();

    abstract String getDomain();

    abstract String getUrl();

    abstract String getAccessCode();

    abstract ISharePointCredentials setAccessCode(String accessCode);

    public String getRefreshToken();

    public ISharePointCredentials setRefreshToken(String token);

    abstract String getToken();

    abstract ISharePointCredentials setToken(String token);
}
