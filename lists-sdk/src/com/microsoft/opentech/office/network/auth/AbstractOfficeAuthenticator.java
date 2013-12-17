package com.microsoft.opentech.office.network.auth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.app.Activity;
import android.text.TextUtils;

import com.microsoft.opentech.office.network.BaseOperation;
import com.microsoft.opentech.office.network.BaseOperation.OnOperaionExecutionListener;
import com.microsoft.opentech.office.network.NetworkException;

/**
 * Abstract implementation for credentials required to authorize to Office 365 online.
 */
public abstract class AbstractOfficeAuthenticator implements IAuthenticator, OnOperaionExecutionListener {

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
    public void prepareClient(final HttpClient client) {
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
                        onExecutionComplete(operation, false);
                    }
                }
            };
            // As WebView is running on it's own thread we should block an wait until it's finished.
            synchronized (mUiRunnable) {
                getActivity().runOnUiThread(mUiRunnable);
                mUiRunnable.wait();
            }

            // We got the access code, now going for our final goal - the token.
            new AccessTokenOperation(new OnOperaionExecutionListener() {
                @Override
                public void onExecutionComplete(BaseOperation operation, boolean executionResult) {
                    mCredentials.setToken(((AccessTokenOperation) operation).getResult());
                }
            }, getActivity(), mCredentials).execute();
        } catch (Exception e) {
            throw new NetworkException("Error while preparing and adding authentication cookies to a request.", e);
        }
    }

    @Override
    // TODO: populate throwing exception
    public void onExecutionComplete(BaseOperation operation, boolean executionSucceeded) {
        try {
            if (executionSucceeded) {
                mCredentials.setAccessCode(((AccessCodeOperation) operation).getResult());
            }
            releaseUiThread();
        } catch (Exception e) {
            // TODO: throw agregated exception here.
        }
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
