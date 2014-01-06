package com.microsoft.opentech.office.core.network.odata;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Pair;

import com.microsoft.opentech.office.core.Configuration;
import com.microsoft.opentech.office.core.network.NetworkException;
import com.microsoft.opentech.office.core.network.NetworkOperation;
import com.microsoft.opentech.office.core.odata.Entity;
import com.microsoft.opentech.office.core.odata.async.ICallback;
import com.msopentech.odatajclient.engine.communication.request.ODataBasicRequestImpl;
import com.msopentech.odatajclient.engine.communication.request.ODataRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
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
     * Indicates if current operation requires to set X-RequestDigest header for performing.
     */
    protected final boolean mRequiresDigest;

    /**
     * Creates a new instance of {@link ODataOperation} class.
     *
     * @param listener Listener to be executed when operation finished.
     * @param context Application context.
     * @param requiresDigest Determines should current operation set X-RequestDigest header for performing.
     */
    public ODataOperation(ICallback<RESULT> listener, Context context, boolean requiresDigest) {
        super(listener, context);
        mRequiresDigest = requiresDigest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected URI getServerUrl() {
        return URI.create(Configuration.getServerBaseUrl());
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException when unable to set headers for request.
     * @throws IOException when an I/O error occurred during operation execution or connection was abroted.
     * @throws ClientProtocolException when an HTTP protocol error occurred during operation execution.
     */
    @Override
    public RESULT execute() throws UnsupportedOperationException, ClientProtocolException, IOException {

        try {
            REQUEST req = getRequest();
            setRequestHeaders(req, getRequestHeaders());

            if (mRequiresDigest) {
                req.addCustomHeader(REQUEST_DIGEST_HEADER_NAME, getDigest());
            }

            handleServerResponse(req.execute());

            mCallbackWrapper.onDone(mResult);

            return mResult;
        } catch (Exception e) {
            mCallbackWrapper.onError(e);
            throw new NetworkException("OdataOperation.execute() - Error: " + e.getMessage(), e);
        }
    }

    @Override
    protected List<Pair<String, String>> getRequestHeaders() throws UnsupportedOperationException {
        List<Pair<String, String>> headers = super.getRequestHeaders();
        try {
            headers.add(Pair.create(ACCEPT_HTTP_HEADER_NAME, SHAREPOINT_CONTENT_TYPE_JSON));

            // Content-Type header needed for operations that contain body, like create or update.
            // If we set this header for other operations, it will be ignored.
            // Server expects data in application/atom+xml format by default.
            headers.add(Pair.create(CONTENT_TYPE_HTTP_HEADER_NAME, SHAREPOINT_CONTENT_TYPE_JSON));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
        return headers;
    }

    /**
     * Gets form-digest-request result used to sign an operation.
     *
     * @return Value to be set to <i>X-RequestDigest</i> header.
     * @throws IOException when an I/O error occurred during digest retrieving.
     * @throws RuntimeException when an error occurred during digest retrieving.
     */
    protected final String getDigest() throws RuntimeException, IOException {
        DigestRequestOperation digestOper = new DigestRequestOperation(null, mContext);
        return digestOper.execute();
    }

    /**
     * Handles server response. Default implementation does nothing and returns true.
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

    protected static ODataEntity getODataEntity(Entity entity) {
        try {
            //TODO: fix this by limiting access level w/o reflection
            Method method = Entity.class.getDeclaredMethod("getODataEntity", entity.getClass());
            method.setAccessible(true);
            return (ODataEntity) method.invoke(null, entity);
        } catch (Exception e) {
            return null;
        }
    }
}
