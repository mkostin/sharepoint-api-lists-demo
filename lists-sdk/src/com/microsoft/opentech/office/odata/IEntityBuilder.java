package com.microsoft.opentech.office.odata;

public interface IEntityBuilder<T> extends IBuilder<T> {
    
    public IEntityBuilder<T> set(String name, Object value);
    
    public IEntityBuilder<T> setMeta(String name, Object value);
    
}
