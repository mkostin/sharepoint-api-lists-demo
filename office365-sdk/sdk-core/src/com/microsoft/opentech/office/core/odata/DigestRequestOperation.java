package com.microsoft.opentech.office.core.odata;

import java.net.URI;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.util.Pair;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.Configuration;
import com.microsoft.opentech.office.core.net.HttpOperation;

/**
 * Retrieves request digest to sign an operation.
 */
public class DigestRequestOperation extends HttpOperation {

    private static final String OPERATION_SUFFIX = "contextinfo";

    private static final String CONTEXT_WEB_INFORMATION_FIELD_NAME = "GetContextWebInformation";

    private static final String FORM_DIGEST_VALUE_FIELD_NAME = "FormDigestValue";

    /**
     * Creates a new instance of {@link DigestRequestOperation} class.
     *
     * @param listener Callback to be executed when operation finishes.
     * @param context Application context.
     */
    public DigestRequestOperation(IOperationCallback<String> listener, Context context) {
        super(listener, context);
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + OPERATION_SUFFIX);
    }

    @Override
    protected boolean handleServerResponse(String response) throws RuntimeException {
        Entity entity = Entity.from(response).build();
        this.mResult = ((Entity)entity.get(CONTEXT_WEB_INFORMATION_FIELD_NAME)).get(FORM_DIGEST_VALUE_FIELD_NAME).toString();
        return true;
    }

    @Override
    protected boolean prepareClient(HttpClient httpClient) {
        Configuration.getAuthenticator().prepareClient(httpClient);
        return super.prepareClient(httpClient);
    }

    @Override
    protected boolean prepareMessage(HttpUriRequest httpMessage) {
        Configuration.getAuthenticator().prepareRequest(httpMessage);
        return super.prepareMessage(httpMessage);
    }

    @Override
    protected List<Pair<String, String>> getRequestHeaders() throws RuntimeException {
        List<Pair<String, String>> headers = super.getRequestHeaders();
        headers.add(Pair.create(ACCEPT_HTTP_HEADER_NAME, ODataOperation.SHAREPOINT_CONTENT_TYPE_JSON));

        return headers;
    }

    @Override
    protected Object getPostData() {
        return "";
    }
}
