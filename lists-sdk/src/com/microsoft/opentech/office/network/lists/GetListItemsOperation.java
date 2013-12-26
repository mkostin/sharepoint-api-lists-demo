package com.microsoft.opentech.office.network.lists;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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

public class GetListItemsOperation extends ODataOperation<ODataEntityRequest, List<Object>, ODataPubFormat> {

    private String mGUID;

    public GetListItemsOperation(ICallback<List<Object>> listener, Context context, String guid) {
        super(listener, context);
        this.mGUID = guid;
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        Entity entity = Entity.from(((ODataRetrieveResponse<ODataEntity>) response).getBody()).build();
        mResult = (List<Object>) entity.get(SHAREPOINT_RESULTS_FIELD_NAME);
        if (mResult == null) {
            mResult = new ArrayList<Object>();
        }
        return true;
    }

    @Override
    protected URI getServerUrl() {
        String listUrl = super.getServerUrl().toString();
        if (!listUrl.endsWith("/")) {
            listUrl += "/";
        }
        listUrl += SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mGUID + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX;
        return URI.create(listUrl);
    }
}
