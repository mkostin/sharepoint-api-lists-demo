package com.microsoft.opentech.office.network.lists;

import java.net.URI;
import java.util.List;

import android.content.Context;

import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * SP Lists list retrieval operation.
 */
public class GetListsOperation extends ODataOperation<ODataEntityRequest, List<Object>, ODataPubFormat> {

    public GetListsOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            Entity entity = Entity.from(((ODataRetrieveResponse<ODataEntity>) response).getBody()).build();
            mResult = (List<Object>) entity.get(SHAREPOINT_RESULTS_FIELD_NAME);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    protected URI getServerUrl() {
        String url = super.getServerUrl() + SHAREPOINT_LISTS_URL_SUFFIX;
        return URI.create(url);
    }
}
