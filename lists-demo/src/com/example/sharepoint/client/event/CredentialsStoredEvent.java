package com.example.sharepoint.client.event;

import com.microsoft.opentech.office.network.auth.ISharePointCredentials;


/**
 * Credentials changed event.
 */
public class CredentialsStoredEvent extends AbstractEvent {

	private final ISharePointCredentials credentials;

	/**
	 * Creates an instance.
	 *
	 * @param credentials  Loaded credentials
	 */
	public CredentialsStoredEvent(ISharePointCredentials credentials) {
		this.credentials = credentials;
	}

	/**
	 * Gets new credentials.
	 *
	 * @return	{@link ISharePointCredentials} implementation instance.
	 */
	public ISharePointCredentials getCredentials() {
		return credentials;
	}
}
