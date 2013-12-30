package com.example.office.mail.data.odata;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * Structure describes a user in current OData implementation. Endpoints send this structure in cases when we expect information about
 * message sender or receiver (i. e. fields "From", "Sender", "ToRecipients", etc.). Current specification says that fields must be just
 * strings.
 */
public class User implements Serializable {
    
    /**
     * User name.
     */
    @SerializedName("Name")
    private String mName;
    
    /**
     * User address
     */
    @SerializedName("Address")
    private String mAddress;
    
    private static final long serialVersionUID = 5L;

    /**
     * Gets current user name.
     * 
     * @return User name.
     */
    public String getName() {
        return mName;
    }
    
    /**
     * Sets current user name.
     * 
     * @param name New name value.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Gets current user address.
     * 
     * @return User address.
     */
    public String getAddress() {
        return mAddress;
    }
    
    /**
     * Sets current user address.
     * 
     * @param address New address value.
     */
    public void setAddress(String address) {
        mAddress = address;
    }
}