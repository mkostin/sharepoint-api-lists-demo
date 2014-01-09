package com.microsoft.opentech.office.mail.data.odata;


/**
 * Odata Importance enumeration
 */
public enum Importance {
    LOW,
    NORMAL, 
    HIGH;
    
    public static Importance fromString(String value) {
        for (Importance importance: Importance.values()) {
            if (importance.name().equalsIgnoreCase(value)) {
                return importance;
            }
        }
        
        return null;
    }
}
