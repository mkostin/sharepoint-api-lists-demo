package com.microsoft.office365.sdk;

import java.util.Locale;

import com.microsoft.office365.sdk.http.AndroidHttpConnection;
import com.microsoft.office365.sdk.http.HttpConnection;


/**
 * Platform specific classes and operations
 */
public class Platform {
	static boolean mPlatformVerified = false;
	static boolean mIsAndroid = false;
	
	/**
	 * Creates an adequate HttpConnection for the current platform
	 * @param logger Logger to use with the connection
	 * @return An HttpConnection
	 */
	public static HttpConnection createHttpConnection() {
		if (isAndroid()) {
			return new AndroidHttpConnection();
		} else {
			return null;
		}
	}

	/**
	 * Indicates if the current platform is Android
	 */
	public static boolean isAndroid() {
		
		if (!mPlatformVerified) {
			String runtime = System.getProperty("java.runtime.name").toLowerCase(Locale.getDefault());
			
			if (runtime.contains("android")) {
				mIsAndroid = true;
			} else {
				mIsAndroid = false;
			}
			
			mPlatformVerified = true;
		}

		return mIsAndroid;
	}
}
