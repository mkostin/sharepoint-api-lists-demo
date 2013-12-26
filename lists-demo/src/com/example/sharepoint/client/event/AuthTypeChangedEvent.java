package com.example.sharepoint.client.event;

import com.example.sharepoint.client.network.auth.AuthType;

/**
 * Credentials changed event.
 */
public class AuthTypeChangedEvent extends AbstractEvent {

	private final AuthType type;

	/**
	 * Creates an instance.
	 *
	 * @param type New type.
	 */
	public AuthTypeChangedEvent(AuthType type) {
		this.type = type;
	}

	/**
	 * Gets new auth type.
	 *
	 * @return	{@link AuthType} instance.
	 */
	public AuthType getAuthType() {
		return type;
	}
}
