package com.microsoft.opentech.office.lists.network;

import java.net.URI;
import java.util.ArrayList;
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
 * Gets all items in specified list and returns them as collection.
 */
public class GetListItemsOperation extends ODataOperation<ODataEntityRequest, List<Object>, ODataPubFormat> {

    private String mGUID;

    /**
     * Creates a new instance of {@link GetListItemsOperation} class.
     * 
     * @param listener Callback to be executed when operation finishes.
     * @param context Application context.
     * @param guid List GUID.
     */
    public GetListItemsOperation(IOperationCallback<List<Object>> listener, Context context, String guid) {
        super(listener, context, false);
        this.mGUID = guid;
    }

    @Override
    protected ODataEntityRequest getRequest() {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }

    @SuppressWarnings("unchecked")
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
