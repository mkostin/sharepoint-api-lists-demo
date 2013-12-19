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
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * SP Lists list retrieval operation.
 */
public class GetListsOperation extends ODataOperation<ODataEntityRequest, ODataCollectionValue, ODataPubFormat> {

    public GetListsOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        ODataEntity entity = ((ODataRetrieveResponse<ODataEntity>) response).getBody();
        for (ODataProperty p : entity.getProperties()) {
            mResult = p.getComplexValue().get(SHAREPOINT_RESULTS_FIELD_NAME).getCollectionValue();
        }
        return true;
    }

    @Override
    protected URI getServerUrl() {
        String url = super.getServerUrl() + SHAREPOINT_LISTS_URL_SUFFIX;
        return URI.create(url);
    }
}
