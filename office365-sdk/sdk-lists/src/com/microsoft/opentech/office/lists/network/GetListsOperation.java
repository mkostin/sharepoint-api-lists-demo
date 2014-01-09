package com.microsoft.opentech.office.lists.network;

import java.net.URI;
import java.util.List;

import android.content.Context;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.ODataOperation;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Gets all lists on server and returns them as a collection.
 */
public class GetListsOperation extends ODataOperation<ODataEntityRequest, List<Object>, ODataPubFormat> {

    /**
     * Creates a new instance of {@link GetListsOperation} class.
     * 
     * @param listener Callback to be executed when operation finishes.
     * @param context Application context.
     */
    public GetListsOperation(IOperationCallback<List<Object>> listener, Context context) {
        super(listener, context, false);
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @SuppressWarnings("unchecked")
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
