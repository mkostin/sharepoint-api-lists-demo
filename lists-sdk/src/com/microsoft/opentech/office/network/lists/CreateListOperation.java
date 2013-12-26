package com.microsoft.opentech.office.network.lists;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.Entity;
import com.microsoft.opentech.office.odata.IBuilder;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class CreateListOperation extends ODataOperation<ODataEntityCreateRequest, Entity, ODataPubFormat> {

    private IBuilder<Entity> mListBuilder;

    public CreateListOperation(OnOperaionExecutionListener listener, Context context, IBuilder<Entity> listBuilder) {
        super(listener, context);
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
