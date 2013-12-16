package com.microsoft.office365.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SPFile {
	private String mUniqueId;
	private String mServerRelativeURL;
	private JSONObject mJsonData;
	
	public static List<SPFile> listFromJson(JSONObject json) throws JSONException {
		List<SPFile> list = new ArrayList<SPFile>();
		
		JSONArray results = json.getJSONObject("d").getJSONArray("results");
		
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			SPFile item = new SPFile(result, true);
			list.add(item);
		}
		
		return list;
	}
	
	public SPFile(JSONObject json) throws JSONException {
		this(json, false);
	}
	
	public SPFile(JSONObject json, boolean isPlainItem) throws JSONException {
		if (isPlainItem) {
			initialize(json);
		} else {
			initialize(json.getJSONObject("d"));
		}
	}
	
	private void initialize(JSONObject json) throws JSONException {
		mJsonData = json;
		
		setServerRelativeURL(mJsonData.getString("ServerRelativeUrl"));
		setUniqueId(mJsonData.getString("UniqueId"));
	}

	public Object getData(String field) throws JSONException {
		return mJsonData.get(field);
	}

	public String getUniqueId() {
		return mUniqueId;
	}

	public void setUniqueId(String mUniqueId) {
		this.mUniqueId = mUniqueId;
	}

	public String getServerRelativeURL() {
		return mServerRelativeURL;
	}

	public void setServerRelativeURL(String mServerRelativeURL) {
		this.mServerRelativeURL = mServerRelativeURL;
	}

}
