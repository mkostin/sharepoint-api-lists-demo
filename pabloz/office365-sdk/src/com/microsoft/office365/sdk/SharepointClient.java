package com.microsoft.office365.sdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.office365.sdk.http.HttpConnection;
import com.microsoft.office365.sdk.http.HttpConnectionFuture;
import com.microsoft.office365.sdk.http.Request;
import com.microsoft.office365.sdk.http.Response;

public class SharepointClient {
	private Credentials mCredentials;
	private String mSiteUrl;
	
	public SharepointClient(String siteUrl, Credentials credentials) {
		if (credentials == null) {
			throw new IllegalArgumentException("credentials must not be null");
		}
		
		if (siteUrl == null) {
			throw new IllegalArgumentException("siteUrl must not be null");
		}
		
		mCredentials = credentials;
		mSiteUrl = siteUrl;
		
		if (!mSiteUrl.endsWith("/")) {
			mSiteUrl += "/";
		}
	}

	private OfficeFuture<String> getFormDigest() {
		HttpConnection connection = Platform.createHttpConnection();
		
		Request request = new Request("POST");
		
		request.setUrl(mSiteUrl + "_api/contextinfo");
		
		prepareRequest(request);
		
		final OfficeFuture<String> result = new OfficeFuture<String>();
		HttpConnectionFuture future = connection.execute(request);
		
		future.done(new Action<Response>() {
			
			@Override
			public void run(Response response) throws Exception {
				
				if (isValidStatus(response.getStatus())) {
					String responseContent = response.readToEnd();
					
					JSONObject json = new JSONObject(responseContent);
					
					result.setResult(json.getJSONObject("d").getJSONObject("GetContextWebInformation").getString("FormDigestValue"));
				} else {
					result.triggerError(new Exception(response.readToEnd()));
				}
			}
		});
		
		future.onError(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});
		
		future.onTimeout(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});

		return result;
	}
	
	public OfficeFuture<SPList> getList(String listName) {
		HttpConnection connection = Platform.createHttpConnection();
		
		Request request = new Request("GET");
		
		String getListUrl = mSiteUrl + "_api/web/lists/GetByTitle('%s')"; 
		request.setUrl(String.format(getListUrl, listName));
		
		prepareRequest(request);
		
		final OfficeFuture<SPList> result = new OfficeFuture<SPList>();
		HttpConnectionFuture future = connection.execute(request);
		
		future.done(new Action<Response>() {
			
			@Override
			public void run(Response response) throws Exception {
				
				if (isValidStatus(response.getStatus())) {
					String responseContent = response.readToEnd();
					
					JSONObject json = new JSONObject(responseContent);
					result.setResult(new SPList(json));
				} else {
					result.triggerError(new Exception(response.readToEnd()));
				}
			}
		});
		
		future.onError(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});
		
		future.onTimeout(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});

		return result;
	}
	
	private void prepareRequest(Request request) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Accept", "application/json;odata=verbose");
		request.setHeaders(headers);
		
		mCredentials.prepareRequest(request);
	}

	public OfficeFuture<List<SPListItem>> getListItems(SPList list) {
		return getListItems(list.getTitle());
	}	
	
	public OfficeFuture<List<SPListItem>> getListItems(String listTitle) {
		HttpConnection connection = Platform.createHttpConnection();
		
		Request request = new Request("GET");
		
		String getListUrl = mSiteUrl + "_api/web/lists/GetByTitle('%s')/Items"; 
		request.setUrl(String.format(getListUrl, listTitle));
		
		prepareRequest(request);
		
		final OfficeFuture<List<SPListItem>> result = new OfficeFuture<List<SPListItem>>();
		HttpConnectionFuture future = connection.execute(request);
		
		future.done(new Action<Response>() {
			
			@Override
			public void run(Response response) throws Exception {
				
				if (isValidStatus(response.getStatus())) {
					String responseContent = response.readToEnd();
					
					JSONObject json = new JSONObject(responseContent);
					result.setResult(SPListItem.listFromJson(json));
				} else {
					result.triggerError(new Exception(response.readToEnd()));
				}
			}
		});
		
		future.onError(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});
		
		future.onTimeout(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});

		return result;
	}
	
	public OfficeFuture<Void> insertListItem(final SPList list, final Map<String, Object> values) throws JSONException {
		final OfficeFuture<Void> result = new OfficeFuture<Void>();
		
		OfficeFuture<String> digestFuture = getFormDigest();
		digestFuture.onError(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});
		
		digestFuture.done(new Action<String>() {
			
			@Override
			public void run(String digest) throws Exception {
				HttpConnection connection = Platform.createHttpConnection();
				
				Request request = new Request("POST");
				prepareRequest(request);
				
				request.addHeader("Content-Type", "application/json;odata=verbose");
				request.addHeader("X-RequestDigest", digest);
				
				JSONObject payload = new JSONObject();
				JSONObject metadata = new JSONObject();
				metadata.put("type", list.getListItemEntityTypeFullName());
				payload.put("__metadata", metadata);
				
				for (String key : values.keySet()) {
					payload.put(key, values.get(key));
				}
				
				String getListUrl = mSiteUrl + "_api/web/lists/GetByTitle('%s')/Items"; 
				request.setUrl(String.format(getListUrl, list.getTitle()));
				
				request.setContent(payload.toString());
				
				HttpConnectionFuture future = connection.execute(request);
				
				future.done(new Action<Response>() {
					
					@Override
					public void run(Response response) throws Exception {
						
						if (isValidStatus(response.getStatus())) {
							//String responseContent = response.readToEnd();
							
							//JSONObject json = new JSONObject(responseContent);
							result.setResult(null);
						} else {
							result.triggerError(new Exception(response.readToEnd()));
						}
					}

				});
				
				future.onError(new ErrorCallback() {
					
					@Override
					public void onError(Throwable error) {
						result.triggerError(error);
					}
				});
				
				future.onTimeout(new ErrorCallback() {
					
					@Override
					public void onError(Throwable error) {
						result.triggerError(error);
					}
				});
			}
		});
		
		return result;
	}
	
	public OfficeFuture<Void> uploadFile(final String documentLibraryName, final String fileName, final byte[] fileContent) {
		final OfficeFuture<Void> result = new OfficeFuture<Void>();
		
		OfficeFuture<String> digestFuture = getFormDigest();
		digestFuture.onError(new ErrorCallback() {
			
			@Override
			public void onError(Throwable error) {
				result.triggerError(error);
			}
		});
		
		digestFuture.done(new Action<String>() {
			
			@Override
			public void run(String digest) throws Exception {
				HttpConnection connection = Platform.createHttpConnection();
				
				Request request = new Request("POST");
				prepareRequest(request);
				
				request.addHeader("X-RequestDigest", digest);
				
				String getListUrl = mSiteUrl + "_api/web/GetFolderByServerRelativeUrl('%s')/Files/add(url='%s',overwrite=true)"; 
				request.setUrl(String.format(getListUrl, documentLibraryName, fileName));
				
				request.setContent(fileContent);
				
				HttpConnectionFuture future = connection.execute(request);
				
				future.done(new Action<Response>() {
					
					@Override
					public void run(Response response) throws Exception {
						
						if (isValidStatus(response.getStatus())) {
							//String responseContent = response.readToEnd();
							
							//JSONObject json = new JSONObject(responseContent);
							result.setResult(null);
						} else {
							result.triggerError(new Exception(response.readToEnd()));
						}
					}

				});
				
				future.onError(new ErrorCallback() {
					
					@Override
					public void onError(Throwable error) {
						result.triggerError(error);
					}
				});
				
				future.onTimeout(new ErrorCallback() {
					
					@Override
					public void onError(Throwable error) {
						result.triggerError(error);
					}
				});
			}
		});
		
		return result;
	}
	
	private static boolean isValidStatus(int status) {
		return status >= 200 && status <= 299;
	}
}
