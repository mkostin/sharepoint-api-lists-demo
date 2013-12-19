package com.example.sharepoint.client.network.operations;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.ComplexValue;
import com.microsoft.opentech.office.odata.EntityBuilder;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class ListCreateEntityOperationOld extends ODataOperation<ODataEntityCreateRequest, ODataComplexValue, ODataPubFormat> {

    private ODataEntity list;

    public ListCreateEntityOperationOld(OnOperaionExecutionListener listener, Context context, ODataEntity list) {
        super(listener, context);
        this.list = list;
    }

    @Override
    protected ODataEntityCreateRequest getRequest() {
        ODataComplexValue properties = list.getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue();
        String entityType = properties.get(SHAREPOINT_LIST_ITEM_ENTITY_TYPE_FULL_NAME_FIELD_NAME).getValue().asPrimitive().toString();
        String listTitle = properties.get(SHAREPOINT_TITLE_FIELD_NAME).getValue().asPrimitive().toString();

//        EntityBuilder builder = EntityBuilder.newEntity(entityType).set(SHAREPOINT_TITLE_FIELD_NAME, "test");
//        
        // an image
        final String imageUrl = "https://www.google.ru/images/srpr/logo11w.png";
//
//        ODataComplexValue image = new ODataComplexValue("");
//        image.add(ODataFactory.newPrimitiveProperty(SHAREPOINT_FIELD_URL_DATA_TYPE, new ODataPrimitiveValue.Builder().setText(imageUrl).build()));
//        image.add(ODataFactory.newPrimitiveProperty(SHAREPOINT_DESCRIPTION_FIELD_NAME, new ODataPrimitiveValue.Builder().setText("Image example")
//                .build()));
//        image.add(ODataFactory.newComplexProperty(SHAREPOINT_METADATA_FIELD_NAME, generateMetadata(SHAREPOINT_FIELD_URL_DATA_TYPE, null)));
//        builder.set(SHAREPOINT_IMAGE_FIELD_NAME, image);
        
        ComplexValue image = new ComplexValue().set(SHAREPOINT_FIELD_URL_DATA_TYPE, imageUrl).
                set(SHAREPOINT_DESCRIPTION_FIELD_NAME, "Image example");
        
        EntityBuilder builder = EntityBuilder.newEntity(entityType).set(SHAREPOINT_TITLE_FIELD_NAME, "test").
                set(SHAREPOINT_IMAGE_FIELD_NAME, image);
        String uri = getServerUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "/GetByTitle('" + listTitle + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX;

        ODataEntityCreateRequest request = ODataCUDRequestFactory.getEntityCreateRequest(URI.create(uri), builder.build().asODataEntity());

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
