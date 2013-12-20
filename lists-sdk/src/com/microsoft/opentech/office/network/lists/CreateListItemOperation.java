package com.microsoft.opentech.office.network.lists;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.EntityBuilder;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class CreateListItemOperation extends ODataOperation<ODataEntityCreateRequest, ODataEntity, ODataPubFormat> {

    private String listGUID;

    private EntityBuilder mBuilder;

    public CreateListItemOperation(OnOperaionExecutionListener listener, Context context, String listId, EntityBuilder entityBuilder) {
        super(listener, context);
        listGUID = listId;
        mBuilder = entityBuilder;
    }

    @Override
    protected ODataEntityCreateRequest getRequest() throws UnsupportedOperationException, UnsupportedEncodingException {
        GetListOperation getListOper = new GetListOperation(null, mContext, listGUID);
        try {
            getListOper.execute();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
        ODataEntity list = getListOper.getResult();
        mBuilder.setMeta(SHAREPOINT_TYPE_FIELD_NAME,
                list.getProperty(SHAREPOINT_ROOT_OBJECT_NAME).getComplexValue().get(SHAREPOINT_LIST_ITEM_ENTITY_TYPE_FULL_NAME_FIELD_NAME)
                        .getPrimitiveValue().toString());

        ODataEntityCreateRequest request = ODataCUDRequestFactory.getEntityCreateRequest(getServerUrl(), mBuilder.build().asODataEntity());

        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse res) {
        mResult = ((ODataEntityCreateResponse) res).getBody();
        return true;
    }

    @Override
    protected URI getServerUrl() {
        String url = Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + listGUID + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX;
        return URI.create(url);
    }

}
