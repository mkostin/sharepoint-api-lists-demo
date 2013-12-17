package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class RemoveListOperation extends ODataOperation<ODataDeleteRequest, Boolean, ODataPubFormat> {

    private ODataEntity mList;

    public RemoveListOperation(OnOperaionExecutionListener listener, Context context, ODataEntity entity) {
        super(listener, context);
        mList = entity;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_LISTS_URL);
    }

    @Override
    protected ODataDeleteRequest getRequest() {
        // FIX: will it work w/o server URL?
        String uri = mList.getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue().get(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue()
                .get(SHAREPOINT_URI_FIELD_NAME).getPrimitiveValue().toString();
        ODataDeleteRequest request = ODataCUDRequestFactory.getDeleteRequest(URI.create(uri));
        request.setIfMatch(ETAG_ANY_ETAG);

        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            // TODO: verify operation was successful.
            mResult = true;
            return mResult;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

}
