package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
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
public class ListsOperation extends ODataOperation<ODataEntityRequest, ODataCollectionValue, ODataPubFormat> {

    public ListsOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            @SuppressWarnings("unchecked")
            ODataEntity entity = ((ODataRetrieveResponse<ODataEntity>) response).getBody();
            for (ODataProperty p : entity.getProperties()) {
                mResult = p.getComplexValue().get(SHAREPOINT_RESULTS_FIELD_NAME).getCollectionValue();
            }

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_LISTS_URL);
    }
}
