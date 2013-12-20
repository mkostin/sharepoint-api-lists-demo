package com.microsoft.opentech.office.network.files;

import java.net.URI;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Pair;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.HttpOperation;
import com.microsoft.opentech.office.network.odata.ODataOperation;

public class CreateFileOperation extends HttpOperation {

    private static String CREATE_FILE_TEMPLATE_URL = "web/GetFolderByServerRelativeUrl('%s')/Files/add(url='%s',overwrite=true)";

    private static final String CONTEXT_WEB_INFORMATION_FIELD_NAME = "GetContextWebInformation";

    private static final String FORM_DIGEST_VALUE_FIELD_NAME = "FormDigestValue";

    private String mFileName;

    private String mLibraryName;

    private byte[] mData;

    public CreateFileOperation(OnOperaionExecutionListener listener, Context context, String libName, String fileName, byte[] data) {
        super(listener, context);
        mFileName = fileName;
        mLibraryName = libName;
        mData = data;
    }

    @Override
    protected URI getServerUrl() {
        String url = Configuration.getServerBaseUrl() + CREATE_FILE_TEMPLATE_URL;
        url = String.format(url, mLibraryName, mFileName);
        return URI.create(url);
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
        return mData;
    }
}
