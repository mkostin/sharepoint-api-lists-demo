package com.microsoft.opentech.office.lists.network;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.Configuration;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.ODataOperation;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Gets an item from server and returns it as {@link Entity}.
 */
public class GetItemOperation extends ODataOperation<ODataEntityRequest, Entity, ODataPubFormat> {

    private String mListGUID;

    private int mItemId;

    /**
     * Creates a new instance of {@link GetItemOperation} class.
     * 
     * @param listener Callback to be invoked when operation finishes.
     * @param context Application context.
     * @param listGUID GUID of list to retrieve an item.
     * @param id Item ID.
     */
    public GetItemOperation(IOperationCallback<Entity> listener, Context context, String listGUID, int id) {
        super(listener, context, false);
        mListGUID = listGUID;
        mItemId = id;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mListGUID + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX
                + "(" + mItemId + ")");
    }

    @SuppressWarnings("unchecked")
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
