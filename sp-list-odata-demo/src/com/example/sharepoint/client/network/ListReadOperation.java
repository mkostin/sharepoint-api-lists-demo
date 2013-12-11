package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;
import android.util.Base64;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.uri.ODataURIBuilder;

public class ListReadOperation extends HttpOperation {

    private String guid;

    private int items = 0;
    
    private ODataEntity result = null;

    public ListReadOperation(OnOperaionExecutionListener listener, AuthType authType, Context context, String guid) {
        super(listener, authType, context);
        this.guid = guid;
    }

    @Override
    protected List<Header> getRequestHeaders() {
        List<Header> headers = super.getRequestHeaders();
        try {
            headers.add(new BasicHeader("Accept", "application/json; odata=verbose"));

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
    protected String getServerUrl() {
        return Constants.SP_BASE_URL;
    }

    @Override
    public void execute() {
        try {
            ODataURIBuilder builder = new ODataURIBuilder(getServerUrl()).appendEntityTypeSegment("Web/Lists").appendKeySegment(guid);
            ODataEntityRequest req = ODataRetrieveRequestFactory.getEntityRequest(builder.build());
            for (Header h : getRequestHeaders()) {
                req.addCustomHeader(h.getName(), h.getValue());
            }

            ODataEntity res = req.execute().getBody();

            boolean isSucceeded = handleServerResponse(res);

            if (mListener != null) {
                mListener.onExecutionComplete(this, isSucceeded);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".execute(): Error.");
        }
    }

    private boolean handleServerResponse(ODataEntity res) {
        try {
            items = Integer.valueOf(res.getProperty("d").getComplexValue().get("ItemCount").getPrimitiveValue().toString());
            result = res;
            
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }

        return false;
    }

    public ODataEntity getResult() {
        return result;
    }
    
    public int getItemsCount() {
        return items;
    }
}
