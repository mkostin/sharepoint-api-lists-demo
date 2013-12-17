package com.example.sharepoint.client.network;

import java.net.URI;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;
import android.util.Pair;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;

public class GetRequestDigestOperation extends HttpOperation {

    private static final String OPERATION_SUFFIX = "contextinfo";

    private static final String CONTEXT_WEB_INFORMATION_FIELD_NAME = "GetContextWebInformation";

    private static final String FORM_DIGEST_VALUE_FIELD_NAME = "FormDigestValue";

    public GetRequestDigestOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_BASE_URL + OPERATION_SUFFIX);
    }

    @Override
    protected boolean handleServerResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            this.mResult = json.getJSONObject(ODataOperation.SHAREPOINT_ROOT_OBJECT_NAME).getJSONObject(CONTEXT_WEB_INFORMATION_FIELD_NAME)
                    .getString(FORM_DIGEST_VALUE_FIELD_NAME);
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    @Override
    protected List<Pair<String, String>> getRequestHeaders() {
        List<Pair<String, String>> headers = super.getRequestHeaders();
        try {
            headers.add(Pair.create(ACCEPT_HTTP_HEADER_NAME, ODataOperation.SHAREPOINT_CONTENT_TYPE_JSON));

            AuthType auth = getAuthenticationType();
            if (auth == AuthType.Office365) {
                headers.add(Pair.create(COOKIE_HTTP_HEADER_NAME, Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH));
            } else if (auth == AuthType.Basic) {
                headers.add(Pair.create(AUTHORIZATION_HTTP_HEADER_NAME, BASIC_HTTP_AUTHORIZATION_PREFIX
                                + Base64.encodeToString((Constants.USERNAME + ":" + Constants.PASSWORD).getBytes(), Base64.DEFAULT).trim()));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    public AuthType getAuthenticationType() {
        return AuthType.Office365;
    }

    @Override
    protected Object getPostData() {
        return "";
    }
}
