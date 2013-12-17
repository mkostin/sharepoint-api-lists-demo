package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.odata.ODataEntityBuilder;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class UpdateListItemOperation extends ODataOperation<ODataEntityUpdateRequest, ODataEntity, ODataPubFormat> {

    private ODataComplexValue entity;

    public UpdateListItemOperation(OnOperaionExecutionListener listener, Context context, ODataComplexValue entity) {
        super(listener, context);
        this.entity = entity;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_LISTS_URL);
    }

    @Override
    protected ODataEntityUpdateRequest getRequest() {
        // remove attached image, for example
        String entityId = entity.get(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue().get(SHAREPOINT_METADATA_ID_FIELD_NAME).getPrimitiveValue()
                .toString();
        String entityUrl = entity.get(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue().get(SHAREPOINT_URI_FIELD_NAME).getPrimitiveValue().toString();
        String etag = entity.get(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue().get(SHAREPOINT_ETAG_FIELD_NAME).getPrimitiveValue().toString();
        String entityType = entity.get(SHAREPOINT_METADATA_FIELD_NAME).getComplexValue().get(SHAREPOINT_TYPE_FIELD_NAME).getPrimitiveValue()
                .toString();

        ODataEntity updatedEntity = ODataEntityBuilder.newEntity(entityType).add(SHAREPOINT_IMAGE_FIELD_NAME, null).
                addMetadataProperty(SHAREPOINT_METADATA_ID_FIELD_NAME, entityId).
                addMetadataProperty(SHAREPOINT_URI_FIELD_NAME, entityUrl).build();
        
        URI uri = URI.create(entityUrl);
        ODataEntityUpdateRequest request = ODataCUDRequestFactory.getEntityUpdateRequest(uri, UpdateType.MERGE, updatedEntity);
        request.setIfMatch(etag); // without this server returns 412 Precondition Failed

        this.mResult = updatedEntity; // save it for future operation
        return request;
    }
}
