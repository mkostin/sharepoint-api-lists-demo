/**
 *
 */
package com.microsoft.opentech.office.core.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.http.client.methods.HttpUriRequest;

import android.content.Context;
import android.util.Pair;

import com.microsoft.opentech.office.core.action.BaseOperation;
import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.action.async.OperationAsyncTask;

/**
 * Implements standard HTTP operation. Has common fields for all operations.
 *
 * @param <REQUEST> Request.
 * @param <RESPONSE> Response type retrieved from network.
 * @param <RESULT> Operation execution result.
 */
public abstract class NetworkOperation<REQUEST, RESPONSE, RESULT> extends BaseOperation<RESULT> {

    protected static final String ACCEPT_HTTP_HEADER_NAME = "Accept";

    static final String COOKIE_HTTP_HEADER_NAME = "Cookie";

    protected static final String AUTHORIZATION_HTTP_HEADER_NAME = "Authorization";

    protected static final String CONTENT_TYPE_HTTP_HEADER_NAME = "Content-Type";

    protected static final String BASIC_HTTP_AUTHORIZATION_PREFIX = "Basic ";

    protected static final String ETAG_ANY_ETAG = "*";

    protected static final String NTLM_HTTP_AUTHENTICATION_SCHEME_NAME = "ntlm";

    protected static final String LOCALHOST_DOMAIN_VALUE = "localhost";

    protected CallbackWrapper mCallbackWrapper;

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
    public NetworkOperation(IOperationCallback<RESULT> listener) {
        this(listener, null);
    }

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     * @param context Application context.
     */
    public NetworkOperation(IOperationCallback<RESULT> listener, Context context) {
        super(listener);
        this.mContext = context;
        mCallbackWrapper = new CallbackWrapper(false);
    }

    /**
     * @return server Url to send request for.
     */
    protected abstract URI getServerUrl();

    /**
     * Creates and retrieves instance of {@linkplain HttpUriRequest} object initiated with headers and message body (if any) using
     * {@link #getRequestHeaders()} and {@link #getPostData()} correspondingly.
     *
     * @return Request string representation.
     * @throws UnsupportedOperationException when unable to set headers for request.
     * @throws UnsupportedEncodingException when unable to set entity because its encoding does not match default http encoding.
     */
    protected abstract REQUEST getRequest() throws UnsupportedEncodingException, UnsupportedOperationException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract RESULT execute() throws RuntimeException, IOException;

    /**
     * Executes operation asynchronously.
     *
     * @return Future with given operation.
     */
    @SuppressWarnings("unchecked")
    public Future<RESULT> executeAsync() {
        OperationAsyncTask<RESULT> task = new OperationAsyncTask<RESULT>(getListener());
        task.execute(this);
        mCallbackWrapper.setAsyncMode(true);
        return task.getFuture();
    }

    /**
     * Retrieves response.
     *
     * @return Server response or null.
     */
    public RESPONSE getResponse() {
        return mResponse;
    }

    /**
     * Provides name-value http parameters list that will be included in the request. Should be overridden. Not intended to be called
     * directly. Called from {@linkplain #getHttpRequest()}. Default implementation returns new empty list instance.
     *
     * @param Request message object instance. Returns empty List by default.
     * @throws RuntimeException when unable to add some header to list. This declared for overriding in descendants.
     */
    protected List<Pair<String, String>> getRequestHeaders() throws RuntimeException {
        List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();
        return headers;
    }

    /**
     * Handles server response.
     *
     * @param response Server response.
     *
     * @return <code>True</code> if response was successfully parsed with no error, otherwise <code>false</code>.
     * @throws RuntimeException when error occurred during response handling.
     */
    protected abstract boolean handleServerResponse(RESPONSE response) throws IOException;

    /**
     * Wraps callback to avoid checking if current operation is running sync/async.
     * This class is not static since it makes sense only in operation context.
     */
    protected class CallbackWrapper {

        /**
         * Asynchrony flag.
         */
        private boolean mIsAsync;

        /**
         * Wrapped callback.
         */
        private IOperationCallback<RESULT> mCallback;

        /**
         * Constructor.
         *
         * @param isAsync Indicates if current operation is being executed in sync or async mode.
         */
        public CallbackWrapper(boolean isAsync) {
            this.mCallback = NetworkOperation.this.getListener();
            this.mIsAsync = isAsync;
        }

        /**
         * Invokes callback's onDone method if necessary.
         *
         * @param result Parameter passed to callback.
         */
        public void onDone(RESULT result) {
            if (!mIsAsync && mCallback != null) {
                mCallback.onDone(result);
            }
        }

        /**
         * Invokes callback's onError method if necessary.
         *
         * @param error Parameter passed to callback.
         */
        public void onError(Throwable error) {
            if (!mIsAsync && mCallback != null) {
                mCallback.onError(error);
            }
        }

        /**
         * Sets asynchrony mode.
         *
         * @param isAsync New value.
         */
        public void setAsyncMode(boolean isAsync) {
            mIsAsync = isAsync;
        }
    }
}
