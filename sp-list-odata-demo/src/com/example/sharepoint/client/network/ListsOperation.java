package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.example.sharepoint.client.network.auth.UserIdentity;
import com.example.sharepoint.client.network.ntlm.NTLMSchemeFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.ODataURIBuilder;

/**
 * SP Lists list retrieval operation.
 */
public class ListsOperation extends HttpOperation {

    private ODataCollectionValue result;

    public ListsOperation(OnOperaionExecutionListener listener, AuthType authType, Context context) {
        super(listener, authType, context);
    }

    @Override
    protected List<Header> getRequestHeaders() {
        List<Header> headers = super.getRequestHeaders();
        try {
            headers.add(new BasicHeader("Accept", "application/json; odata=verbose"));

            if (authType == AuthType.Office365) {
                headers.add(new BasicHeader("Cookie", Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH));
            } else if (authType == AuthType.Basic) {
                headers.add(new BasicHeader("Authorization", "Basic "
                        + Base64.encodeToString((Constants.USERNAME + ":" + Constants.PASSWORD).getBytes(), Base64.DEFAULT).trim()));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    @Override
    protected HttpUriRequest getHttpRequest() {
        HttpUriRequest message = super.getHttpRequest();
        try {
            message.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getHttpRequest(): Error.");
        }
        return message;
    }

    protected boolean initializeClient(HttpClient httpClient) {
        try {
            //CommonsHttpOAuthProvider provider = new CommonsHttpOAuthProvider(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
            if (authType == AuthType.NTLM) {
                ((AbstractHttpClient) httpClient).getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            }
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".initializeClient(): Error.");
        }
        return false;
    }

    @Override
    protected boolean setCredentials(CredentialsProvider provider) {
        try {
            if (provider == null) return false;

            switch (authType) {
                case Basic:
                    provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials(
                            Constants.USERNAME, Constants.PASSWORD));
                    break;
                case NTLM:
                    // also see initializeClient()
                    String userName = UserIdentity.parseUserName(Constants.USERNAME);
                    String domain = UserIdentity.parseDomainName(Constants.USERNAME);
                    if (TextUtils.isEmpty(domain)) {
                        domain = "localhost";
                    }
                    NTCredentials creds = new NTCredentials(userName, Constants.PASSWORD, "", "EUROPE");
                    provider.setCredentials(AuthScope.ANY, creds);
                    break;
                case Office365:
                    // setHeaders() method is acting as a stub by using cookies.
                    // TODO: implement authentication.
                    break;
                default:
                    break;
            }

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".setCredentials(): Error.");
        }

        return false;
    }

    @Override
    public void execute() {
        ODataURIBuilder uriBuilder = new ODataURIBuilder(Constants.SP_BASE_URL);
        uriBuilder.appendEntityTypeSegment("Web/Lists");
        ODataEntityRequest req = ODataRetrieveRequestFactory.getEntityRequest(uriBuilder.build());
        for (Header header : getRequestHeaders()) {
            req.addCustomHeader(header.getName(), header.getValue());
        }

        ODataEntity res = req.execute().getBody();

        boolean isSucceeded = handleServerResponse(res);
        if (mListener != null) {
            mListener.onExecutionComplete(this, isSucceeded);
        }
    }

    protected boolean handleServerResponse(ODataEntity response) {
        try {
            for (ODataProperty p : response.getProperties()) {
                result = p.getComplexValue().get("results").getCollectionValue();
            }

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    @Override
    protected String getServerUrl() {
        return Constants.SP_LISTS_URL;
    }

    public ODataCollectionValue getResult() {
        return result;
    }
}
