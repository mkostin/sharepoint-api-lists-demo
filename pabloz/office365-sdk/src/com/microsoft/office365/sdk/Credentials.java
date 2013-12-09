package com.microsoft.office365.sdk;

import com.microsoft.office365.sdk.http.Request;

/**
 * Interface for credentials to be sent in a request
 */
public interface Credentials {
	
	/**
	 * Adds the credentials to the request
	 * @param request The request to prepare
	 */
	public void prepareRequest(Request request);
}
