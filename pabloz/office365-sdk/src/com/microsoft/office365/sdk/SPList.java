package com.microsoft.office365.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SPList {
	
	private String mId;
	private String mTitle;
	private String mListItemEntityTypeFullName;
	private JSONObject mJsonData;
	
	public static List<SPList> listFromJson(JSONObject json) throws JSONException {
		List<SPList> list = new ArrayList<SPList>();
		
		JSONArray results = json.getJSONObject("d").getJSONArray("results");
		
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			SPList item = new SPList(result, true);
			list.add(item);
		}
		
		return list;
	}
	
	public SPList(JSONObject json) throws JSONException {
		this(json, false);
	}
	
	public SPList(JSONObject json, boolean isPlainItem) throws JSONException {
		if (isPlainItem) {
			initialize(json);
		} else {
			initialize(json.getJSONObject("d"));
		}
	}
	
	private void initialize(JSONObject json) throws JSONException {
		mJsonData = json;
		
		setId(mJsonData.getString("Id"));
		setListItemEntityTypeFullName(mJsonData.getString("ListItemEntityTypeFullName"));
		setTitle(mJsonData.getString("Title"));
	}
	
	public Object getData(String field) throws JSONException {
		return mJsonData.get(field);
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		mId = id;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getListItemEntityTypeFullName() {
		return mListItemEntityTypeFullName;
	}

	public void setListItemEntityTypeFullName(String listItemEntityTypeFullName) {
		mListItemEntityTypeFullName = listItemEntityTypeFullName;
	}
}
