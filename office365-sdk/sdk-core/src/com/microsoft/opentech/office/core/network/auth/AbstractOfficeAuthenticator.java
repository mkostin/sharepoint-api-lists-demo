package com.microsoft.opentech.office.core.network.auth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.app.Activity;
import android.text.TextUtils;

import com.microsoft.opentech.office.core.network.NetworkException;
import com.microsoft.opentech.office.core.odata.async.ICallback;

/**
 * Abstract implementation for credentials required to authorize to Office 365 online.
 */
public abstract class AbstractOfficeAuthenticator implements IAuthenticator, ICallback<String> {

    protected abstract ISharePointCredentials getCredentials();

    protected abstract Activity getActivity();

    public AbstractOfficeAuthenticator() {}

    ISharePointCredentials mCredentials;

    Runnable mUiRunnable;

    @Override
    public void prepareRequest(HttpUriRequest request) {
        // Will be called after prepareClient that will retrieve token if non available.
        request.addHeader("Authorization", "Bearer " + mCredentials.getToken());
    }

    @Override
    public void prepareClient(final HttpClient client) throws NetworkException {
        // We do have credentials, simply pass this step. Token will be set later in prepareRequest().
        if (mCredentials != null && !TextUtils.isEmpty(mCredentials.getAccessCode()) && !TextUtils.isEmpty(mCredentials.getToken())) {
            return;
        }

        mCredentials = getCredentials();

        // Should call this on UI thread b/c WebVew must be instantiated and run on UI thread only.
        try {
            mUiRunnable = new Runnable() {
                public void run() {
                    final AccessCodeOperation operation = new AccessCodeOperation(AbstractOfficeAuthenticator.this, getActivity(), mCredentials);
                    try {
                        operation.execute();
                    } catch (Exception e) {
                        onError(e);
                    }
                }
            };
            // As WebView is running on it's own thread we should block an wait until it's finished.
            synchronized (mUiRunnable) {
                getActivity().runOnUiThread(mUiRunnable);
                mUiRunnable.wait();
            }
        } catch (Exception e) {
            // TODO: Log it
            throw new NetworkException("Authentication: Error executing access code retrieval." + e.getMessage(), e);
        }

        if (!TextUtils.isEmpty(mCredentials.getAccessCode())) {
            try {
                // We got the access code, now going for our final goal - the token.
                new AccessTokenOperation(new ICallback<String>() {
                    @Override
                    public void onDone(String result) {
                        mCredentials.setToken(result);
                    }
                    
                    @Override
                    public void onError(Throwable error) {
                        
                    }
                }, getActivity(), mCredentials).execute();
            } catch (Exception e) {
                // TODO: Log it
                throw new NetworkException("Authentication: Error executing access token retrieval." + e.getMessage(), e);
            }
        } else {
            throw new NetworkException("Authentication: Access code retrieval failed.");
        }
    }

    @Override
    public void onDone(String result) {
        try {
            mCredentials.setAccessCode(result);
            releaseUiThread();
        } catch (Exception e) {
            // TODO: log it.
        }
    }
    
    @Override
    public void onError(Throwable error) {
        releaseUiThread();
    }

    private void releaseUiThread() {
        try {
            synchronized (mUiRunnable) {
                mUiRunnable.notify();
            }
        } catch (Exception e) {
            // Ignore.
        }
    }
}
