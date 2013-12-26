package com.microsoft.opentech.office.network.lists;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.IEntityBuilder;
import com.microsoft.opentech.office.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class CreateListItemOperation extends ODataOperation<ODataEntityCreateRequest, Entity, ODataPubFormat> {

    private String listGUID;

    private IEntityBuilder<Entity> mBuilder;

    public CreateListItemOperation(ICallback<Entity> listener, Context context, String listId, IEntityBuilder<Entity> entityBuilder) {
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
        Entity list = getListOper.getResult();
        mBuilder.setMeta(SHAREPOINT_TYPE_FIELD_NAME, list.get(SHAREPOINT_LIST_ITEM_ENTITY_TYPE_FULL_NAME_FIELD_NAME).toString());

        ODataEntityCreateRequest request = ODataCUDRequestFactory.getEntityCreateRequest(getServerUrl(), getODataEntity(mBuilder.build()));

        return request;
    }

    @Override
    protected boolean handleServerResponse(ODataResponse res) {
        mResult = Entity.from(((ODataEntityCreateResponse) res).getBody()).build();
        return true;
    }

    @Override
    protected URI getServerUrl() {
        String url = Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX + "(guid'" + listGUID + "')/" + SHAREPOINT_ITEMS_URL_SUFFIX;
        return URI.create(url);
    }

}
