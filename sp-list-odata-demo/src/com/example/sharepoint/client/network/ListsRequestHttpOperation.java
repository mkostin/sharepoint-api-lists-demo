package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.message.BasicHeader;

import android.content.Context;
import android.text.TextUtils;

import com.example.sharepoint.client.logger.Logger;

/**
 * SP Lists list retrieval operation.
 */
public class ListsRequestHttpOperation extends HttpOperation {

    /**
     * Base URL to access SharePoint and all related services.
     */
    private final String SP_BASE_URL = "http://your-app.cloudapp.net/";

    /**
     * Endpoint to retrieve Lists.
     */
    private final String SP_LISTS_URL = SP_BASE_URL + "_api/web/lists";

    /**
     * Server response.
     */
    private String response;

    public ListsRequestHttpOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected List<Header> getRequestHeaders() {
        List<Header> headers = super.getRequestHeaders();
        try {
            headers.add(new BasicHeader("Accept", "application/json; odata=verbose"));
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    @Override
    protected boolean handleServerResponse(String response) {
        this.response = response;
        return !TextUtils.isEmpty(response);
    }

    @Override
    protected boolean setCredentials(CredentialsProvider provider) {
        try {
            if (provider == null) return false;

            provider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials("domain\\username", "your-password-here"));

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".setCredentials(): Error.");
        }

        return false;
    }

    @Override
    protected String getServerUrl() {
        return SP_LISTS_URL;
    }

    public String getResult() {
        return response;
    }
}
