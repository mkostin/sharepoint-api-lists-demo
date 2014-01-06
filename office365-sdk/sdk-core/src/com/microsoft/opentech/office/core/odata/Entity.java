package com.microsoft.opentech.office.core.odata;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import android.text.TextUtils;
import android.util.Pair;

import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataReader;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

/**
 * Represents a structured object.
 */
public class Entity implements Serializable {

    private static final long serialVersionUID = 1708624818507606418L;

    private static final String METADATA_KEY = "__metadata";

    private static final String TYPE_KEY = "type";

    private static final String SHAREPOINT_ROOT_OBJECT_KEY = "d";

    protected final TreeMap<String, Object> mFields;

    protected final TreeMap<String, Object> mMetadata;

    /**
     * Creates a new instance of {@link Entity} class.
     */
    protected Entity() {
        mFields = new TreeMap<String, Object>();
        mMetadata = new TreeMap<String, Object>();
    }

    /**
     * Gets an object related to specified name.
     * 
     * @param name Name of field to be retrieved.
     * @return Object related to specified name.
     * @throws IllegalArgumentException Thrown when given name is not found in current {@link Entity} instance.
     */
    public Object get(String name) throws IllegalArgumentException {
        if (mFields.containsKey(name)) {
            return mFields.get(name);
        } else {
            throw new IllegalArgumentException("Key \"" + name + "\" not found in current Entity instance");
        }
    }

    /**
     * Gets an object related to specified name from metadata.
     * 
     * @param name Name of field to be retrieved.
     * @return Object related to specified name.
     * @throws IllegalArgumentException Thrown when given name is not found in metadata of current {@link Entity} instance.
     */
    public Object getMeta(String name) throws IllegalArgumentException {
        if (mMetadata.containsKey(name)) {
            return mMetadata.get(name);
        } else {
            throw new IllegalArgumentException("Key \"" + name + "\" not found in metadata of current Entity instance");
        }
    }

    /**
     * Returns an object to iterate through all fields.
     * 
     * @return Iterator on fields.
     */
    public Iterator<Pair<String, Object>> iterator() {
        return new Iterator<Pair<String, Object>>() {

            private Iterator<String> keysIterator = mFields.keySet().iterator();

            public boolean hasNext() {
                return keysIterator.hasNext();
            }

            public Pair<String, Object> next() {
                String key = keysIterator.next();
                return new Pair<String, Object>(key, mFields.get(key));
            }

            public void remove() {
                throw new UnsupportedOperationException("Cannot remove from Entity");
            }
        };
    }

    /**
     * Creates a builder from json string.
     * 
     * @param json JSON string to create builder from.
     * @return Builder for Entity.
     * @throws Exception Thrown when an error occurred during json parsing or Entity building.
     */
    public static Builder from(Object json) throws IllegalArgumentException {
        ODataEntity odataEntity;
        if (json instanceof String) {
            try {
                odataEntity = ODataReader.readEntity(new ByteArrayInputStream(((String) json).getBytes()), ODataPubFormat.JSON_VERBOSE_METADATA);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        } else if (json instanceof ODataEntity) {
            odataEntity = (ODataEntity) json;
        } else {
            throw new IllegalArgumentException("Entity.Builder.from(): you must pass a string containing correct JSON to this method");
        }

        ODataComplexValue properties;
        // if this json retrieved from sharepoint, all payload is located in "d" object
        if (odataEntity.getProperties().size() == 1 && odataEntity.getProperties().get(0).getName().equals(SHAREPOINT_ROOT_OBJECT_KEY)) {
            properties = odataEntity.getProperty(SHAREPOINT_ROOT_OBJECT_KEY).getComplexValue();
        } else {
            throw new UnsupportedOperationException("Parsing of non-sharepoint json is not implemented yet");
        }

        Builder builder = new Builder();
        for (ODataProperty property : properties) {
            if (METADATA_KEY.equals(property.getName())) {
                for (ODataProperty meta : property.getComplexValue()) {
                    builder.setMeta(meta.getName(), fromODataObject(meta.getValue()));
                }
            } else {
                builder.set(property.getName(), fromODataObject(property.getValue()));
            }
        }

        return builder;
    }

    /**
     * Converts {@link Entity} to {@link ODataEntity} instance.
     * 
     * @param entity Entity to be converted.
     * @return {@link ODataEntity} instance.
     * @throws IllegalArgumentException Thrown when unable to convert an entity.
     */
    private static final ODataEntity getODataEntity(Entity entity) throws IllegalArgumentException {
        ODataEntity odataEntity = ODataFactory.newEntity("");
        Iterator<Pair<String, Object>> iterator = entity.iterator();
        while (iterator.hasNext()) {
            Pair<String, Object> pair = iterator.next();
            odataEntity.addProperty(getODataProperty(pair));
        }

        return odataEntity;
    }

    /**
     * Converts given object to {@link ODataValue}.
     * 
     * @param value Object to be converted.
     * @return {@link ODataValue}.
     * @throws IllegalArgumentException Thrown when unable to convert.
     */
    private static ODataValue toODataObject(Object value) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }

        if (value instanceof ODataValue) {
            return (ODataValue) value;
        }

        if (value instanceof Entity) {
            ODataComplexValue complex = new ODataComplexValue("");
            Iterator<Pair<String, Object>> iterator = ((Entity) value).iterator();
            while (iterator.hasNext()) {
                Pair<String, Object> field = iterator.next();
                complex.add(getODataProperty(field));
            }

            return complex;
        }

        if (value instanceof List || value instanceof Object[] || value instanceof Vector) {
            ODataCollectionValue collection = new ODataCollectionValue("");
            if (value instanceof List) {
                for (Object item : (List<?>) value) {
                    collection.add(toODataObject(item));
                }
            } else if (value instanceof Vector) {
                for (Object item : (Vector<?>) value) {
                    collection.add(toODataObject(item));
                }
            } else {
                for (Object item : (Object[]) value) {
                    collection.add(toODataObject(item));
                }
            }

            return collection;
        }

        try {
            return new ODataPrimitiveValue.Builder().setValue(value).setType(EdmSimpleType.fromObject(value)).build();
        } catch (IllegalArgumentException e) {
            return (ODataComplexValue) value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Cannot cast this object to OData type");
        }
    }

    /**
     * Returns given {@link Pair} as {@link ODataProperty}.
     * 
     * @param field Value to be converted.
     * @return Given value as {@link ODataProperty}.
     */
    private static ODataProperty getODataProperty(Pair<String, Object> field) {
        ODataValue value = toODataObject(field.second);
        if (value == null) {
            return ODataFactory.newPrimitiveProperty(field.first, null);
        }
        if (value.isPrimitive()) {
            return ODataFactory.newPrimitiveProperty(field.first, value.asPrimitive());
        }
        if (value.isCollection()) {
            return ODataFactory.newCollectionProperty(field.first, value.asCollection());
        }

        return ODataFactory.newComplexProperty(field.first, value.asComplex());
    }
    
    /**
     * Converts {@link ODataValue} to ODataJClient library independent object.
     * 
     * @param value Object to be converted.
     * @return <code>null</code> if value is null; primitive if given value is primitive; List&lt;Object&rt; if given value is collention;
     *         {@link Entity} if given value is complex.
     */
    private static Object fromODataObject(ODataValue value) {
        if (value == null) {
            return null;
        }

        if (value.isPrimitive()) {
            return value.asPrimitive().toValue();
        }

        if (value.isCollection()) {
            List<Object> collection = new ArrayList<Object>();
            Iterator<ODataValue> iterator = value.asCollection().iterator();
            while (iterator.hasNext()) {
                collection.add(fromODataObject(iterator.next()));
            }

            return collection;
        }

        Builder builder = new Builder();
        Iterator<ODataProperty> iterator = value.asComplex().iterator();
        while (iterator.hasNext()) {
            ODataProperty property = iterator.next();
            builder.set(property.getName(), fromODataObject(property.getValue()));
        }

        return builder.build();
    }

    /**
     * Checks if given name is correct.
     * 
     * @param name Name to be checked.
     * @throws IllegalArgumentException Thrown if given name is incorrect.
     */
    private static void checkName(String name) throws IllegalArgumentException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("ComplexValue.checkName(): Name cannot be empty or null");
        }
    }

    /**
     * Provides methods to build an {@link Entity}.
     */
    public static class Builder implements IEntityBuilder<Entity> {

        private Entity mCurrent;

        private String mTypeName;

        /**
         * Creates a new instance of {@link Builder} class.
         * 
         * @param typeName Name of entity type.
         */
        public Builder(String typeName) {
            mCurrent = new Entity();
            mTypeName = typeName;
        }

        /**
         * Creates a new instance of {@link Builder} class.
         */
        public Builder() {
            this(null);
        }

        public Builder set(String name, Object value) throws IllegalArgumentException {
            checkName(name);
            mCurrent.mFields.put(name, value);
            return this;
        }

        public Builder setMeta(String name, Object value) throws IllegalArgumentException {
            checkName(name);
            mCurrent.mMetadata.put(name, value);
            return this;
        }

        /**
         * Builds an {@link Entity}.
         * 
         * @return {@link Entity} was built.
         */
        public Entity build() throws IllegalStateException {
            // try to set type
            // if type already contained in map, it has higher priority
            if (!mCurrent.mMetadata.containsKey(TYPE_KEY) && !TextUtils.isEmpty(mTypeName)) {
                setMeta(TYPE_KEY, mTypeName);
            }
            if (mCurrent.mMetadata.size() > 0) {
                set(METADATA_KEY, convertMetadata(mCurrent.mMetadata));
            }
            return mCurrent;
        }

        /**
         * Represents metadata as an {@link Entity} to be added to entity.
         * 
         * @param metadata Metadata to be converted.
         * @return Entity was created.
         */
        private Entity convertMetadata(TreeMap<String, Object> metadata) {
            Builder builder = new Builder();
            for (Map.Entry<String, Object> field : metadata.entrySet()) {
                builder.set(field.getKey(), field.getValue());
            }

            return builder.build();
        }
    }
}
