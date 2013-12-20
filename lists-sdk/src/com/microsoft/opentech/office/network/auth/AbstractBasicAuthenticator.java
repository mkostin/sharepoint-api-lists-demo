package com.microsoft.opentech.office.network.auth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.util.Base64;

import com.microsoft.opentech.office.network.NetworkException;

/**
 * Abstract implementation for credentials required to authorize using cookies.
 */
public abstract class AbstractBasicAuthenticator implements IAuthenticator {

    protected abstract String getUsername();

    protected abstract String getPassword();

    @Override
    public void prepareClient(HttpClient client) {
        try {
            String encodedValue = Base64.encodeToString((getUsername() + ":" + getPassword()).getBytes(), Base64.DEFAULT).trim();
            ((HttpUriRequest) client).setHeader("Authorization", "Basic " + encodedValue);

        } catch (Exception e) {
            throw new NetworkException("Error while preparing and adding authentication cookies to a request.", e);
        }
    }

    @Override
    public void prepareRequest(HttpUriRequest request) {
    }

}
