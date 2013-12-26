package com.microsoft.opentech.office.network.files;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.util.Pair;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.HttpOperation;
import com.microsoft.opentech.office.network.odata.DigestRequestOperation;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.async.ICallback;

public class CreateFileOperation extends HttpOperation {

    private static String CREATE_FILE_TEMPLATE_URL = "web/GetFolderByServerRelativeUrl('%s')/Files/add(url='%s',overwrite=true)";

    private static final String REQUEST_DIGEST_HEADER_NAME = "X-RequestDigest";

    private static final String SHAREPOINT_METADATA_URI_FIELD_NAME = "uri";

    private String mFileName;

    private String mLibraryName;

    private byte[] mData;

    private Entity mCreatedEntity;

    public CreateFileOperation(ICallback<String> listener, Context context, String libName, String fileName, byte[] data) {
        super(listener, context);
        mFileName = fileName;
        mLibraryName = libName;
        mData = data;
    }

    public Entity getCreatedEntity() {
        return mCreatedEntity;
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

            mCreatedEntity = Entity.from(response).build();
            // get relative url
            String uri = (String) mCreatedEntity.getMeta(SHAREPOINT_METADATA_URI_FIELD_NAME);
            // uri looks like
            // https://example.com/.../_api/Web/GetFileByServerRelativeUrl('/teams/MSOpenTech-CLA/testsite/Shared%20Documents/image.jpg')
            // extract part in parentheses
            final String getCommand = "GetFileByServerRelativeUrl";
            String relativeUri = uri.substring(uri.indexOf(getCommand) + getCommand.length() + 2);
            relativeUri = relativeUri.substring(0, relativeUri.length() - 2);
            URI apiUri = URI.create(uri);
            String absoluteUri = (apiUri.getScheme() == null ? "" : apiUri.getScheme()) + "://" + apiUri.getHost() + relativeUri;
            this.mResult = absoluteUri;
            return true;
        } catch (Exception e) {
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

        DigestRequestOperation digestOper = new DigestRequestOperation(null, mContext);
        try {
            digestOper.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        headers.add(Pair.create(REQUEST_DIGEST_HEADER_NAME, digestOper.getResult()));

        return headers;
    }

    @Override
    protected Object getPostData() {
        return mData;
    }
}
