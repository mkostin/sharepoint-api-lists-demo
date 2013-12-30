package com.example.office.utils;

import org.json.JSONObject;

/**
 * JSON utility helper class.
 */
public class JSONUtils {
    
    /**
     * Retrieves a String value from the specified parameter within provided {@linkplain JSONObject}.
     * 
     * @param json JSONObject to inspect.
     * @param name Name of the parameter.
     * @param defval Default value to return if requested parameter doesn't exist.
     * 
     * @return String value of the parameter if it exists, <code>defval</code> value otherwise.
     */
    public static String getString(JSONObject json, String name, String defval) {
        if (json == null || name == null) return defval;
        try {
            return json.getString(name);
        } catch (Exception e) {
        }
        return defval;
    }

    /**
     * Retrieves a String value from the specified parameter within provided {@linkplain JSONObject}.
     * 
     * @param json JSONObject to inspect.
     * @param name Name of the parameter.
     * 
     * @return String value of the parameter if it exists, <code>null</code> otherwise.
     */
    public static String getString(JSONObject json, String name) {
        return getString(json, name, null);
    }
}
