package com.microsoft.opentech.office.network.lists;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import android.content.Context;

import com.microsoft.opentech.office.Configuration;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class CreateListOperation extends ODataOperation<ODataEntityCreateRequest, ODataEntity, ODataPubFormat> {
    
    private ODataEntity mList;
    
    public CreateListOperation(OnOperaionExecutionListener listener, Context context, ODataEntity list) {
        super(listener, context);
        mList = list;
    }
    
    @Override
    protected ODataEntityCreateRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        return ODataCUDRequestFactory.getEntityCreateRequest(getServerUrl(), mList);
    }
    
    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        if (!(response instanceof ODataEntityCreateResponse)) {
            return false;
        }
        ODataEntityCreateResponse res = (ODataEntityCreateResponse) response;
        this.mResult = res.getBody();
        return true;
    }    
    
    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + SHAREPOINT_LISTS_URL_SUFFIX);
    }
}
