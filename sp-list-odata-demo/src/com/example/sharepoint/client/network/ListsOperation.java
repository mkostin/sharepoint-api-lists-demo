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

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.example.sharepoint.client.network.auth.UserIdentity;
import com.example.sharepoint.client.network.ntlm.NTLMSchemeFactory;

/**
 * SP Lists list retrieval operation.
 */
public class ListsOperation extends HttpOperation {

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
                    provider.setCredentials(
                            new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                            new UsernamePasswordCredentials(Constants.USERNAME, Constants.PASSWORD));
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

    protected boolean handleServerResponse(String response) {
        return true;
    }

    @Override
    protected String getServerUrl() {
        return Constants.SP_LISTS_URL;
    }
}
