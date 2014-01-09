package com.microsoft.opentech.office.core;

/**
 * Provides common interface for constructing objects.
 * @param <T> Type of object to build.
 */
public interface IBuilder<T> {
    
    /**
     * Builds and returns current object.
     * @return Built object.
     */
    public T build();
}
