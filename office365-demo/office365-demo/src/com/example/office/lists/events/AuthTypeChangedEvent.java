package com.example.office.lists.events;

import com.example.office.events.AbstractEvent;
import com.example.office.lists.auth.AuthType;

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
