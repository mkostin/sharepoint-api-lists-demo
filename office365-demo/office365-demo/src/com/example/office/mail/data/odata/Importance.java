package com.example.office.mail.data.odata;

import com.google.gson.annotations.SerializedName;

/**
 * Odata Importance enumeration
 */
public enum Importance {
    @SerializedName("Low")
    LOW,
    
    @SerializedName("Normal")
    NORMAL, 
    
    @SerializedName("High")
    HIGH
}
