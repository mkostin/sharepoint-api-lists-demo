/**
 *
 */
package com.example.sharepoint.client.network;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.example.sharepoint.client.network.auth.AuthType;

import android.content.Context;
import android.util.Pair;

/**
 * Implements standard HTTP operation. Has common fields for all operations.
 *
 * @param <REQUEST> Request.
 * @param <RESPONSE> Response type retrieved from network.
 * @param <RESULT> Operation execution result.
 */
public abstract class NetworkOperation<REQUEST, RESPONSE, RESULT> extends BaseOperation<RESULT> {
    
    protected static final String ACCEPT_HTTP_HEADER_NAME = "Accept";
    
    protected static final String COOKIE_HTTP_HEADER_NAME = "Cookie";
    
    protected static final String AUTHORIZATION_HTTP_HEADER_NAME = "Authorization";
    
    protected static final String CONTENT_TYPE_HTTP_HEADER_NAME = "Content-Type";
    
    protected static final String BASIC_HTTP_AUTHORIZATION_PREFIX = "Basic ";
    
    protected static final String ETAG_ANY_ETAG = "*";
    
    protected static final String NTLM_HTTP_AUTHENTICATION_SCHEME_NAME = "ntlm";
    
    protected static final String LOCALHOST_DOMAIN_VALUE = "localhost";
    
    /**
     * Application context.
     */
    protected Context mContext;

    /**
     * Operation network response.
     */
    protected RESPONSE mResponse = null;

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     */
    public NetworkOperation(OnOperaionExecutionListener listener) {
        super(listener);
    }

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     * @param context Application context.
     */
    public NetworkOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener);
        this.mContext = context;
    }

    /**
     * @return server Url to send request for.
     */
    protected abstract URI getServerUrl();

    /**
     * Creates and retrieves instance of {@linkplain HttpUriRequest} object initiated with headers and message body (if any) using
     * {@#getRequestHeaders()} and {@#getPostData()} correspondingly.
     *
     * @return Request string representation.
     */
    protected abstract REQUEST getRequest();

    /**
     * Retrieves response.
     *
     * @return Server response or null.
     */
    public RESPONSE getResponse() {
        return mResponse;
    }

    /**
    * Returns the authentication type. Should be overridden. Default implementation returns {@link AuthType#Undefined}.
    *
    * @return Authentication type.
    */
    public AuthType getAuthenticationType() {
        return AuthType.Undefined;
    }

    /**
     * Provides name-value http parameters list that will be included in the request. Should be overridden. Not intended to be called
     * directly. Called from {@linkplain #getHttpRequest()}. Default implementation returns new empty list instance.
     *
     * @param Request message object instance. Returns empty List by default.
     */
    protected List<Pair<String, String>> getRequestHeaders() {
        List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        return headers;
    }

    /**
     * Handles server response.
     *
     * @param response Server response.
     *
     * @return <code>True</code> if response was successfully parsed with no error, otherwise <code>false</code>.
     */
    protected abstract boolean handleServerResponse(RESPONSE response);
}
