package com.microsoft.opentech.office.odata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;

/**
 * Wraps ODataEntity class.
 */
public class Entity implements Serializable {

    /**
     * Generated serial version uid.
     */
    private static final long serialVersionUID = -8889179594394726368L;

    /**
     * Wrapped value.
     */
    private final ODataEntity mEntity;

    /**
     * Creates new Entity with given type.
     *
     * @param typeName Type name.
     */
    Entity(String typeName) {
        mEntity = ODataFactory.newEntity("");
    }

    /**
     * Creates new Entity from ODataEntity passed from server.
     *
     * @param odataEntity Received entity.
     */
    Entity(ODataEntity odataEntity) {
        ODataComplexValue properties = odataEntity.getProperty("d").getComplexValue();
        ODataComplexValue metadata = properties.get("__metadata").getComplexValue();
        String type = metadata.get("type").getPrimitiveValue().toString();

        mEntity = ODataFactory.newEntity(type);
        for (ODataProperty property: metadata) {
            EntityBuilder.setMeta(mEntity, property.getName(), property.getValue());
        }

        for (ODataProperty property: properties) {
            if ("__metadata".equals(property.getName())) {
              continue;
            }

            EntityBuilder.set(mEntity, property.getName(), property.getValue());
        }
    }

    // TODO: hide this from end user
    public ODataEntity asODataEntity() {
        return mEntity;
    }

    public Object getMeta(String name) throws IllegalArgumentException {
        ODataComplexValue metadata = mEntity.getProperty("__metadata").getComplexValue();
        if (metadata.get(name) == null) {
            throw new IllegalArgumentException("Field \"" + name + "\" not found in metadata");
        }

        ODataProperty property = metadata.get(name);
        if (property.hasNullValue()) {
            return null;
        }

        if (property.hasPrimitiveValue()) {
            return property.getValue().asPrimitive().toValue();
        }

        if (property.hasCollectionValue()) {
            ArrayList<Object> collection = new ArrayList<Object>();
            Iterator<ODataValue> iterator = property.getCollectionValue().iterator();
            while (iterator.hasNext()) {
                ODataValue next = iterator.next();
                collection.add(next);
            }

            return collection;
        }

        ComplexValue complex = new ComplexValue();
        Iterator<ODataProperty> iterator = property.getComplexValue().iterator();
        while (iterator.hasNext()) {
            ODataProperty next = iterator.next();
            complex.set(next.getName(), next.getValue());
        }

        return complex;
    }

    public Entity set(String name, Object value) throws IllegalArgumentException {
        ODataValue odataValue = ComplexValue.getODataObject(value);
        if (mEntity.getProperty(name) != null) {
            mEntity.removeProperty(mEntity.getProperty(name));
        }
        if (odataValue == null || odataValue.isPrimitive()) {
            mEntity.addProperty(ODataFactory.newPrimitiveProperty(name, odataValue.asPrimitive()));
        } else if (odataValue.isCollection()) {
            mEntity.addProperty(ODataFactory.newCollectionProperty(name, odataValue.asCollection()));
        } else {
            mEntity.addProperty(ODataFactory.newComplexProperty(name, odataValue.asComplex()));
        }

        return this;
    }
}
