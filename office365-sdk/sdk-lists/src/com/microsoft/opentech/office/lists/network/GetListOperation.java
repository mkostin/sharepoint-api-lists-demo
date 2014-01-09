package com.microsoft.opentech.office.lists.network;

import java.io.UnsupportedEncodingException;
import java.net.URI;

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
 * Gets a list from server and returns list as an {@link Entity}.
 */
public class GetListOperation extends ODataOperation<ODataEntityRequest, Entity, ODataPubFormat> {

    private String mGUID;

    /**
     * Creates a new instance of {@link GetListOperation} class.
     * 
     * @param listener Callback to be executed when operation finishes.
     * @param context Application context.
     * @param guid List GUID.
     */
    public GetListOperation(IOperationCallback<Entity> listener, Context context, String guid) {
        super(listener, context, false);
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

    @SuppressWarnings("unchecked")
    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        mResult = Entity.from(((ODataRetrieveResponse<ODataEntity>) response).getBody()).build();
        return true;
    }
}
