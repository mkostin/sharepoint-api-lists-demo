package com.example.office.lists.auth;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.cookie.BasicClientCookie;

import com.example.office.Constants;
import com.microsoft.opentech.office.network.auth.AbstractCookieAuthenticator;

/**
 * Abstract implementation for Cookie credentials required to authorize to Office 365 online.
 */
public class CookieAuthenticator extends AbstractCookieAuthenticator {

    public CookieAuthenticator() {}

    @Override
    protected List<BasicClientCookie> getCookies() {
        ArrayList<BasicClientCookie> cookies = new ArrayList<BasicClientCookie>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 100);
        Date date = calendar.getTime();

        int idx = Constants.COOKIE_RT_FA.indexOf("=");
        BasicClientCookie rtFa = new BasicClientCookie(Constants.COOKIE_RT_FA.substring(0, idx), Constants.COOKIE_RT_FA.substring(idx + 1));
        rtFa.setExpiryDate(date);
        rtFa.setDomain(URI.create(Constants.SP_BASE_URL).getHost());
        rtFa.setPath("/");
        cookies.add(rtFa);

        idx = Constants.COOKIE_FED_AUTH.indexOf("=");
        BasicClientCookie fedAuth = new BasicClientCookie(Constants.COOKIE_FED_AUTH.substring(0, idx), Constants.COOKIE_FED_AUTH
                .substring(idx + 1));
        fedAuth.setExpiryDate(date);
        fedAuth.setDomain(URI.create(Constants.SP_BASE_URL).getHost());
        fedAuth.setPath("/");
        cookies.add(fedAuth);

        return cookies;
    }
}
