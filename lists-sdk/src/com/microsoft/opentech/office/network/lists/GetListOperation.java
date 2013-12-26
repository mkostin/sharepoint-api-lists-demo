package com.microsoft.opentech.office.network.lists;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class GetListOperation extends ODataOperation<ODataEntityRequest, Entity, ODataPubFormat> {

    private String mGUID;

    public GetListOperation(ICallback<Entity> listener, Context context, String guid) {
        super(listener, context);
        mGUID = guid;
    }

    @Override
    protected ODataEntityRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @Override
    protected URI getServerUrl() {
        String url = super.getServerUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mGUID + "')";
        return URI.create(url);
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        mResult = Entity.from(((ODataRetrieveResponse<ODataEntity>) response).getBody()).build();
        return true;
    }
}
