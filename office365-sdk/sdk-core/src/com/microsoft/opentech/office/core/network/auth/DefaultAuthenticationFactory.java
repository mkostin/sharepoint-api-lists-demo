package com.microsoft.opentech.office.core.network.auth;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.microsoft.opentech.office.core.Configuration;
import com.msopentech.odatajclient.engine.client.http.DefaultHttpClientFactory;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;

/**
 * Default implementation of authentication factory.
 */
public class DefaultAuthenticationFactory extends DefaultHttpClientFactory {

    /**
     * Creates a new instance of {@link DefaultAuthenticationFactory} class.
     */
    public DefaultAuthenticationFactory() {}

    @Override
    public HttpClient createHttpClient(HttpMethod method, URI uri) {
        final DefaultHttpClient httpclient = (DefaultHttpClient) super.createHttpClient(method, uri);

        final IAuthenticator creds = Configuration.getAuthenticator();
        if (creds != null) {
            creds.prepareClient(httpclient);
        }

        return httpclient;
    }

}