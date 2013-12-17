package com.microsoft.opentech.office.network.auth;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.microsoft.opentech.office.Configuration;
import com.msopentech.odatajclient.engine.client.http.DefaultHttpClientFactory;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;

public class DefaultAuthenticationFactory extends DefaultHttpClientFactory {

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