package com.microsoft.office365.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SPListItem {

	private int mId;
	private String mGUID;
	
	private String mTitle;
	private JSONObject mJsonData;
	
	public static List<SPListItem> listFromJson(JSONObject json) throws JSONException {
		List<SPListItem> list = new ArrayList<SPListItem>();
		
		JSONArray results = json.getJSONObject("d").getJSONArray("results");
		
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(0);
			SPListItem item = new SPListItem(result, true);
			list.add(item);
		}
		
		return list;
	}
	
	
	public SPListItem(JSONObject json) throws JSONException {
		this(json, false);
	}
	
	public SPListItem(JSONObject json, boolean isPlainItem) throws JSONException {
		if (isPlainItem) {
			initialize(json);
		} else {
			initialize(json.getJSONObject("d"));
		}
	}
	
	private void initialize(JSONObject json) throws JSONException {
		mJsonData = json;
		
		setId(mJsonData.getInt("Id"));
		setGUID(mJsonData.getString("GUID"));
		setTitle(mJsonData.getString("Title"));
	}

	public Object getData(String field) throws JSONException {
		return mJsonData.get(field);
	}


	public int getId() {
		return mId;
	}


	public void setId(int id) {
		mId = id;
	}


	public String getTitle() {
		return mTitle;
	}


	public void setTitle(String title) {
		mTitle = title;
	}


	public String getGUID() {
		return mGUID;
	}


	public void setGUID(String gUID) {
		mGUID = gUID;
	}

}
