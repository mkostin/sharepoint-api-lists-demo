package com.example.sharepoint.client.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
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

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;

/**
 * Implements standard HTTP operation (without MMS routing). Implements basic functionality to make HTTP requests.
 */
public abstract class HttpOperation extends BaseOperation {

    /**
     * Indicates authentication type for this connection. User name and password are retrieved from
     * preferences.
     */
    protected AuthType authType = AuthType.Undefined;

    /**
     * Response code.
     */
    protected int mResponseCode = -1;

    protected Context context;

    /**
     * Creates new instance of the class.
     *
     * @param listener Listener to get notifications when operation will be completed.
     */
    public HttpOperation(OnOperaionExecutionListener listener, AuthType authType, Context context) {
        super(listener);
        this.context = context;
        this.authType = authType;
    }

    @Override
    public void execute() {
        // Checking for Internet connection before operation executing.
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            this.mErrorMessage = context.getString(R.string.data_connection_no_data_connection);
            if (mListener != null) mListener.onExecutionComplete(this, false);
            return;
        }

        completeOperation();
    }

    /**
     * Completes operation execution.
     */
    private void completeOperation() {
        executeOperation();

        if (this.hasError()) {
            if (mListener != null) mListener.onExecutionComplete(this, false);
            return;
        }

        boolean result = handleServerResponse(this.mResponse) && !hasError();
        if (mListener != null) mListener.onExecutionComplete(this, result);
    }

    /**
     * Performs request to server side. Handles response.
     */
    private void executeOperation() {
        InputStream inputStream = null;
        HttpClient httpClient = null;
        HttpEntity ent = null;

        try {
            httpClient = new DefaultHttpClient();
            initializeClient((DefaultHttpClient) httpClient);
            setCredentials(((DefaultHttpClient) httpClient).getCredentialsProvider());

            HttpUriRequest httpMessage = getHttpRequest();

            HttpResponse response = null;
            Logger.logTraceMessage(HttpOperation.class.getSimpleName() + ".executeOperation(): Trying to connect to " + getServerUrl());
            response = httpClient.execute(httpMessage);

            mResponseCode = response.getStatusLine().getStatusCode();
            if (mResponseCode >= 400 && mResponseCode <= 599) {
                mErrorMessage = "Operation failed with status code: " + mResponseCode + ". Reason: " + response.getStatusLine().getReasonPhrase();
                Logger.logMessage("BaseOperation: URI=" + getServerUrl());
                Logger.logMessage("BaseOperation: " + mErrorMessage);
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

            Logger.logTraceMessage(getClass().getSimpleName() + ".executeOperation(): Gets status line:\n" + mStatusLine + "\nGets response:\n"
                    + mResponse);
        } catch (final Exception e) {
            mErrorMessage = e.getMessage();
            Logger.logApplicationException(e, getClass().getSimpleName() + ".executeOpetarion(): Failed.");
        } finally {
            if (ent != null) {
                try {
                    ent.consumeContent();
                } catch (IOException e) {
                    Logger.logApplicationException(e, getClass().getSimpleName() + " consumeContent failed in finally.");
                    e.printStackTrace();
                }
            }

            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown(); // do we really want to shut down completely?
            }
        }
    }

    public HttpClient getSecureHttpClient() {
        try {

            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            HttpParams params = new BasicHttpParams();
            SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

            return new DefaultHttpClient(mgr, params);

        } catch (Exception e) {

            Logger.logApplicationException(e, BaseOperation.class.getSimpleName() + ": Unable to create SecureHttpClient: " + e.getMessage());
            return new DefaultHttpClient();
        }
    }

    public HttpClient getTrustAllHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            Logger.logApplicationException(e, BaseOperation.class.getSimpleName() + ": Unable to create TrustAllHttpClient: " + e.getMessage());
            return new DefaultHttpClient();
        }
    }

    /**
     * Handles server response.
     *
     * @param response Server response.
     *
     * @return <code>True</code> if response was successfully parsed with no error, otherwise <code>false</code>.
     */
    protected boolean handleServerResponse(String response) {
        return false;
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
     * @return Authentication type.
     */
    public AuthType getAuthenticationType() {
        return this.authType;
    }
}
