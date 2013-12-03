/**
 *
 */
package com.example.sharepoint.client.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import com.example.sharepoint.client.logger.Logger;

/**
 * Implements standard HTTP operation. Has common fields for all operations.
 */
public abstract class BaseOperation {

    /**
     * Implements listener to notify operation creator when operation is completed.
     */
    public interface OnOperaionExecutionListener {

        /**
         * Notifies operation creator that the operation is completed.
         *
         * @param operation The operation.
         * @param executionResult True if operation was executed successfully, otherwise false.
         */
        public void onExecutionComplete(BaseOperation operation, boolean executionResult);
    }

    /**
     * Unique ID for operation.
     */
    protected String mOperationId = UUID.randomUUID().toString();

    /**
     * Error message if operation failed.
     */
    protected String mErrorMessage = null;

    /**
     * Operation response.
     */
    protected String mResponse = null;

    /**
     * Operation HTTP status line.
     */
    protected String mStatusLine = null;

    /**
     * Operation execution listener.
     */
    protected OnOperaionExecutionListener mQueueListener;

    /**
     * Listener to get notifications when operation will be completed.
     */
    protected OnOperaionExecutionListener mListener;

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     */
    public BaseOperation(OnOperaionExecutionListener listener) {
        mListener = listener;
    }

    /**
     * Executes the operation.
     */
    abstract void execute();

    /**
     * @return server Url to send request for.
     */
    protected abstract String getServerUrl();

    /**
     * Called during http client setup to set authentication credentials. Should be overridden if necessary.
     *
     * @param provider Credentials provider
     *
     * @return <code>true</code> if credentials were set correctly, or <code>false</code> in case of error.
     */
    protected abstract boolean setCredentials(CredentialsProvider provider);

    /**
     * Creates and retrieves instance of {@linkplain HttpUriRequest} object initiated with headers and message body (if any) using
     * {@#getRequestHeaders()} and {@#getPostData()} correspondingly.
     *
     * @return Request string representation.
     */
    @SuppressWarnings("unchecked")
    protected HttpUriRequest getHttpRequest() {
        HttpUriRequest httpMessage = null;
        try {
            Object postData = getPostData();
            if (postData == null) {
                httpMessage = new HttpGet(getServerUrl());
            } else {
                httpMessage = new HttpPost(getServerUrl());
                if (postData instanceof String) {
                    ((HttpPost) httpMessage).setEntity(new StringEntity((String) postData, "utf-8"));
                } else if (postData instanceof List<?>) {
                    ((HttpPost) httpMessage).setEntity(new UrlEncodedFormEntity((List<NameValuePair>) postData));
                } else if (postData instanceof byte[]) {
                    ((HttpPost) httpMessage).setEntity(new ByteArrayEntity((byte[]) postData));
                }
            }

            List<Header> headers = getRequestHeaders();
            if (headers != null && !headers.isEmpty()) {
                Header[] headersArray = new Header[headers.size()];
                headers.toArray(headersArray);
                httpMessage.setHeaders(headersArray);
            }

        } catch (final Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getHttpRequest(): Failed.");
        }
        return httpMessage;
    }

    /**
     * Returns data that should be included in the POST request if one is performed. Should be overridden. Not intended to be called
     * directly. Must return either {@linkplain String} type, or {@linkplain List<NameValuePair>} type object instance or <code>null</code>
     * if request is of GET type. Default implementation returns <code>null</code>.
     *
     * @return Data that should included in the POST request body. Returns <code>null</code> by default.
     */
    protected Object getPostData() {
        return null;
    }

    /**
     * Provides name-value http parameters list that will be included in the request. e.g. "...?name1=value1&...&namen=valueN". Should be
     * overridden. Not intended to be called directly. Called from {@linkplain #getHttpRequest()}.
     *
     * @return Name-value http parameters list. Returns <code>null</code> by default.
     */
    protected List<NameValuePair> getRequestParams() {
        return null;
    }

    /**
     * Provides name-value http parameters list that will be included in the request. Should be overridden. Not intended to be called
     * directly. Called from {@linkplain #getHttpRequest()}.
     *
     * @param Request message object instance. Returns empty List by default.
     */
    protected List<Header> getRequestHeaders() {
        List<Header> headers = new ArrayList<Header>();
        return headers;
    }

    /**
     * Retrieves listener.
     *
     * @return The listener.
     */
    OnOperaionExecutionListener getListener() {
        return mListener;
    }

    /**
     * Retrieves the error message.
     *
     * @return Error Message or null.
     */
    public String getErrorMessage() {
        return mErrorMessage;
    }

    /**
     * @return whether operation failed or not
     */
    public boolean hasError() {
        return mErrorMessage != null;
    }

    /**
     * Retrieves response.
     *
     * @return Server response or null.
     */
    public String getResponse() {
        return mResponse;
    }
}
