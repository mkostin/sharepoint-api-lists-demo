package com.microsoft.opentech.office.network.lists;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class GetItemOperation extends ODataOperation<ODataEntityRequest, Entity, ODataPubFormat> {

    private String mListGUID;

    private int mItemId;

    public GetItemOperation(OnOperaionExecutionListener listener, Context context, String listGUID, int id) {
        super(listener, context);
        mListGUID = listGUID;
        mItemId = id;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mListGUID + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX
                + "(" + mItemId + ")");
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        ODataEntity odataEntity = ((ODataRetrieveResponse<ODataEntity>) response).getBody();
        mResult = Entity.from(odataEntity).build();
        return true;
    }

    @Override
    protected ODataEntityRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }
}
