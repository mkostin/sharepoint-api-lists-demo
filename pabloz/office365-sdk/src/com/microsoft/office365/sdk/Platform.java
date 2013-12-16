package com.microsoft.office365.sdk;

import java.util.Locale;

import android.os.Build;

import com.microsoft.office365.sdk.http.AndroidHttpConnection;
import com.microsoft.office365.sdk.http.HttpConnection;
import com.microsoft.office365.sdk.http.JavaHttpConnection;


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
		if (isAndroid() && Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
			return new AndroidHttpConnection();
		} else {
			return new JavaHttpConnection();
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
