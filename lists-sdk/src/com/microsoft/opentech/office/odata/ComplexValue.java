package com.microsoft.opentech.office.odata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.text.TextUtils;

import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.data.metadata.edm.EdmSimpleType;

public class ComplexValue implements Serializable {

    private static final long serialVersionUID = 1708624818507606418L;

    private final TreeMap<String, Object> mMap;

    public ComplexValue() {
        mMap = new TreeMap<String, Object>();
    }

    public ComplexValue set(String name, Object value) throws IllegalArgumentException {
        checkName(name);
        mMap.put(name, getODataObject(value));
        return this;
    }

    public ComplexValue remove(String name) {
        mMap.remove(name);
        return this;
    }

    public Object get(String name) {
        return mMap.get(name);
    }

    private void checkName(String name) throws IllegalArgumentException {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("ComplexValue.checkName(): Name cannot be empty or null");
        }
    }

    static ODataValue getODataObject(Object value) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }

        if (value instanceof ODataValue) {
            return (ODataValue) value;
        }

        if (value instanceof ComplexValue) {
            ODataComplexValue complex = new ODataComplexValue("");
            for (Map.Entry<String, Object> field: ((ComplexValue)value).mMap.entrySet()) {
                ODataValue fieldValue = getODataObject(field.getValue());
                if (fieldValue == null) {
                    complex.add(ODataFactory.newPrimitiveProperty(field.getKey(), null));
                } else if (fieldValue.isPrimitive()) {
                    complex.add(ODataFactory.newPrimitiveProperty(field.getKey(), fieldValue.asPrimitive()));
                } else if (fieldValue.isCollection()) {
                    complex.add(ODataFactory.newCollectionProperty(field.getKey(), fieldValue.asCollection()));
                } else {
                    complex.add(ODataFactory.newComplexProperty(field.getKey(), fieldValue.asComplex()));
                }
            }

            return complex;
        }

        if (value instanceof List || value instanceof Object[]) {
            ODataCollectionValue collection = new ODataCollectionValue("");
            if (value instanceof List) {
                for (Object item: (List<?>) value) {
                    collection.add(getODataObject(item));
                }
            } else {
                for (Object item: (Object[])value) {
                    collection.add(getODataObject(item));
                }
            }

            return collection;
        }

        try {
            return new ODataPrimitiveValue.Builder().setValue(value).setType(EdmSimpleType.fromObject(value)).build();
        } catch (IllegalArgumentException e) {
            return (ODataComplexValue)value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Cannot cast this object to OData type");
        }
    }
}
