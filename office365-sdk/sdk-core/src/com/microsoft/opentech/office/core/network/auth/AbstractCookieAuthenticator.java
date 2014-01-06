package com.microsoft.opentech.office.core.network.auth;

import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.microsoft.opentech.office.core.network.NetworkException;

/**
 * Abstract implementation for credentials required to authorize using cookies.
 */
public abstract class AbstractCookieAuthenticator implements IAuthenticator {

    /**
     * Provides cookies to insert into request.
     *
     * @return cookies to insert into request.
     */
    protected abstract List<BasicClientCookie> getCookies();

    public AbstractCookieAuthenticator() {
    }

    @Override
    public void prepareClient(HttpClient client) {
        try {
            CookieStore cookieStore = ((DefaultHttpClient) client).getCookieStore();

            for (BasicClientCookie cookie : getCookies()) {
                cookieStore.addCookie(cookie);
            }

            ((DefaultHttpClient) client).setCookieStore(cookieStore);
        } catch (Exception e) {
            throw new NetworkException("Error while preparing and adding authentication cookies to a request.", e);
        }
    }

    @Override
    public void prepareRequest(HttpUriRequest request) {
    }

}
