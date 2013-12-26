package com.example.sharepoint.client.event;

import com.example.sharepoint.client.event.bus.EventBus;

/**
 * Base class for all diagnostics events.
 */
abstract class AbstractEvent {

	/**
	 * Empty constructor.
	 */
	AbstractEvent() {  }

	/**
	 * Submits the event.
	 */
	public final void submit() {
		EventBus.getInstance().post(this);
	}
}
