package com.example.sharepoint.client.network;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.odata.ODataEntityBuilder;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class CreateListOperation extends ODataOperation<ODataEntityCreateRequest, ODataEntity, ODataPubFormat> {

    public CreateListOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_LISTS_URL);
    }

    @Override
    protected ODataEntityCreateRequest getRequest() {
        ODataEntityBuilder builder = ODataEntityBuilder.newEntity(SHAREPOINT_LIST_DATA_TYPE).
                add(SHAREPOINT_BASE_TEMPLATE_FIELD_NAME, 100).
                add(SHAREPOINT_DESCRIPTION_FIELD_NAME, "Create list using api sample").
                add(SHAREPOINT_TITLE_FIELD_NAME, "List, created using API");
        ODataEntityCreateRequest request = ODataCUDRequestFactory.getEntityCreateRequest(getServerUrl(), builder.build());

        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            mResult = ((ODataEntityCreateResponse) response).getBody();
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

}
