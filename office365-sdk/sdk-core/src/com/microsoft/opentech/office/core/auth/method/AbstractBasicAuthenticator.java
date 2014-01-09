package com.microsoft.opentech.office.core.auth.method;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.util.Base64;

import com.microsoft.opentech.office.core.net.NetworkException;

/**
 * Abstract implementation for credentials required to authorize using cookies.
 */
public abstract class AbstractBasicAuthenticator implements IAuthenticator {

    protected abstract String getUsername();

    protected abstract String getPassword();

    @Override
    public void prepareClient(HttpClient client) throws NetworkException {
// TODO: verify
//            CredentialsProvider provider = ((AbstractHttpClient) client).getCredentialsProvider();
//            provider.setCredentials(
//                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
//                    new UsernamePasswordCredentials(getUsername(), getPassword()));
    }

    @Override
    public void prepareRequest(HttpUriRequest request) {
        String encodedValue = Base64.encodeToString((getUsername() + ":" + getPassword()).getBytes(), Base64.DEFAULT).trim();
        request.setHeader("Authorization", "Basic " + encodedValue);
    }

}
