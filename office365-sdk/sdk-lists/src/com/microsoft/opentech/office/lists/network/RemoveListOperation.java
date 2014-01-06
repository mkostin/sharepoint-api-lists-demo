package com.microsoft.opentech.office.lists.network;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.core.Configuration;
import com.microsoft.opentech.office.core.network.odata.ODataOperation;
import com.microsoft.opentech.office.core.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Removes specified list and returns a value indicating about success.
 */
public class RemoveListOperation extends ODataOperation<ODataDeleteRequest, Boolean, ODataPubFormat> {

    private String mGuid;

    /**
     * Creates a new instance of {@link RemoveListOperation} class.
     * 
     * @param listener Callback to be executed when operation finishes.
     * @param context Application context.
     * @param guid List GUID.
     */
    public RemoveListOperation(ICallback<Boolean> listener, Context context, String guid) {
        super(listener, context, true);
        mGuid = guid;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mGuid + "')");
    }

    @Override
    protected ODataDeleteRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        ODataDeleteRequest request = ODataCUDRequestFactory.getDeleteRequest(getServerUrl());
        request.setIfMatch(ETAG_ANY_ETAG);
        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        // If any error occurred during request execution, server returns 4xx (or 5xx) code. This case is handled by ODataJClient classes,
        // they will throw an unchecked exception (TODO: add such exceptions to superclass execute() method signature). So, if we are here,
        // we have no need to handle response: it was succeeded.
        mResult = true;
        return true;
    }

}
