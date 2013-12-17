package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.odata.ODataEntityBuilder;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class ListCreateEntityOperation extends ODataOperation<ODataEntityCreateRequest, ODataComplexValue, ODataPubFormat> {

    private ODataEntity list;

    public ListCreateEntityOperation(OnOperaionExecutionListener listener, Context context, ODataEntity list) {
        super(listener, context);
        this.list = list;
    }

    @Override
    protected ODataEntityCreateRequest getRequest() {
        ODataComplexValue properties = list.getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue();
        String entityType = properties.get(SHAREPOINT_LIST_ITEM_ENTITY_TYPE_FULL_NAME_FIELD_NAME).getValue().asPrimitive().toString();
        String listTitle = properties.get(SHAREPOINT_TITLE_FIELD_NAME).getValue().asPrimitive().toString();

        ODataEntityBuilder builder = ODataEntityBuilder.newEntity(entityType).add(SHAREPOINT_TITLE_FIELD_NAME, "test");
        
        // an image
        final String imageUrl = "https://www.google.ru/images/srpr/logo11w.png";

        ODataComplexValue image = new ODataComplexValue("");
        image.add(ODataFactory.newPrimitiveProperty(SHAREPOINT_FIELD_URL_DATA_TYPE, new ODataPrimitiveValue.Builder().setText(imageUrl).build()));
        image.add(ODataFactory.newPrimitiveProperty(SHAREPOINT_DESCRIPTION_FIELD_NAME, new ODataPrimitiveValue.Builder().setText("Image example")
                .build()));
        image.add(ODataFactory.newComplexProperty(SHAREPOINT_METADATA_FIELD_NAME, generateMetadata(SHAREPOINT_FIELD_URL_DATA_TYPE, null)));
        builder.add(SHAREPOINT_IMAGE_FIELD_NAME, image);
        String uri = getServerUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "/GetByTitle('" + listTitle + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX;

        ODataEntityCreateRequest request = ODataCUDRequestFactory.getEntityCreateRequest(URI.create(uri), builder.build());

        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse res) {
        try {
            mResult = ((ODataEntityCreateResponse) res).getBody().getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue();
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

}
