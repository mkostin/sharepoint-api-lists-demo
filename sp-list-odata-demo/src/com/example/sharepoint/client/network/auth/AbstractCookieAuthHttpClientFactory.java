package com.example.sharepoint.client.network.auth;

import java.net.URI;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.example.sharepoint.client.logger.Logger;
import com.msopentech.odatajclient.engine.client.http.DefaultHttpClientFactory;
import com.msopentech.odatajclient.engine.client.http.HttpMethod;

/**
 * Authentication method via known cookie(s).
 */
public abstract class AbstractCookieAuthHttpClientFactory extends DefaultHttpClientFactory {

    protected abstract List<BasicClientCookie> getCookies();

    @Override
    public HttpClient createHttpClient(final HttpMethod method, final URI uri) {
        final DefaultHttpClient httpclient = (DefaultHttpClient) super.createHttpClient(method, uri);

        try {
            CookieStore cookieStore = httpclient.getCookieStore();

            for (BasicClientCookie cookie : getCookies()) {
                cookieStore.addCookie(cookie);
            }

            httpclient.setCookieStore(cookieStore);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".createHttpClient(): Error.");
        }

        return httpclient;
    }

}
