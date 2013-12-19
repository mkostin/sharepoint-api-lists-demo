package com.microsoft.opentech.office.network.lists;

import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class GetListItemsOperation extends ODataOperation<ODataEntityRequest, ODataCollectionValue, ODataPubFormat> {

    private String mGUID;

    public GetListItemsOperation(OnOperaionExecutionListener listener, Context context, String guid) {
        super(listener, context);
        this.mGUID = guid;
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        ODataEntity res = ((ODataRetrieveResponse<ODataEntity>) response).getBody();
        mResult = res.getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue().get(SHAREPOINT_RESULTS_FIELD_NAME).getCollectionValue();
        if (mResult == null) {
            mResult = new ODataCollectionValue(null); // handle an empty list situation
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
