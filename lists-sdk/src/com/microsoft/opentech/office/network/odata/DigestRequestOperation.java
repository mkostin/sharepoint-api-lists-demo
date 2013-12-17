package com.microsoft.opentech.office.network.odata;

import java.net.URI;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.HttpOperation;

public class DigestRequestOperation extends HttpOperation {

    private static final String OPERATION_SUFFIX = "contextinfo";

    private static final String CONTEXT_WEB_INFORMATION_FIELD_NAME = "GetContextWebInformation";

    private static final String FORM_DIGEST_VALUE_FIELD_NAME = "FormDigestValue";

    public DigestRequestOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + OPERATION_SUFFIX);
    }

    @Override
    protected boolean handleServerResponse(String response) throws RuntimeException {
        try {
            JSONObject json = new JSONObject(response);
            this.mResult = json.getJSONObject(ODataOperation.SHAREPOINT_ROOT_OBJECT_NAME).getJSONObject(CONTEXT_WEB_INFORMATION_FIELD_NAME)
                    .getString(FORM_DIGEST_VALUE_FIELD_NAME);
            return true;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean initializeClient(HttpClient httpClient) {
        Configuration.getAuthenticator().prepareClient(httpClient);
        return super.initializeClient(httpClient);
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
