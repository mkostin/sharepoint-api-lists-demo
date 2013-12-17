package com.example.sharepoint.client.odata;

import android.text.TextUtils;

import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;

/**
 * Builds ODataEntity from its fields values.
 */
public class ODataEntityBuilder {

    /**
     * Metadata for currently building item.
     */
    private ODataComplexValue mMetadata;

    /**
     * Currently building item.
     */
    private ODataEntity mCurrent;

    /**
     * Creates a new instance of {@link ODataEntityBuilder} class.
     * 
     * @param type Type of new entity.
     */
    private ODataEntityBuilder(String type) {
        mCurrent = ODataFactory.newEntity(type);
        mMetadata = new ODataComplexValue("");
        mMetadata.add(ODataFactory.newPrimitiveProperty("type", new ODataPrimitiveValue.Builder().setText(type).build()));
        mCurrent.addProperty(ODataFactory.newComplexProperty("__metadata", mMetadata));
    }

    /**
     * Returns a new instance of {@link ODataEntityBuilder} class.
     * 
     * @param type Type of new entity.
     * @return Object that can be used for building an entity.
     */
    public static final ODataEntityBuilder newEntity(String type) {
        return new ODataEntityBuilder(type);
    }

    /**
     * Adds field to currently building entity.
     * 
     * @param name Field name.
     * @param value Field value. May be null.
     * @return The builder object.
     * @throws IllegalArgumentException Thrown if given field name is invalid.
     */
    public ODataEntityBuilder add(String name, ODataValue value) throws IllegalArgumentException {
        checkName(name);
        mCurrent.addProperty(createProperty(name, value));
        return this;
    }

    /**
     * Adds field to currently building entity.
     * 
     * @param name Field name.
     * @param value Field value. May be null.
     * @return The builder object.
     * @throws IllegalArgumentException Thrown if given field name is invalid.
     */
    public ODataEntityBuilder add(String name, Object value) throws IllegalArgumentException {
        checkName(name);
        mCurrent.addProperty(createProperty(name, value));
        return this;
    }

    /**
     * Adds field to metadata of currently building entity.
     * 
     * @param name Field name.
     * @param value Field value. May be null.
     * @return The builder object.
     * @throws IllegalArgumentException Thrown if given field name is invalid.
     */
    public ODataEntityBuilder addMetadataProperty(String name, ODataValue value) throws IllegalArgumentException {
        checkName(name);
        mMetadata.add(createProperty(name, value));
        return this;
    }

    /**
     * Adds field to metadata of currently building entity.
     * 
     * @param name Field name.
     * @param value Field value. May be null.
     * @return The builder object.
     * @throws IllegalArgumentException Thrown if given field name is invalid.
     */
    public ODataEntityBuilder addMetadataProperty(String name, Object value) throws IllegalArgumentException {
        checkName(name);
        mMetadata.add(createProperty(name, value));
        return this;
    }
    
    /**
     * Builds an entity.
     * 
     * @return An entity was built.
     */
    public ODataEntity build() {
        return mCurrent;
    }

    /**
     * Creates {@link ODataProperty} based on given name and value.
     * 
     * @param name Property name.
     * @param value Property value.
     * @return Created property.
     */
    private ODataProperty createProperty(String name, ODataValue value) {
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
     * Creates {@link ODataPrimitiveValue} as {@link ODataProperty} based on given name and value. Also automatically gets value type.
     * 
     * @param name Property name.
     * @param value Property value.
     * @return {@link ODataProperty} may be attached to {@link ODataEntity} or to {@link ODataComplexValue}.
     */
    private ODataProperty createProperty(String name, Object value) {
        return ODataFactory.newPrimitiveProperty(name, new ODataPrimitiveValue.Builder().setValue(value).setType(EdmSimpleType.fromObject(value))
                .build());
    }

    /**
     * Checks if given field name is correct and throws an exception if it is not.
     * 
     * @param name Field name for checking.
     * @throws IllegalArgumentException Thrown when name is incorrect.
     */
    private void checkName(String name) throws IllegalArgumentException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name must be a non-empty character string");
        }
    }
}
