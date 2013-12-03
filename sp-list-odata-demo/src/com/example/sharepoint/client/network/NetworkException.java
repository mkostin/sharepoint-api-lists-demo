package com.example.sharepoint.client.network;

/**
 * Base class for all network-related exceptions.
 **/
public class NetworkException extends Exception {
	private static final long serialVersionUID = -6778375031458006946L;

	private final int statusCode;

	public NetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		this.statusCode = -1;
	}

	public NetworkException(String detailMessage, Throwable throwable, int statusCode) {
		super(detailMessage, throwable);
		this.statusCode = statusCode;
	}

	public NetworkException(String string) {
		super(string);
		this.statusCode = -1;
	}

	public NetworkException(String string, int statusCode) {
		super(string);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}