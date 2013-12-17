package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataDeleteRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class ListDeleteEntityOperation extends ODataOperation<ODataDeleteRequest, Boolean, ODataPubFormat> {

    private ODataEntity entity;

    public ListDeleteEntityOperation(OnOperaionExecutionListener listener, Context context, ODataEntity entity) {
        super(listener, context);
        this.entity = entity;
    }

    @Override
    protected ODataDeleteRequest getRequest() {
        // reread entity to get new etag value
        ODataEntityRequest entityReq = ODataRetrieveRequestFactory.getEntityRequest(URI.create(entity
                .getProperty(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue()
                .get(SHAREPOINT_URI_FIELD_NAME).getPrimitiveValue().toString()));

        setRequestHeaders(entityReq, getRequestHeaders());

        String etag = entityReq.execute().getBody().getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue()
                .get(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue()
                .get(SHAREPOINT_ETAG_FIELD_NAME).getPrimitiveValue().toString();

        ODataDeleteRequest request = ODataCUDRequestFactory.getDeleteRequest(URI.create(entity
                .getProperty(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue()
                .get(SHAREPOINT_URI_FIELD_NAME).getPrimitiveValue().toString()));
        request.setIfMatch(etag); // TODO check can we use "*" value

        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse res) {
        try {
            mResult = true;
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }
        return false;
    }
}
