package com.microsoft.opentech.office.lists.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.core.Configuration;
import com.microsoft.opentech.office.core.network.odata.ODataOperation;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.IEntityBuilder;
import com.microsoft.opentech.office.core.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Operation to create an item in specified list and returns created {@link Entity} as result.
 */
public class CreateListItemOperation extends ODataOperation<ODataEntityCreateRequest, Entity, ODataPubFormat> {

    private String listGUID;

    private IEntityBuilder<Entity> mBuilder;

    /**
     * Creates a new instance of {@link CreateListItemOperation} class.
     * 
     * @param listener Callback to be invoked when operation finishes.
     * @param context Application context.
     * @param listId GUID of list to create an item.
     * @param entityBuilder Builder of entity to be created.
     */
    public CreateListItemOperation(ICallback<Entity> listener, Context context, String listId, IEntityBuilder<Entity> entityBuilder) {
        super(listener, context, true);
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
