package com.example.office.lists.auth;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import com.example.office.lists.auth.ntlm.NTLMSchemeFactory;
import com.example.office.lists.storage.AuthPreferences;
import com.microsoft.opentech.office.core.network.NetworkException;
import com.microsoft.opentech.office.core.network.auth.AbstractBasicAuthenticator;

/**
 * Abstract implementation for NTLM credentials required to authorize to Office 365 online.
 */
public class NTLMAuthenticator extends AbstractBasicAuthenticator {

    /**
     * Name of auth scheme.
     */
    private static String SCHEME_NTLM = "ntlm";

    public NTLMAuthenticator() {}

    @Override
    public void prepareClient(final HttpClient client) {
        try {
            ((AbstractHttpClient) client).getAuthSchemes().register(SCHEME_NTLM, new NTLMSchemeFactory());

            CredentialsProvider provider = ((AbstractHttpClient) client).getCredentialsProvider();

            // TODO: verify if this is required
            // String name = getUsername();
            // String userName = UserIdentity.parseUserName(name);
            // String domain = UserIdentity.parseDomainName(name);
            // if (TextUtils.isEmpty(domain)) {
            // domain = "localhost";
            // }

            NTCredentials creds = new NTCredentials(getUsername(), getPassword(), "", "");

            provider.setCredentials(AuthScope.ANY, creds);
        } catch (Exception e) {
            throw new NetworkException("Error while applying NTLM authentication to a request.", e);
        }
    }

    @Override
    public void prepareRequest(HttpUriRequest request) {
        request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
    }

    @Override
    protected String getUsername() {
        final SharePointCredentials creds = (SharePointCredentials) AuthPreferences.loadCredentials();
        if(creds != null) {
            return creds.getLogin();
        }
        return "";
    }

    @Override
    protected String getPassword() {
        final SharePointCredentials creds = (SharePointCredentials) AuthPreferences.loadCredentials();
        if(creds != null) {
            return creds.getPassword();
        }
        return "";
    }
}
