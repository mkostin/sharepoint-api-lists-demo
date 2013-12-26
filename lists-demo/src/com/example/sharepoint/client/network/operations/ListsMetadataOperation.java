package com.example.sharepoint.client.network.operations;

import java.net.URI;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.microsoft.opentech.office.network.odata.ODataOperation;
import com.microsoft.opentech.office.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataMetadataRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.metadata.EdmMetadata;
import com.msopentech.odatajclient.engine.data.metadata.edm.Schema;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * SP Lists list retrieval operation.
 */
public class ListsMetadataOperation extends ODataOperation<ODataMetadataRequest, String, ODataPubFormat> {

    public ListsMetadataOperation(ICallback<String> listener, Context context) {
        super(listener, context);
    }

    @Override
    protected boolean handleServerResponse(ODataResponse response) {
        try {
            @SuppressWarnings("unchecked")
            EdmMetadata metadata = ((ODataRetrieveResponse<EdmMetadata>) response).getBody();
            StringBuilder schemas = new StringBuilder();

            for (Schema schema : metadata.getSchemas()) {
                schemas.append(schema.getNamespace() + "\n");
            }

            this.mResult = schemas.toString();

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    @Override
    protected ODataMetadataRequest getRequest() {
        ODataMetadataRequest request = ODataRetrieveRequestFactory.getMetadataRequest(getServerUrl().toString());
        return request;
    }

    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_METADATA);
    }
}
