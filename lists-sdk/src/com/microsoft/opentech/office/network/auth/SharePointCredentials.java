package com.microsoft.opentech.office.network.auth;

import android.text.TextUtils;

/**
 * Stores credentials required to authenticate to Office 365 Online.
 */
public class SharePointCredentials implements ISharePointCredentials {

    private String mClientId;

    private String mClientSecret;

    private String mUrl;

    private String mDomain;

    private String mRedirectUrl;

    private String mAccessCode;

    private String mToken;

    public SharePointCredentials(String clientId, String clientSecret, String url, String redirectUrl, String domain) throws IllegalArgumentException {
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
        // TODO Auto-generated method stub
        return mClientSecret;
    }

    @Override
    public String getRedirectUrl() {
        // TODO Auto-generated method stub
        return mRedirectUrl;
    }

    @Override
    public String getDomain() {
        // TODO Auto-generated method stub
        return mDomain;
    }

    @Override
    public String getUrl() {
        // TODO Auto-generated method stub
        return mUrl;
    }

    @Override
    public String getAccessCode() {
        // TODO Auto-generated method stub
        return mAccessCode;
    }

    @Override
    public void setAccessCode(String accessCode) {
        mAccessCode = accessCode;
    }

    @Override
    public String getToken() {
        // TODO Auto-generated method stub
        return mToken;
    }

    @Override
    public void setToken(String token) {
        mToken = token;
    }
}
