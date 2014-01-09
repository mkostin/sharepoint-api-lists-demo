package com.microsoft.opentech.office.core.auth;

import java.net.URI;

import org.apache.http.client.methods.HttpUriRequest;

import com.microsoft.opentech.office.core.auth.method.IAuthenticator;
import com.msopentech.odatajclient.engine.client.http.DefaultHttpUriRequestFactory;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;

/**
 * Default implementation of request authentication factory.
 */
class DefaultRequestAuthenticationFactory extends DefaultHttpUriRequestFactory {

    @Override
    public HttpUriRequest createHttpUriRequest(final HttpMethod method, final URI uri) {
        final HttpUriRequest request = super.createHttpUriRequest(method, uri);
        try {
            final IAuthenticator creds = Configuration.getAuthenticator();
            if (creds != null) {
                creds.prepareRequest(request);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign request via OAuth", e);
        }
        return request;
    }
}