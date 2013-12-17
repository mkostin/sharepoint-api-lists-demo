package com.microsoft.opentech.office.network.auth;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.microsoft.opentech.office.network.BaseOperation;

/**
 * Implements standard HTTP operation using Apache HTTP components. Provides fields for all operations.
 */
class AccessCodeOperation extends BaseOperation<String> {

    private static final String AUTHORIZATION_CODE_REQUEST_URL_FORMAT = "%s_layouts/15/OAuthAuthorize.aspx?mobile=0&client_id=%s&scope=List.Write Web.Write&response_type=code&redirect_uri=%s";

    private ISharePointCredentials mCreds;

    private Activity mActivity;

    public AccessCodeOperation(OnOperaionExecutionListener listener, Activity activity, ISharePointCredentials creds) {
        super(listener);

        mCreds = creds;

        mActivity = activity;
        if (mActivity == null) {
            throw new IllegalArgumentException("Activity can not be null.");
        }
    }

    @Override
    public void execute() throws RuntimeException, IOException {

        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        // Create the Web View to show the login page
        final WebView wv = new WebView(mActivity);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                AccessCodeOperation.this.getListener().onExecutionComplete(AccessCodeOperation.this, false);
            }
        });

        wv.getSettings().setUserAgentString(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36");
        wv.getSettings().setJavaScriptEnabled(true);

        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int webViewHeight = displaymetrics.heightPixels;

        wv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, webViewHeight));

        wv.requestFocus(View.FOCUS_DOWN);
        wv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                    if (!view.hasFocus()) {
                        view.requestFocus();
                    }
                }

                return false;
            }
        });

        // Create a LinearLayout and add the WebView to the Layout
        LinearLayout layout = new LinearLayout(mActivity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(wv);

        // Add a dummy EditText to the layout as a workaround for a bug
        // that prevents showing the keyboard for the WebView on some devices
        EditText dummyEditText = new EditText(mActivity);
        dummyEditText.setVisibility(View.GONE);
        layout.addView(dummyEditText);

        // Add the layout to the dialog
        builder.setView(layout);

        final AlertDialog dialog = builder.create();

        wv.setWebViewClient(new WebViewClient() {

            boolean mResultReturned = false;
            Object mSync = new Object();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                synchronized (mSync) {
                    // If the URL of the started page matches with the final URL
                    // format, the login process finished
                    if (isFinalUrl(url) && !mResultReturned) {
                        mResultReturned = true;
                        String code = url.replace(mCreds.getRedirectUrl() + "?code=", "");
                        dialog.dismiss();

                        // Setting result
                        AccessCodeOperation.this.mResult = code;
                        mListener.onExecutionComplete(AccessCodeOperation.this, true);
                    }

                    super.onPageStarted(view, url, favicon);
                }
            }

            // Checks if the given URL matches with the final URL's format
            private boolean isFinalUrl(String url) {
                if (url == null) {
                    return false;
                }

                return url.startsWith(mCreds.getRedirectUrl());
            }

        });

        final String url = String.format(AUTHORIZATION_CODE_REQUEST_URL_FORMAT, mCreds.getUrl(), mCreds.getClientId(), mCreds.getRedirectUrl());

        wv.loadUrl(url);
        dialog.show();
    }

}
