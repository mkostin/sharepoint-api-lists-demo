package com.microsoft.opentech.office.core.odata;


/**
 * Provides an interface to build an Entity.
 *
 * @param <T> Type of entity to be built.
 */
public interface IEntityBuilder<T> extends IBuilder<T> {
    
    /**
     * Assigns given object to specified name.
     * 
     * @param name Field name.
     * @param value Field value.
     * @return Current builder instance.
     * @throws IllegalArgumentException Thrown when given name is incorrect.
     */
    public IEntityBuilder<T> set(String name, Object value);
    
    /**
     * Assigns given object to specified name in metadata.
     * 
     * @param name Field name.
     * @param value Field value.
     * @return Current builder instance.
     * @throws IllegalArgumentException Thrown when given name is incorrect.
     */
    public IEntityBuilder<T> setMeta(String name, Object value);
    
}
