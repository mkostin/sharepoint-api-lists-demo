package com.microsoft.opentech.office.odata;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.text.TextUtils;
import android.util.Pair;

import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataReader;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class Entity implements Serializable {

    private static final long serialVersionUID = 1708624818507606418L;

    private static final String METADATA_KEY = "__metadata";

    private static final String TYPE_KEY = "type";

    private static final String SHAREPOINT_ROOT_OBJECT_KEY = "d";

    private final TreeMap<String, Object> mFields;

    private final TreeMap<String, Object> mMetadata;

    private Entity() {
        mFields = new TreeMap<String, Object>();
        mMetadata = new TreeMap<String, Object>();
    }

    public Object get(String name) throws IllegalArgumentException {
        if (mFields.containsKey(name)) {
            return mFields.get(name);
        } else {
            throw new IllegalArgumentException("Key \"" + name + "\" not found in current Entity instance");
        }
    }

    public Object getMeta(String name) throws IllegalArgumentException {
        if (mMetadata.containsKey(name)) {
            return mMetadata.get(name);
        } else {
            throw new IllegalArgumentException("Key \"" + name + "\" not found in metadata of current Entity instance");
        }
    }

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
                odataEntity = ODataReader.readEntity(new ByteArrayInputStream(((String)json).getBytes()), ODataPubFormat.JSON_VERBOSE_METADATA);
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

    private static void checkName(String name) throws IllegalArgumentException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("ComplexValue.checkName(): Name cannot be empty or null");
        }
    }

    public static class Builder implements IEntityBuilder<Entity> {

        private Entity mCurrent;

        private String mTypeName;

        public Builder(String typeName) {
            mCurrent = new Entity();
            mTypeName = typeName;
        }

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
        
        private Entity convertMetadata(TreeMap<String, Object> metadata) {
            Builder builder = new Builder();
            for (Map.Entry<String, Object> field: metadata.entrySet()) {
                builder.set(field.getKey(), field.getValue());
            }
            
            return builder.build();
        }
    }
}
