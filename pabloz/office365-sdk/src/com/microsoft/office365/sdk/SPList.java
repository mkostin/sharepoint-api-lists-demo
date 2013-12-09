package com.microsoft.office365.sdk;

import org.json.JSONException;
import org.json.JSONObject;

public class SPList {
	
	private String mId;
	private String mTitle;
	private String mListItemEntityTypeFullName;
	private JSONObject mJsonData;
	
	public SPList(JSONObject json) throws JSONException {
		initialize(json);
	}
	
	private void initialize(JSONObject json) throws JSONException {
		mJsonData = json.getJSONObject("d");
		
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
