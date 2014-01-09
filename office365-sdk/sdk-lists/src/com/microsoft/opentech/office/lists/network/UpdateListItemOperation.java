package com.microsoft.opentech.office.lists.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.Configuration;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.IEntityBuilder;
import com.microsoft.opentech.office.core.odata.ODataOperation;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Updates specified item in specified list and returns a value indicating about success.
 */
public class UpdateListItemOperation extends ODataOperation<ODataEntityUpdateRequest, Boolean, ODataPubFormat> {

    private String mListGuid;

    private int mItemId;

    private IEntityBuilder<Entity> mBuilder;

    /**
     * Creates a new instance of {@link UpdateListItemOperation} class.
     * 
     * @param listener Callback to be executed when operation finishes.
     * @param context Application context.
     * @param listGuid List GUID.
     * @param itemId Item ID.
     * @param builder Builder with fields to be updated.
     */
    public UpdateListItemOperation(IOperationCallback<Boolean> listener, Context context, String listGuid, int itemId, IEntityBuilder<Entity> builder) {
        super(listener, context, true);
        mListGuid = listGuid;
        mItemId = itemId;
        mBuilder = builder;
        mResult = false;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mListGuid + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX
                + "(" + mItemId + ")");
    }

    @Override
    protected ODataEntityUpdateRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        GetItemOperation getItemOper = new GetItemOperation(null, mContext, mListGuid, mItemId);
        try {
            getItemOper.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Entity oldItem = getItemOper.getResult();

        mBuilder.setMeta(SHAREPOINT_TYPE_FIELD_NAME, oldItem.getMeta(SHAREPOINT_TYPE_FIELD_NAME));

        ODataEntityUpdateRequest req = ODataCUDRequestFactory.getEntityUpdateRequest(getServerUrl(), UpdateType.PATCH,
                getODataEntity(mBuilder.build()));
        req.setIfMatch(ETAG_ANY_ETAG);
        return req;
    }
    
    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        mResult = true;
        return true;
    }
}
