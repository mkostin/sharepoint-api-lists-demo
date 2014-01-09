package com.microsoft.opentech.office.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;

import com.microsoft.opentech.office.core.action.async.IOperationCallback;

/**
 * Implements standard HTTP operation using Apache HTTP components. Provides fields for all operations.
 */
public abstract class HttpOperation extends NetworkOperation<HttpRequest, String, String> {

    private static final String NO_DATA_CONNECTION_MESSAGE = "No data connection";

    /**
     * Operation HTTP status line.
     */
    protected String mStatusLine = null;

    /**
     * Response code.
     */
    protected int mResponseCode = -1;

    /**
     * Creates new instance of the class.
     * 
     * @param listener Listener to get notifications when operation will be completed.
     */
    public HttpOperation(IOperationCallback<String> listener) {
        super(listener);
    }

    /**
     * Creates new instance of the class.
     * 
     * @param listener Listener to get notifications when operation will be completed.
     * @param context Application context.
     */
    public HttpOperation(IOperationCallback<String> listener, Context context) {
        super(listener, context);
    }

    /**
     * Called during http client setup to set authentication credentials. Should be overridden if necessary. Default implementation does
     * nothing and returns <code>true</code>.
     * 
     * @param provider Credentials provider
     * 
     * @return <code>true</code> if credentials were set correctly, or <code>false</code> in case of error.
     */
    protected boolean setCredentials(CredentialsProvider provider) {
        return true;
    };

    /**
     * Called during http client setup. Can be overridden if necessary. Default implementation does nothing and returns <code>true</code>.
     * 
     * @param httpClient HTTP client instance.
     * 
     * @return <code>true</code> if initialization was successfull, <code>false</code> otherwise.
     */
    protected boolean prepareClient(HttpClient httpClient) {
        return true;
    }

    /**
     * Called during http message setup. Can be overridden if necessary. Default implementation does nothing and returns <code>true</code>.
     * 
     * @param httpMessage HTTP message instance.
     * 
     * @return <code>true</code> if initialization was successfull, <code>false</code> otherwise.
     */
    protected boolean prepareMessage(HttpUriRequest httpMessage) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected HttpUriRequest getRequest() throws UnsupportedEncodingException, UnsupportedOperationException {
        HttpUriRequest httpMessage = null;
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

        List<Pair<String, String>> headers = getRequestHeaders();
        if (headers != null && !headers.isEmpty()) {
            for (Pair<String, String> header : headers) {
                httpMessage.setHeader(header.first, header.second);
            }
        }

        return httpMessage;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws UnsupportedOperationException when unable to instantiate request.
     * @throws ClientProtocolException when an HTTP protocol error occurred.
     * @throws IOException when I/O error occurred or connection was aborted.
     */
    @Override
    public String execute() throws UnsupportedOperationException, ClientProtocolException, IOException {
        // Checking for Internet connection before operation executing.
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            this.mErrorMessage = NO_DATA_CONNECTION_MESSAGE;
            mCallbackWrapper.onError(new NetworkException(this.mErrorMessage));
            return null;
        }

        completeOperation();
        return mResult;
    }

    /**
     * Completes operation execution.
     * 
     * @throws UnsupportedOperationException when unable to instantiate request.
     * @throws IOException when I/O error occurred or connection was aborted.
     * @throws ClientProtocolException when HTTP protocol error occurred.
     * @throws Exception when response hadling fails.
     */
    private void completeOperation() throws UnsupportedOperationException, ClientProtocolException, IOException {
        executeOperation();

        if (this.hasError()) {
            mCallbackWrapper.onError(new RuntimeException(this.getErrorMessage()));
            return;
        }

        boolean success = handleServerResponse(this.mResponse) && !hasError();
        if (success) {
            mCallbackWrapper.onDone(this.mResult);
        } else {
            mCallbackWrapper.onError(new RuntimeException("An error occurred during execution operation " + this.mOperationId));
        }
    }

    /**
     * Performs request to server side. Handles response.
     * 
     * @throws UnsupportedOperationException when unable to instantiate request.
     * @throws IOException when I/O error occurred or connection was aborted.
     * @throws ClientProtocolException when HTTP protocol error occurred.
     */
    private void executeOperation() throws UnsupportedOperationException, ClientProtocolException, IOException {
        InputStream inputStream = null;
        HttpClient httpClient = null;
        HttpEntity ent = null;

        try {
            // httpClient = new DefaultHttpClient();
            httpClient = getTrustAllHttpClient();
            prepareClient((DefaultHttpClient) httpClient);
            setCredentials(((DefaultHttpClient) httpClient).getCredentialsProvider());

            HttpUriRequest httpMessage = getRequest();
            prepareMessage(httpMessage);

            HttpResponse response = null;
            response = httpClient.execute(httpMessage);

            mResponseCode = response.getStatusLine().getStatusCode();
            if (mResponseCode >= 400 && mResponseCode <= 599) {
                mErrorMessage = "Operation failed with status code: " + mResponseCode + ". Reason: " + response.getStatusLine().getReasonPhrase();
            }

            int received;
            ent = response.getEntity();
            inputStream = ent.getContent();
            this.mStatusLine = response.getStatusLine().toString();
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            while ((received = inputStream.read()) != -1) {
                responseBuffer.write(received);
            }

            this.mResponse = responseBuffer.toString();
        } catch (Throwable e) {
            throw new NetworkException("Error: " + e.getMessage(), e);
        } finally {
            if (ent != null) {
                ent.consumeContent();
            }

            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown(); // do we really want to shut down completely?
            }
        }
    }

    public HttpClient getSecureHttpClient() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();
        SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

        return new DefaultHttpClient(mgr, params);
    }

    /**
     * Gets an instance of secure HttpClient which accepts connections from all SSL hosts.
     * 
     * @return HttpClient instance.
     * @throws GeneralSecurityException when unable to create client.
     * @throws IOException thrown never.
     */
    public HttpClient getTrustAllHttpClient() throws GeneralSecurityException, IOException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        SSLSocketFactory sf = new TrustAllSSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

        return new DefaultHttpClient(ccm, params);
    }

    /**
     * Returns response code.
     * 
     * @return Response code returned by the HTTP request.
     */
    public int getResponseCode() {
        return mResponseCode;
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

}
