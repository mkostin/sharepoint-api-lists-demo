package com.example.sharepoint.client.network;

import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;

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
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
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
            headers.add(new BasicHeader("Accept", "application/json; odata=verbose"));
            headers.add(new BasicHeader("Content-Type", "application/json; odata=verbose")); // need to override default value

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
        ODataComplexValue properties = list.getProperties().get(0).getComplexValue();
        String entityType = null;
        String listId = null;
        Iterator<ODataProperty> iter = properties.iterator();
        while (iter.hasNext()) {
            ODataProperty current = iter.next();
            if ("ListItemEntityTypeFullName".equals(current.getName())) {
                entityType = current.getValue().asPrimitive().toString();
            } else if ("Id".equals(current.getName())) {
                listId = current.getValue().asPrimitive().toString();
            }
        }

        // following code produces such json:
//        {
//            "odata.type": "SP.Data.TestListItem",
//            "__metadata": {
//              "odata.type": "__metadata",
//              "type@odata.type": "Edm.String",
//              "type": "SP.Data.TestListItem"
//            },
//            "Title@odata.type": "Edm.String",
//            "Title": "test value",
//            "Description@odata.type": "Edm.String",
//            "Description": "test",
//            "LinkLocation": {
//              "odata.type": "SP.FieldUrlValue",
//              "__metadata": {
//                "odata.type": "__metadata",
//                "type@odata.type": "Edm.String",
//                "type": "SP.FieldUrlValue"
//              },
//              "Description@odata.type": "Edm.String",
//              "Description": "test",
//              "Url@odata.type": "Edm.String",
//              "Url": "http://microsoft.com"
//            },
//            "LaunchBehavior@odata.type": "Edm.String",
//            "LaunchBehavior": "New tab"
//          }
        // i found it correct using Poster Firefox extension but server still returns 400 BadRequest so error is not in body
        
        ODataEntity newItem = ODataFactory.newEntity(entityType);
        
        ODataComplexValue metadata = new ODataComplexValue("__metadata");
        metadata.add(ODataFactory.newPrimitiveProperty("type", new ODataPrimitiveValue.Builder().setValue(entityType).build()));
        
        newItem.addProperty(ODataFactory.newComplexProperty("__metadata", metadata));
        
        newItem.addProperty(ODataFactory.newPrimitiveProperty("Title",
                new ODataPrimitiveValue.Builder().setValue("test value").build()));
        newItem.addProperty(ODataFactory.newPrimitiveProperty("Description",
                new ODataPrimitiveValue.Builder().setValue("test").build()));

        ODataComplexValue linkLocation = new ODataComplexValue("SP.FieldUrlValue");
        
        metadata = new ODataComplexValue("__metadata");
        metadata.add(ODataFactory.newPrimitiveProperty("type", new ODataPrimitiveValue.Builder().setValue("SP.FieldUrlValue").build()));
        linkLocation.add(ODataFactory.newComplexProperty("__metadata", metadata));
        
        linkLocation.add(ODataFactory.newPrimitiveProperty("Description",
                new ODataPrimitiveValue.Builder().setValue("test").build()));
        linkLocation.add(ODataFactory.newPrimitiveProperty("Url",
                new ODataPrimitiveValue.Builder().setValue("http://microsoft.com").build()));
        newItem.addProperty(ODataFactory.newComplexProperty("LinkLocation", linkLocation));

        newItem.addProperty(ODataFactory.newPrimitiveProperty("LaunchBehavior",
                new ODataPrimitiveValue.Builder().setValue("New tab").build()));

        ODataURIBuilder uriBuilder = new ODataURIBuilder(getServerUrl() + "Web/Lists(guid'" + listId + "')/Items");
        ODataEntityCreateRequest req = ODataCUDRequestFactory.getEntityCreateRequest(uriBuilder.build(), newItem);

        for (Header header : getRequestHeaders()) {
            req.addCustomHeader(header.getName(), header.getValue());
        }
        ODataEntityCreateResponse res = req.execute();
        result = handleServerResponse(res);

        if (mListener != null) {
            mListener.onExecutionComplete(this, result);
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
