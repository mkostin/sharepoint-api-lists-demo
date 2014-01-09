package com.microsoft.opentech.office.mail.network;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import android.content.Context;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.Configuration;
import com.microsoft.opentech.office.core.odata.ODataOperation;
import com.microsoft.opentech.office.mail.data.EmailParser;
import com.microsoft.opentech.office.mail.data.odata.EmailMessage;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class MailsRequestOperation extends ODataOperation<ODataEntitySetRequest, List<EmailMessage>, ODataPubFormat> {
    
    private final static String PATH_TO_INBOX_FOLDER = "OData/Me/Inbox/Messages";

    public MailsRequestOperation(IOperationCallback<List<EmailMessage>> listener, Context context) {
        super(listener, context, false);
    }

    @Override
    protected ODataEntitySetRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        return ODataRetrieveRequestFactory.getEntitySetRequest(getServerUrl());
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl() + PATH_TO_INBOX_FOLDER);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            ODataRetrieveResponse<ODataEntitySet> resp = (ODataRetrieveResponse<ODataEntitySet>)response;
            mResult = new EmailParser(resp.getBody().getEntities()).getEmails();
            return true;
        }
        catch (Exception e) {
            // throw?
            return false;
        }
    }
}
