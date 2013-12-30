package com.example.office.events;

import com.example.office.events.bus.EventBus;

/**
 * Base class for all diagnostics events.
 */
public abstract class AbstractEvent {

	/**
	 * Empty constructor.
	 */
	protected AbstractEvent() {  }

	/**
	 * Submits the event.
	 */
	public final void submit() {
		EventBus.getInstance().post(this);
	}
}
