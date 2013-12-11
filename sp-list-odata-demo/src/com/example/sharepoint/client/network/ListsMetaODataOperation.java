package com.example.sharepoint.client.network;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataMetadataRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.data.metadata.EdmMetadata;
import com.msopentech.odatajclient.engine.data.metadata.edm.Schema;

/**
 * SP Lists list retrieval operation.
 */
public class ListsMetaODataOperation extends HttpOperation {

    public ListsMetaODataOperation(OnOperaionExecutionListener listener, AuthType authType, Context context) {
        super(listener, authType, context);
    }

    protected boolean handleServerResponse(EdmMetadata response) {
        try {
            StringBuilder schemas = new StringBuilder();

            for (Schema schema : response.getSchemas()) {
                schemas.append(schema.getNamespace() + "\n");
            }

            this.mResponse = schemas.toString();

            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    @Override
    public void execute() {
        try {
            ODataMetadataRequest req = ODataRetrieveRequestFactory.getMetadataRequest(Constants.SP_METADATA);

            if (authType == AuthType.Office365) {
                req.addCustomHeader("Cookie", Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH);
            }

            EdmMetadata metadata = req.execute().getBody();

            boolean result = handleServerResponse(metadata);
            if (mListener != null) mListener.onExecutionComplete(this, result);

        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".execute(): Error.");
            if (mListener != null) mListener.onExecutionComplete(this, false);
        }
    }

    @Override
    protected String getServerUrl() {
        return null;
    }
}
