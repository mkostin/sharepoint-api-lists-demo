package com.microsoft.office365.sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class SPFieldUrlValue {
	public static JSONObject getJsonForUrl(String url, String description) throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("Description", description);
		json.put("Url", url);
		
		return json;
	}
}
