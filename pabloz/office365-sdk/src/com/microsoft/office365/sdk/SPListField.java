package com.microsoft.office365.sdk;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SPListField {

	private String mEntityPropertyName;
	private String mTitle;
	private JSONObject mJsonData;
	
	public static List<SPListField> listFromJson(JSONObject json) throws JSONException {
		List<SPListField> list = new ArrayList<SPListField>();
		
		JSONArray results = json.getJSONObject("d").getJSONArray("results");
		
		for (int i = 0; i < results.length(); i++) {
			JSONObject result = results.getJSONObject(i);
			SPListField item = new SPListField(result, true);
			list.add(item);
		}
		
		return list;
	}
	
	
	public SPListField(JSONObject json) throws JSONException {
		this(json, false);
	}
	
	public SPListField(JSONObject json, boolean isPlainItem) throws JSONException {
		if (isPlainItem) {
			initialize(json);
		} else {
			initialize(json.getJSONObject("d"));
		}
	}
	
	private void initialize(JSONObject json) throws JSONException {
		mJsonData = json;
		
		setEntityPropertyName(mJsonData.getString("EntityPropertyName"));
		setTitle(mJsonData.getString("Title"));
	}

	public Object getData(String field) throws JSONException {
		return mJsonData.get(field);
	}



	public String getTitle() {
		return mTitle;
	}


	public void setTitle(String title) {
		mTitle = title;
	}


	public String getEntityPropertyName() {
		return mEntityPropertyName;
	}


	public void setEntityPropertyName(String entityPropertyName) {
		mEntityPropertyName = entityPropertyName;
	}

}
