package com.microsoft.opentech.office.network.auth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Interface for credentials to be sent in a request.
 */
public interface IAuthenticator {

	/**
	 * Adds the credentials to the client.
	 *
	 * @param client HTTP client to prepare.
	 */
	public void prepareClient(final HttpClient client);

    /**
     * Adds the credentials to the request.
     *
     * @param request HTTP request to prepare.
     */
    public void prepareRequest(final HttpUriRequest request);
}
