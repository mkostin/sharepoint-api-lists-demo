package com.microsoft.opentech.office.utils;

/**
 * Implements utility functionality to work with user name and password.
 */
public final class UserIdentity {

    /**
     * Prevents class creation.
     */
    private UserIdentity() { }

    /**
     * Extracts user name from 'DomainName\UserName' or 'UserName@DomainName' string.
     * 
     * @param usernameDomainStr String with domain and user name.
     * @return User name.
     */
    public static String parseUserName(String usernameDomainStr) {
        if (usernameDomainStr == null) {
            throw new IllegalArgumentException("Username is empty");
        }
        int separatorIndex = usernameDomainStr.indexOf('\\');
        if (separatorIndex >= 0) {
            return usernameDomainStr.substring(separatorIndex + 1);
        } else {
            separatorIndex = usernameDomainStr.indexOf('@');
            if (separatorIndex >= 0) {
                return usernameDomainStr.substring(0, separatorIndex);
            }
        }
        return usernameDomainStr;
    }

    /**
     * Extracts DomainName from 'DomainName\UserName' or 'UserName@DomainName' string.
     * 
     * @param usernameDomainStr String with domain and user name.
     * @return Domain name or <code>null</code> if domain name is missing.
     */
    public static String parseDomainName(String usernameDomainStr) {
        if (usernameDomainStr == null) {
            throw new IllegalArgumentException("Domain name is empty");
        }
        int separatorIndex = usernameDomainStr.indexOf('\\');
        if (separatorIndex >= 0) {
            return usernameDomainStr.substring(0, separatorIndex);
        } else {
            separatorIndex = usernameDomainStr.indexOf('@');
            if (separatorIndex >= 0) {
                return usernameDomainStr.substring(separatorIndex + 1);
            }
        }
        return null;
    }
}
