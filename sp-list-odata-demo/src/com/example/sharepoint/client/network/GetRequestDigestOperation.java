package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import android.content.Context;
import android.util.Base64;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;

public class GetRequestDigestOperation extends HttpOperation {

    public GetRequestDigestOperation(OnOperaionExecutionListener listener, AuthType authType, Context context) {
        super(listener, authType, context);
    }

    @Override
    protected String getServerUrl() {
        return Constants.SP_BASE_URL + "/contextinfo";
    }
    
    @Override
    protected boolean handleServerResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            this.mResponse = json.getJSONObject("d").getJSONObject("GetContextWebInformation").getString("FormDigestValue");
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }
        
        return false;
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
    protected Object getPostData() {
        return "";
    }
}
