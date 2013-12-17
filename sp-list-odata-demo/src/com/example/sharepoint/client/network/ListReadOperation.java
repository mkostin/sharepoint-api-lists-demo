package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class ListReadOperation extends ODataOperation<ODataEntityRequest, ODataEntity, ODataPubFormat> {

    private String mGUID;

    private int mItems = 0;

    public ListReadOperation(OnOperaionExecutionListener listener, Context context, String guid) {
        super(listener, context);
        this.mGUID = guid;
    }

    @Override
    protected ODataEntityRequest getRequest() {
        String uri = getServerUrl().toString() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + mGUID + "')";
        return ODataRetrieveRequestFactory.getEntityRequest(URI.create(uri));
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            ODataEntity res = ((ODataRetrieveResponse<ODataEntity>) response).getBody();
            mItems = Integer.valueOf(res.getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue().get(SHAREPOINT_ITEM_COUNT_FIELD_NAME)
                    .getPrimitiveValue().toString());
            mResult = res;

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    /**
     * Returns list intems count.
     * 
     * @return items count.
     */
    public int getItemsCount() {
        return mItems;
    }
}
