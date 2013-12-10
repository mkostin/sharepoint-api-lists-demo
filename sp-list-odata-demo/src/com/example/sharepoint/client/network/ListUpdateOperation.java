package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class ListUpdateOperation extends HttpOperation {

    private ODataEntity list;

    private boolean result = false;

    public ListUpdateOperation(OnOperaionExecutionListener listener, AuthType authType, Context context, ODataEntity list) {
        super(listener, authType, context);
        this.list = list;
    }

    @Override
    protected String getServerUrl() {
        return Constants.SP_BASE_URL;
    }

    @Override
    protected List<Header> getRequestHeaders() {
        List<Header> headers = super.getRequestHeaders();
        try {
            headers.add(new BasicHeader("Accept", "application/json; odata=verbose"));

            if (authType == AuthType.Office365) {
                headers.add(new BasicHeader("Cookie", Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    @Override
    public void execute() {
        try {
            
            ODataComplexValue newValue = new ODataComplexValue("SP.Data.TestListItem"); // TODO fix hardcoded type
            newValue.add(ODataFactory.newPrimitiveProperty("Title", new ODataPrimitiveValue.Builder().setType(EdmSimpleType.String).setText("test")
                    .build()));

            ODataComplexValue newEntity = new ODataComplexValue("SP.FieldUrlValue"); // TODO fix hardcoded type
            newEntity.add(ODataFactory.newPrimitiveProperty("Description",
                    new ODataPrimitiveValue.Builder().setText("test test test").setType(EdmSimpleType.String).build()));
            newEntity.add(ODataFactory.newPrimitiveProperty("Url",
                    new ODataPrimitiveValue.Builder().setText("http://microsoft.com").setType(EdmSimpleType.String).build()));
            
            newValue.add(ODataFactory.newComplexProperty("test", newEntity));
            
            list.addProperty(ODataFactory.newComplexProperty("test", newValue));

             ODataEntityUpdateRequest req = ODataCUDRequestFactory.getEntityUpdateRequest(UpdateType.MERGE, list);
             for (Header header: getRequestHeaders()) {
             req.addCustomHeader(header.getName(), header.getValue());
             }
             req.setFormat(ODataPubFormat.JSON_VERBOSE_METADATA);
             ODataEntityUpdateResponse res = req.execute();

            boolean isSucceeded = handleServerResponse(res);
            if (mListener != null) {
                mListener.onExecutionComplete(this, isSucceeded);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".execute(): Error.");
        }
    }

    protected boolean handleServerResponse(ODataEntityUpdateResponse response) {
        try {
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    public boolean getResult() {
        return result;
    }
}
