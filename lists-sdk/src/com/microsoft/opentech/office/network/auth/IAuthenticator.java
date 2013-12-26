package com.microsoft.opentech.office.network.auth;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.microsoft.opentech.office.network.NetworkException;

/**
 * Interface for credentials to be sent in a request.
 */
public interface IAuthenticator {

	/**
	 * Adds the credentials to the client.
	 *
	 * @param client HTTP client to prepare.
	 *
	 * @throws NetworkException if any of authentication steps fail.
	 */
	public void prepareClient(final HttpClient client) throws NetworkException;

    /**
     * Adds the credentials to the request.
     *
     * @param request HTTP request to prepare.
     */
    public void prepareRequest(final HttpUriRequest request);
}
