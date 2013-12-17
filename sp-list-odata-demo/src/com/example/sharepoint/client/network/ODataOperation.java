package com.example.sharepoint.client.network;

import java.net.URI;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Base64;
import android.util.Pair;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.communication.request.ODataBasicRequestImpl;
import com.msopentech.odatajclient.engine.communication.request.ODataRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataComplexValue;
import com.msopentech.odatajclient.engine.data.ODataFactory;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;

/**
 * Implements common wrapper for OData operations based on OdataJClient library.
 * 
 * @param <REQUEST> Operation OData request type that extends {@link ODataRequest}.
 * @param <RESULT> Operation execution result.
 * @param <FORMAT> OData request format type, see {@link ODataPubFormat}, {@link ODataFormat}, {@link ODataValueFormat}
 */
public abstract class ODataOperation<REQUEST extends ODataBasicRequestImpl<? extends ODataResponse, FORMAT>, RESULT, FORMAT extends Enum<FORMAT>>
        extends NetworkOperation<REQUEST, ODataResponse, RESULT> {

    public static final String SHAREPOINT_ROOT_OBJECT_NAME = "d";

    public static final String SHAREPOINT_CONTENT_TYPE_JSON = "application/json;odata=verbose";

    protected static final String REQUEST_DIGEST_HEADER_NAME = "X-RequestDigest";

    protected static final String SHAREPOINT_LIST_DATA_TYPE = "SP.List";

    protected static final String SHAREPOINT_FIELD_URL_DATA_TYPE = "SP.FieldUrlValue";

    protected static final String SHAREPOINT_METADATA_FIELD_NAME = "__metadata";

    protected static final String SHAREPOINT_TYPE_FIELD_NAME = "type";

    protected static final String SHAREPOINT_BASE_TEMPLATE_FIELD_NAME = "BaseTemplate";

    protected static final String SHAREPOINT_DESCRIPTION_FIELD_NAME = "Description";

    protected static final String SHAREPOINT_TITLE_FIELD_NAME = "Title";

    protected static final String SHAREPOINT_LIST_ITEM_ENTITY_TYPE_FULL_NAME_FIELD_NAME = "ListItemEntityTypeFullName";

    protected static final String SHAREPOINT_URL_FIELD_NAME = "Url";

    protected static final String SHAREPOINT_IMAGE_FIELD_NAME = "Image";

    protected static final String SHAREPOINT_LISTS_URL_SUFFIX = "Web/Lists";

    protected static final String SHAREPOINT_ITEMS_URL_SUFFIX = "Items";

    protected static final String SHAREPOINT_URI_FIELD_NAME = "uri";

    protected static final String SHAREPOINT_ETAG_FIELD_NAME = "etag";

    protected static final String SHAREPOINT_ITEM_COUNT_FIELD_NAME = "ItemCount";

    protected static final String SHAREPOINT_RESULTS_FIELD_NAME = "results";

    protected static final String SHAREPOINT_METADATA_ID_FIELD_NAME = "id";

    /**
     * Creates a new instance of {@link ODataOperation} class.
     * 
     * @param listener Listener to be executed when operation finished.
     * @param context Application context.
     */
    public ODataOperation(OnOperaionExecutionListener listener, Context context) {
        super(listener, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected URI getServerUrl() {
        return URI.create(Constants.SP_BASE_URL);
    }

    @Override
    public void execute() {
        boolean result = false;

        try {
            REQUEST req = getRequest();
            setRequestHeaders(req, getRequestHeaders());

            req.addCustomHeader(REQUEST_DIGEST_HEADER_NAME, getDigest());

            result = handleServerResponse(req.execute());
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".execute(): Error.");
        } finally {
            if (mListener != null) {
                mListener.onExecutionComplete(this, result);
            }
        }
    }

    @Override
    protected List<Pair<String, String>> getRequestHeaders() {
        List<Pair<String, String>> headers = super.getRequestHeaders();
        try {
            headers.add(Pair.create(ACCEPT_HTTP_HEADER_NAME, SHAREPOINT_CONTENT_TYPE_JSON));
            
            // Content-Type header needed for operations that contain body, like create or update.
            // If we set this header for other operations, it will be ignored.
            // Server expects data in application/atom+xml format by default.
            headers.add(Pair.create(CONTENT_TYPE_HTTP_HEADER_NAME, SHAREPOINT_CONTENT_TYPE_JSON));
            
            AuthType auth = getAuthenticationType();
            if (auth == AuthType.Office365) {
                headers.add(Pair.create(COOKIE_HTTP_HEADER_NAME, Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH));
            } else if (auth == AuthType.Basic) {
                headers.add(Pair.create(
                        AUTHORIZATION_HTTP_HEADER_NAME,
                        BASIC_HTTP_AUTHORIZATION_PREFIX
                                + Base64.encodeToString((Constants.USERNAME + ":" + Constants.PASSWORD).getBytes(), Base64.DEFAULT).trim()));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    @Override
    public AuthType getAuthenticationType() {
        return AuthType.Office365;
    }

    /**
     * Gets form-digest-request result used to sign an operation.
     * 
     * @return Value to be set to <i>X-RequestDigest</i> header.
     */
    protected final String getDigest() {
        GetRequestDigestOperation digestOper = new GetRequestDigestOperation(null, mContext);
        digestOper.execute();
        return digestOper.getResult();
    }

    /**
     * Handles server response. Default implementation does nothing.
     * 
     * @param response Response to be handled.
     * @return <code>true</code> if response handled successfully, <code>false</code> otherwise.
     */
    protected boolean handleServerResponse(ODataResponse response) {
        return true;
    }

    /**
     * Returns operation execution result.
     * 
     * @return Operation result.
     */
    public RESULT getResult() {
        return mResult;
    }

    /**
     * Helper to set up headers.
     * 
     * @param req OData Request.
     * @param headers HTTP headers.
     */
    protected void setRequestHeaders(ODataBasicRequestImpl<? extends ODataResponse, FORMAT> req, List<Pair<String, String>> headers) {
        if (headers != null && req != null) {
            for (Pair<String, String> header : headers) {
                req.addCustomHeader(header.first, header.second);
            }
        }
    }

    /**
     * Generates metadata property based on given type and additional properties.
     * 
     * @param type Type of entity which metadata is generating for.
     * @param additionalProperties Additional properties to be added to metadata. May be null.
     * @return Metadata field as {@link ODataComplexValue}.
     */
    protected ODataComplexValue generateMetadata(String type, Map<String, ODataValue> additionalProperties) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        
        ODataComplexValue metadata = new ODataComplexValue(SHAREPOINT_METADATA_FIELD_NAME);
        metadata.add(ODataFactory.newPrimitiveProperty(SHAREPOINT_TYPE_FIELD_NAME, new ODataPrimitiveValue.Builder().setText(type).build()));
        
        if (additionalProperties == null) {
            return metadata;
        }
        
        for (Map.Entry<String, ODataValue> property: additionalProperties.entrySet()) {
            if (property.getValue() == null) {
                metadata.add(ODataFactory.newPrimitiveProperty(property.getKey(), null));
            }
            else if (property.getValue().isPrimitive()) {
                metadata.add(ODataFactory.newPrimitiveProperty(property.getKey(), property.getValue().asPrimitive()));
            }
            else if (property.getValue().isCollection()) {
                metadata.add(ODataFactory.newCollectionProperty(property.getKey(), property.getValue().asCollection()));
            }
            else if (property.getValue().isComplex()) {
                metadata.add(ODataFactory.newComplexProperty(property.getKey(), property.getValue().asComplex()));
            }
        }
        
        return metadata;
    }
}
