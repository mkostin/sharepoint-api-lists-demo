package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;
import android.util.Base64;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataCUDRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityCreateResponse;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.uri.ODataURIBuilder;

public class ListCreateEntityOperation extends HttpOperation {

    private ODataEntity list;

    private boolean result = false;

    public ListCreateEntityOperation(OnOperaionExecutionListener listener, AuthType authType, Context context, ODataEntity list) {
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
            headers.add(new BasicHeader("Accept", "application/json;odata=verbose"));
            headers.add(new BasicHeader("Content-Type", "application/json;odata=verbose")); // need to override default value

            if (authType == AuthType.Office365) {
                headers.add(new BasicHeader("Cookie", Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH));
            } else if (authType == AuthType.Basic) {
                headers.add(new BasicHeader("Authorization", "Basic "
                        + Base64.encodeToString((Constants.USERNAME + ":" + Constants.PASSWORD).getBytes(), Base64.DEFAULT).trim()));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    @Override
    public void execute() {
        try {
            ODataComplexValue properties = list.getProperty("d").getComplexValue();
            String entityType = properties.get("ListItemEntityTypeFullName").getValue().asPrimitive().toString();
            String listTitle = properties.get("Title").getValue().asPrimitive().toString();

            ODataEntity newItem = ODataFactory.newEntity(entityType);

            ODataComplexValue metadata = new ODataComplexValue("__metadata");
            metadata.add(ODataFactory.newPrimitiveProperty("type", new ODataPrimitiveValue.Builder().setValue(entityType).build()));

            newItem.addProperty(ODataFactory.newComplexProperty("__metadata", metadata));

            newItem.addProperty(ODataFactory.newPrimitiveProperty("Title", new ODataPrimitiveValue.Builder().setText("test").build()));

            ODataURIBuilder uriBuilder = new ODataURIBuilder(getServerUrl() + "/Web/Lists/GetByTitle('" + listTitle + "')/Items");
            ODataEntityCreateRequest req = ODataCUDRequestFactory.getEntityCreateRequest(uriBuilder.build(), newItem);

            // an image

            final String imageUrl = "https://www.google.ru/images/srpr/logo11w.png";

            ODataComplexValue image = new ODataComplexValue("");
            image.add(ODataFactory.newPrimitiveProperty("Url", new ODataPrimitiveValue.Builder().setText(imageUrl).build()));
            image.add(ODataFactory.newPrimitiveProperty("Description", new ODataPrimitiveValue.Builder().setText("Image example").build()));
            metadata = new ODataComplexValue("__metadata");
            metadata.add(ODataFactory.newPrimitiveProperty("type", new ODataPrimitiveValue.Builder().setValue("SP.FieldUrlValue").build()));
            image.add(ODataFactory.newComplexProperty("__metadata", metadata));
            newItem.addProperty(ODataFactory.newComplexProperty("Image", image));

            for (Header header : getRequestHeaders()) {
                req.addCustomHeader(header.getName(), header.getValue());

            }

            GetRequestDigestOperation digestOper = new GetRequestDigestOperation(null, authType, context);
            digestOper.execute();
            req.addCustomHeader("X-RequestDigest", digestOper.getResponse());

            ODataEntityCreateResponse res = req.execute();
            result = handleServerResponse(res);

            if (mListener != null) {
                mListener.onExecutionComplete(this, result);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".execute(): Error.");
        }
    }

    protected boolean handleServerResponse(ODataEntityCreateResponse res) {
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
