package com.example.office365_app;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.microsoft.office365.sdk.Action;
import com.microsoft.office365.sdk.Credentials;
import com.microsoft.office365.sdk.ErrorCallback;
import com.microsoft.office365.sdk.OfficeFuture;
import com.microsoft.office365.sdk.SPFieldUrlValue;
import com.microsoft.office365.sdk.SPFile;
import com.microsoft.office365.sdk.SPList;
import com.microsoft.office365.sdk.SPListField;
import com.microsoft.office365.sdk.SharepointClient;
import com.microsoft.office365.sdk.SharepointOnlineCredentials;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateRecordAndUploadFileActivity extends Activity {

	final static int CAMARA_REQUEST_CODE = 1000;
	final static int SELECT_PHOTO = 1001;
	
	final static String SHAREPOINT_SITE = "https://lagashsystems365.sharepoint.com/sites/Argentina/Produccion/";
	final static String CLIENT_ID = "c2c8ce1c-2a18-4ea6-8034-96935c451dd6";
	final static String REDIRECT_URL = "https://www.lagash.com/login";
	final static String OFFICE_365_DOMAIN = "lagash.com";
	final static String CLIENT_SECRET = "OCT+qIaOXMEmaQ5slKM7dd+24JcLUimWpSiGlqUHYUg=";
	final static String REFRESH_TOKEN_KEY = "office_refresh_token";
	
	private Credentials mCredentials = null;
	private String mSharepointList = null;
	private String mPictureUrl = null;
	
	final CharSequence[] sourceOptions = {"From Library", "From Camera"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_record_and_upload_file);
		
		setTitle("Sharepoint Online Demo");
		
		findViewById(R.id.btnLoadValues).setEnabled(false);
		findViewById(R.id.btnUploadPhoto).setEnabled(false);
		
		findViewById(R.id.btnLoadValues).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadValues();
			}
		});
		
		findViewById(R.id.btnUploadPhoto).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				uploadPhoto();
			}
		});
		
		findViewById(R.id.btnSelectList).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectList();
			}
		});
	}

	protected void selectList() {
		final Activity that = this;
		
		loadCredentials()
			.onError(new DefaultErrorCallback())
			.done(new Action<Void>() {
				
				@Override
				public void run(Void obj) throws Exception {
					SharepointClient client = new SharepointClient(SHAREPOINT_SITE, mCredentials);
					
					client.getLists()
						.onError(new DefaultErrorCallback())
						.done(new Action<List<SPList>>() {
							
							@Override
							public void run(final List<SPList> lists) throws Exception {
								final List<String> listTitles = new ArrayList<String>();
								for (SPList list : lists) {
									listTitles.add(list.getTitle());
								}
								
								runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										AlertDialog.Builder builder = new AlertDialog.Builder(that);
									    builder
											.setTitle("Select a list")
											.setItems(listTitles.toArray(new String[0]), new DialogInterface.OnClickListener() {
									               public void onClick(DialogInterface dialog, int which) {
									            	   mSharepointList = listTitles.get(which);
									            	   findViewById(R.id.btnUploadPhoto).setEnabled(true);
									           }
										    });
									    
									    builder.create().show();
									}
								});
							}
						});
				}
			});
	}

	protected void loadValues() {
		if (mSharepointList == null) {
			return;
		}
		
		final String text = ((EditText)findViewById(R.id.txtTitle)).getText().toString();
		String txtNumber = ((EditText)findViewById(R.id.txtNumber)).getText().toString();
		int n = 0;
		
		try {
			n = Integer.parseInt(txtNumber);
		} catch (Throwable error) {
		}
		
		final int number = n;
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				loadCredentials()
					.onError(new DefaultErrorCallback())
					.done(new Action<Void>() {
					
					@Override
					public void run(Void obj) throws Exception {
						
						try {
							SharepointClient client = new SharepointClient(SHAREPOINT_SITE, mCredentials);
			
							//SPList list = client.getList("RegularList").get();
							SPList list = client.getList(mSharepointList).get();
							
							List<SPListField> fields = client.getListFields("RegularList").get();
							
							String numberColumnFieldName = getColumnName("NumberColumn", fields);
							String pictureColumnFieldName = getColumnName("PictureColumn", fields);
							
							Map<String, Object> values = new HashMap<String, Object>();
							values.put("Title", text);
							values.put(numberColumnFieldName, number); // EntityPropertyName[NumberColumn]
							
							if (mPictureUrl != null) {
								values.put(pictureColumnFieldName, SPFieldUrlValue.getJsonForUrl(mPictureUrl, "Uploaded picture"));
							}
							
							client.insertListItem(list, values).get();							
							createAndShowDialog("Values created", "Success!");
						
						} catch (Throwable e) {
							createAndShowDialog(e);
						}
					}
				});
				
				return null;
			}
			
		}.execute();
		
		
	}
	
	protected String getColumnName(String columnName, List<SPListField>fields ) {
		for (SPListField field : fields) {
			if (field.getTitle().equals(columnName)) {
				return field.getEntityPropertyName();
			}
		}
		
		return null;
	}
	
	private final byte[] getImageData(int requestCode, int resultCode, Intent data){
			
		switch(requestCode){
			case SELECT_PHOTO:{
				if (resultCode == RESULT_OK) {
				
		            try {
			            Uri selectedImage = data.getData();
						InputStream imageStream = getContentResolver().openInputStream(selectedImage);
						Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
				            
						ByteArrayOutputStream stream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
			            bitmap.compress(CompressFormat.JPEG, 100, stream);
			            
			            return stream.toByteArray();
			            
					} catch (Throwable t) {					
						createAndShowDialog(t);
					}		            
				}
			}
			case CAMARA_REQUEST_CODE: {
				if (resultCode == RESULT_OK) {				
					try{
						Bitmap bitmap = (Bitmap) data.getExtras().get("data");
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						
						return stream.toByteArray();

					}catch (Throwable t){
						createAndShowDialog(t);
					}
				}
			}
			default: break;
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		final byte[] bytes = getImageData(requestCode, resultCode, data);
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
						loadCredentials()
							.onError(new DefaultErrorCallback())
							.done(new Action<Void>() {
							
						@Override
						public void run(Void obj) throws Exception {
								
								try {
									
									SharepointClient client = new SharepointClient(SHAREPOINT_SITE, mCredentials);
									String fileName = UUID.randomUUID().toString();
									SPFile file = client.uploadFile("DocLib", fileName + ".jpg", bytes).get();
									mPictureUrl = extractServerUrl(SHAREPOINT_SITE) + file.getServerRelativeURL();
									
									runOnUiThread(new Runnable() {
										
										@Override
										public void run() {
											findViewById(R.id.btnLoadValues).setEnabled(true);
										}
									});
									createAndShowDialog("Photo uploaded", "Success!");
								
								} catch (Throwable e) {
									createAndShowDialog(e);
								}
							}							
						});
						return null;
					}	
				}.execute();
	}
	
	private String extractServerUrl(String sharepointSite) {
		Uri uri = Uri.parse(sharepointSite);
		return uri.getScheme() + "://" + uri.getHost();
	}
	
	private void openPhotoSource(int itemSelected){
		
		switch(itemSelected){
		case 0:
				invokePhotoLibrayIntent();
				break;
		case 1:
				invokeFromCameraIntent();
				break;
		default:
				break;
		}
	}
	
	private void invokeFromCameraIntent(){		
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // start the image capture Intent
	    startActivityForResult(cameraIntent, CAMARA_REQUEST_CODE);
	}
	
	private void invokePhotoLibrayIntent(){
	    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	    photoPickerIntent.setType("image/*");
	    startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
	}
	
	private void uploadPhoto() {
		
	    final Activity that = this;
	    
	    try{
	    	runOnUiThread(new Runnable(){
	    		@Override
	    		public void run(){	
	    			try{
						//TODO: en vez de charSequence podríamos usar un ListAdaper, pero es medio bardo
						//por ahora. 
	    				AlertDialog.Builder builder = new AlertDialog.Builder(that);
					    builder
							.setTitle("Select an option:")				
							.setSingleChoiceItems(sourceOptions, 0, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int item) {				            	  
					            	   openPhotoSource(item);
					            	   findViewById(R.id.btnUploadPhoto).setEnabled(true);
					           }
						    });					    
					    builder.create().show();
	    			}catch(Throwable t){
	    				
	    			}
	    		}
	    	});
	    }catch(Throwable t){
	    	
	    }
	}

	private OfficeFuture<Void> loadCredentials() {
		final OfficeFuture<Void> dummyFuture = new OfficeFuture<Void>();
		final Activity that = this;
		if (mCredentials == null) {
			try {
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							
							String refreshToken = getStoredRefreshToken();
							
							SharepointOnlineCredentials.requestCredentials(that, SHAREPOINT_SITE, CLIENT_ID, REDIRECT_URL, OFFICE_365_DOMAIN, CLIENT_SECRET, refreshToken)
								.onError(new DefaultErrorCallback())
								.done(new Action<SharepointOnlineCredentials>() {
								
								@Override
								public void run(SharepointOnlineCredentials credentials) throws Exception {
									mCredentials = credentials;
									storeRefreshToken(credentials.getRefreshToken());
									dummyFuture.setResult(null);
								}
							});
						} catch (Throwable t) {
						}
					}
				});
				
			} catch (Throwable e) {
				dummyFuture.triggerError(e);
			}

			return dummyFuture;
		} else {
			dummyFuture.setResult(null);
			
			return dummyFuture;
		}
	}

	private String getStoredRefreshToken() {
		SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);	
		return preferences.getString(REFRESH_TOKEN_KEY, null);
	}
	
	private void storeRefreshToken(String refreshToken) {
		Editor editor = this.getPreferences(Context.MODE_PRIVATE).edit();	
		editor.putString(REFRESH_TOKEN_KEY, refreshToken);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_record_and_upload_file, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		//The only menu item is "clear refresh token"
		storeRefreshToken(null);
		return super.onMenuItemSelected(featureId, item);
	}

	private void createAndShowDialog(Throwable error) {
		StringBuilder sb = new StringBuilder();
		
		while (error != null) {
			sb.append(error.toString() + "\n");
			error = error.getCause();
		}
		
		createAndShowDialog(sb.toString(), "Error");	
	}
	
	private void createAndShowDialog(final String content, final String title) {
		final Activity that = this;
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(that);

				builder.setMessage(content);
				builder.setTitle(title);
				builder.create().show();
			}
		});
	}
	
	class DefaultErrorCallback implements ErrorCallback {

		@Override
		public void onError(Throwable error) {
			error.printStackTrace();
			createAndShowDialog(error);
		}
	}
}
