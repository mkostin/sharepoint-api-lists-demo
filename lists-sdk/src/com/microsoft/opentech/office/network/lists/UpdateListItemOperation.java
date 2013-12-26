package com.microsoft.opentech.office.network.lists;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.IEntityBuilder;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class UpdateListItemOperation extends ODataOperation<ODataEntityUpdateRequest, Boolean, ODataPubFormat> {

    private String mListGuid;

    private int mItemId;

    private IEntityBuilder<Entity> mBuilder;

    public UpdateListItemOperation(OnOperaionExecutionListener listener, Context context, String listGuid, int itemId, IEntityBuilder<Entity> builder) {
        super(listener, context);
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
