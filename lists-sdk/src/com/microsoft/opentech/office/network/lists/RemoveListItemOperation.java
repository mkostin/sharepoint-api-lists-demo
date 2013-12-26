package com.microsoft.opentech.office.network.lists;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class RemoveListItemOperation extends ODataOperation<ODataDeleteRequest, Boolean, ODataPubFormat> {

    private String mListGUID;

    private int mIndex;

    public RemoveListItemOperation(ICallback<Boolean> listener, Context context, String guid, int index) {
        super(listener, context);
        mListGUID = guid;
        mIndex = index;
    }

    @Override
    protected ODataDeleteRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        ODataDeleteRequest req = ODataCUDRequestFactory.getDeleteRequest(getServerUrl());
        req.setIfMatch(ETAG_ANY_ETAG);
        return req;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mListGUID + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX
                + "(" + mIndex + ")");
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
