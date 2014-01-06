package com.microsoft.opentech.office.lists.network;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.core.Configuration;
import com.microsoft.opentech.office.core.network.odata.ODataOperation;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.IBuilder;
import com.microsoft.opentech.office.core.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Creates a list on server and return created list as {@link Entity} as result.
 */
public class CreateListOperation extends ODataOperation<ODataEntityCreateRequest, Entity, ODataPubFormat> {

    private IBuilder<Entity> mListBuilder;

    /**
     * Creates a new instance of {@link CreateListOperation} class.
     * 
     * @param listener Callback to be invoked when operation finishes.
     * @param context Application context.
     * @param listBuilder Builder to build a list to be created.
     */
    public CreateListOperation(ICallback<Entity> listener, Context context, IBuilder<Entity> listBuilder) {
        super(listener, context, true);
        mListBuilder = listBuilder;
    }

    @Override
    protected ODataEntityCreateRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        return ODataCUDRequestFactory.getEntityCreateRequest(getServerUrl(), getODataEntity(mListBuilder.build()));
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        if (!(response instanceof ODataEntityCreateResponse)) {
            return false;
        }
        ODataEntityCreateResponse res = (ODataEntityCreateResponse) response;
        this.mResult = Entity.from(res.getBody()).build();
        return true;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX);
    }
}
