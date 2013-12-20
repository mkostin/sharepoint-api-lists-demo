package com.microsoft.opentech.office.odata;

import android.text.TextUtils;

import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;

/**
 * Builds ODataEntity from its fields values.
 */
public class EntityBuilder {

    /**
     * Metadata for currently building item.
     */
    private ComplexValue mMetadata;

    /**
     * Currently building item.
     */
    private Entity mCurrent;

    /**
     * Creates a new instance of {@link EntityBuilder} class.
     * 
     * @param type Type of new entity.
     */
    private EntityBuilder(String type) {
        mCurrent = new Entity(type);
        mMetadata = new ComplexValue();
        mMetadata.set("type", type);
        mCurrent.set("__metadata", mMetadata);
    }

    /**
     * Returns a new instance of {@link EntityBuilder} class.
     * 
     * @param type Type of new entity.
     * @return Object that can be used for building an entity.
     */
    public static final EntityBuilder newEntity(String type) {
        if (type == null) {
            return new EntityBuilder("");
        }
        return new EntityBuilder(type);
    }
    
    /**
     * Changes a value of existing property in currently building entity.
     * 
     * @param name Property name.
     * @param value New property value.
     * @return The builder object.
     * @throws IllegalArgumentException thrown when 
     */
    static ODataEntity set(ODataEntity entity, String name, ODataValue value) throws IllegalArgumentException {
        if (entity.getProperty(name) == null) {
            checkName(name);
            entity.addProperty(createProperty(name, value));
        } else {
            entity.getProperty(name).setValue(value); 
        }
        
        return entity;
    }
    
    /**
     * Sets a value of existing property in currently building entity.
     * 
     * @param name Property name.
     * @param value New property value.
     * @return The builder object.
     * @throws IllegalArgumentException thrown when 
     */
    public EntityBuilder set(String name, Object value) throws IllegalArgumentException {
        checkName(name);
        mCurrent.set(name, value);        
        return this;
    }
    
    /**
     * Sets a value of existing property in metadata of currently building entity.
     * 
     * @param name Property name.
     * @param value New property value.
     * @return The builder object.
     * @throws IllegalArgumentException thrown when 
     */
    static ODataEntity setMeta(ODataEntity entity, String name, ODataValue value) throws IllegalArgumentException {
        ODataComplexValue metadata = entity.getProperty("__metadata").getComplexValue();
        if (metadata.get(name) == null) {
            checkName(name);
            metadata.add(createProperty(name, value));
        } else {
            metadata.get(name).setValue(value);
        }
        
        return entity;
    }
    
    /**
     * Changes a value of existing property in metadata of currently building entity.
     * 
     * @param name Property name.
     * @param value New property value.
     * @return The builder object.
     * @throws IllegalArgumentException thrown when 
     */
    public EntityBuilder setMeta(String name, Object value) throws IllegalArgumentException {
        checkName(name);
        mMetadata.set(name, value);
        // update value in entity
        mCurrent.set("__metadata", mMetadata);
        return this;
    }
    
    /**
     * Builds an entity.
     * 
     * @return An entity was built.
     */
    public Entity build() {
        return mCurrent;
    }

    /**
     * Creates {@link ODataProperty} based on given name and value.
     * 
     * @param name Property name.
     * @param value Property value.
     * @return Created property.
     */
    private static ODataProperty createProperty(String name, ODataValue value) {
        if (value == null) {
            return ODataFactory.newPrimitiveProperty(name, null);
        } else if (value.isPrimitive()) {
            return ODataFactory.newPrimitiveProperty(name, value.asPrimitive());
        } else if (value.isCollection()) {
            return ODataFactory.newCollectionProperty(name, value.asCollection());
        } else {
            return ODataFactory.newComplexProperty(name, value.asComplex());
        }
    }

    /**
     * Checks if given field name is correct and throws an exception if it is not.
     * 
     * @param name Field name for checking.
     * @throws IllegalArgumentException Thrown when name is incorrect.
     */
    private static void checkName(String name) throws IllegalArgumentException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name must be a non-empty character string");
        }
    }
}
