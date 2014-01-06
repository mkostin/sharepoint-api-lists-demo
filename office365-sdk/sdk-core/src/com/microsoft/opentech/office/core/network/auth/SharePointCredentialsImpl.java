package com.microsoft.opentech.office.core.network.auth;

import java.io.Serializable;

import android.text.TextUtils;

/**
 * Stores credentials required to authenticate to Office 365 Online.
 */
public class SharePointCredentialsImpl implements ISharePointCredentials, Serializable {

    /**
     * Unique storage UUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * SharePoint application unique ID. E.g. "60188dfc-3250-44c5-8434-8f106c9e529e".
     */
    private String mClientId;

    /**
     * SharePoint application client secret value. E.g. "Epn9cpyL7qxyCvPJgjNv6RNYZDAEq0vDefhJ+3hi16A=".
     */
    private String mClientSecret;

    /**
     * Office365/SharePoint site url. E.g. "https://microsoft.sharepoint.com/teams/root-site/sub-site/".
     */
    private String mUrl;

    /**
     * Domain you're operation with. E.g. "www.domain.com"
     */
    private String mDomain;

    /**
     * Url application will be redirected after authentication. Should be SSL secure. "https://www.domain.com/redirect".
     */
    private String mRedirectUrl;

    /**
     * Access code returned by the SharePoint site after 1-st step of authentication. E.g.
     * "IAAAAAaTtI10OGlsRaiSpMZotjUjygIvXmUBbWT7f1cNRVC5mVYsONbP%5F6Ek40hecRnnYDt3otof7QGtUVXWW%5FPbuAo7QKtdmC%2Dn9xJSRzWC6S5%2Dx9DHScNcluxOoEr7F8loWmUMbAfY9uRcbtWxqaHmbDMD6e8v2WD3NGrFI4gn%5FZ6aGTv%2DlpvuoDNddRbokyjinfcYhcYKRsoifZqFdmZY1%2DBvKHC%2Dk5gCc%2DXkVj29ISyZ9qMq8MSkSgcc0rWaBsjijX8C84KGkCprRSOvCg7Wtn1Rt1lhTkACvyZBF94v1r95aM0NF3Qer83z2KZEgTPWjTtkBtP1WaXbUA88BeT7AUb9gNq0WEleD6xh0EJC7tCCuzFak7WkXHJX8mq4ikv2IFd3Y9BWa7IjidrzWWYh40c"
     */
    private String mAccessCode;

    /**
     * Refresh token returned by the SharePoint site after 2-st (final) step of authentication.
     */
    private String mRefreshToken;

    /**
     * Access token returned by the SharePoint site after 2-st (final) step of authentication.
     */
    private String mToken;

    public SharePointCredentialsImpl() {}

    /**
     * Instantiates and validates credentials.
     *
     * @param clientId SharePoint application unique ID. E.g. "60188dfc-3250-44c5-8434-8f106c9e529e".
     * @param clientSecret SharePoint application client secret value. E.g. "Epn9cpyL7qxyCvPJgjNv6RNYZDAEq0vDefhJ+3hi16A=".
     * @param url Office365/SharePoint site url. E.g. "https://microsoft.sharepoint.com/teams/root-site/sub-site/".
     * @param redirectUrl Url application will be redirected after authentication. Should be SSL secure. "https://www.domain.com/redirect".
     * @param domain Domain you're operation with. E.g. "www.domain.com".
     *
     * @throws IllegalArgumentException Thrown if arguments validation fails.
     */
    public SharePointCredentialsImpl(String clientId, String clientSecret, String url, String redirectUrl, String domain)
            throws IllegalArgumentException {
        mClientId = clientId;
        mClientSecret = clientSecret;
        mUrl = url;

        if (TextUtils.isEmpty(mUrl)) {
            throw new IllegalArgumentException("Url can not be null or empty");
        }

        mRedirectUrl = redirectUrl;
        if (TextUtils.isEmpty(mRedirectUrl)) {
            throw new IllegalArgumentException("Redirect Url can not be null or empty");
        }

        mDomain = domain;
    }

    @Override
    public String getClientId() {
        return mClientId;
    }

    @Override
    public String getClientSecret() {
        return mClientSecret;
    }

    @Override
    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    @Override
    public String getDomain() {
        return mDomain;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getAccessCode() {
        return mAccessCode;
    }

    @Override
    public SharePointCredentialsImpl setAccessCode(String accessCode) {
        mAccessCode = accessCode;
        return this;
    }

    @Override
    public String getRefreshToken() {
        return mRefreshToken;
    }

    @Override
    public SharePointCredentialsImpl setRefreshToken(String token) {
        mRefreshToken = token;
        return this;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public SharePointCredentialsImpl setToken(String token) {
        mToken = token;
        return this;
    }

    /**
     * @param clientId the mClientId to set.
     */
    public SharePointCredentialsImpl setClientId(String clientId) {
        mClientId = clientId;
        return this;
    }

    /**
     * @param clientSecret the mClientSecret to set.
     */
    public SharePointCredentialsImpl setClientSecret(String clientSecret) {
        mClientSecret = clientSecret;
        return this;
    }

    /**
     * @param url the mUrl to set.
     */
    public SharePointCredentialsImpl setUrl(String url) {
        mUrl = url;
        return this;
    }

    /**
     * @param domain the mDomain to set.
     */
    public SharePointCredentialsImpl setDomain(String domain) {
        mDomain = domain;
        return this;
    }

    /**
     * @param redirectUrl the mRedirectUrl to set.
     */
    public SharePointCredentialsImpl setRedirectUrl(String redirectUrl) {
        mRedirectUrl = redirectUrl;
        return this;
    }
}
