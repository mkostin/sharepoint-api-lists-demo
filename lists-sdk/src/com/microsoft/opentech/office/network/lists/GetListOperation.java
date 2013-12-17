package com.microsoft.opentech.office.network.lists;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class GetListOperation extends ODataOperation<ODataEntityRequest, ODataEntity, ODataPubFormat> {

    private String mGUID;
    
    public GetListOperation(OnOperaionExecutionListener listener, Context context, String guid) {
        super(listener, context);
        mGUID = guid;
    }
    
    @Override
    protected ODataEntityRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        return ODataRetrieveRequestFactory.getEntityRequest(getServerUrl());
    }
    
    @Override
    protected URI getServerUrl() {
        String url = super.getServerUrl() + "Web/Lists(guid'" + mGUID + "')";
        return URI.create(url);
    }
}
