package com.example.sharepoint.client.event.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * The event bus allowing to listen to or to produce messages (events).
 * Is used for communication between the service and its UI.
 * <br>The singleton is unsafe because its initialization is separated from its usage
 * and it's guaranteed that the instance will be initialized before first use.
 */
public final class EventBus {

	private static EventBus instance;
	
	private final Bus bus = new Bus();
	private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());	
	
	private EventBus() {  }
	
	/**
	 * Initializes the bus.
	 */
	public static void init() {
		EventBus.instance = new EventBus();
	}
	
	/**
	 * Gets the instance of the bus.
	 * @return	event bus instance.
	 */
	public static EventBus getInstance() {
		return instance;
	}
	
	/**
	 * Posts a new event to the bus. The event is posted to the main thread.
	 * @param	event	the event to post
	 */
	public void post(Object event) {
		mainThreadHandler.post(new PostEventTask(event));
	}
	
	/**
	 * Registers a new bus listener.
	 * @param listener	the listener to register
	 */
	public void register(Object listener) {
		bus.register(listener);
	}
	
	/**
	 * Unregisters a listener previously registered via {@link #register(Object)}.
	 * @param listener	the listener to unregister
	 */
	public void unregister(Object listener) {
		bus.unregister(listener);
	}
	
	/**
	 * Posts events to the bus.
	 */
	private final class PostEventTask implements Runnable {
		
		private final Object message;
		
		PostEventTask(Object message) {
			this.message = message;
		}

		@Override
		public void run() {
			bus.post(message);
		}
	}	
}
