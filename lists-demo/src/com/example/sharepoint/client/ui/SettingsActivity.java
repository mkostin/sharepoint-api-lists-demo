package com.example.sharepoint.client.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.sharepoint.client.R;
import com.example.sharepoint.client.event.AuthTypeChangedEvent;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.example.sharepoint.client.network.auth.SharePointCredentials;
import com.example.sharepoint.client.preferences.AuthPreferences;

/**
 * Implements Options dialog. Allows to setup displaying of alert notifications.
 */
public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.settings);

            final SharePointCredentials creds = (SharePointCredentials) AuthPreferences.loadCredentials();
            final AuthType storedType = creds == null ? AuthType.UNDEFINED : creds.getAuthType();

            final RadioGroup authGroup = (RadioGroup) findViewById(R.id.settings_auth_group);
            final RadioButton authCookie = (RadioButton) findViewById(R.id.settings_auth_cookie);
            authCookie.setChecked(storedType.is(AuthType.COOKIE) || storedType.is(AuthType.UNDEFINED));

            final RadioButton authOauth = (RadioButton) findViewById(R.id.settings_auth_oauth);
            authOauth.setChecked(storedType.is(AuthType.OAUTH));

            final RadioButton authNtlm = (RadioButton) findViewById(R.id.settings_auth_ntlm);
            authNtlm.setChecked(storedType.is(AuthType.NTLM));

            TextView buttonSave = (TextView) findViewById(R.id.settings_ok);
            OnClickListener saveListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        AuthType type = null;
                        switch (authGroup.getCheckedRadioButtonId()) {
                            case R.id.settings_auth_cookie:
                                type = AuthType.COOKIE;
                                break;
                            case R.id.settings_auth_oauth:
                                type = AuthType.OAUTH;
                                break;
                            case R.id.settings_auth_ntlm:
                                type = AuthType.NTLM;
                                break;
                        }

                        if(!type.is(storedType)) {
                            if(creds != null) {
                                creds.reset().setAuthType(type);
                                AuthPreferences.storeCredentials(creds);
                            } else {
                                SharePointCredentials newCreds = new SharePointCredentials();
                                newCreds.setAuthType(type);
                                AuthPreferences.storeCredentials(newCreds);

                                // Inform listeners that auth type has changed.
                            }
                        }
                        new AuthTypeChangedEvent(type).submit();

                        finish();
                    } catch (Exception e) {
                        Logger.logApplicationException(e, getClass().getSimpleName() + "Button.onClick(): Save Failed.");
                    }
                }
            };
            buttonSave.setOnClickListener(saveListener);

            TextView buttonCancel = (TextView) findViewById(R.id.settings_cancel);
            OnClickListener cancelListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingsActivity.this.finish();
                }
            };

            buttonCancel.setOnClickListener(cancelListener);
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".onCreate(): Failed.");
        }
    }
}
