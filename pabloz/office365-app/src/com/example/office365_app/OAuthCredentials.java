package com.example.office365_app;

import com.microsoft.office365.sdk.Credentials;
import com.microsoft.office365.sdk.http.Request;

public class OAuthCredentials implements Credentials {

	String mToken;
	
	public OAuthCredentials(String oAuthToken) {
		mToken = oAuthToken;
	}
	
	@Override
	public void prepareRequest(Request request) {
		request.addHeader("Authorization", "Bearer " + mToken);
	}
}
