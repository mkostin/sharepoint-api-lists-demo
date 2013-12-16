package com.example.office365_app;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.microsoft.office365.sdk.Action;
import com.microsoft.office365.sdk.Credentials;
import com.microsoft.office365.sdk.ErrorCallback;
import com.microsoft.office365.sdk.OfficeFuture;
import com.microsoft.office365.sdk.SPList;
import com.microsoft.office365.sdk.SPListField;
import com.microsoft.office365.sdk.SharepointClient;
import com.microsoft.office365.sdk.SharepointOnlineCredentials;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class CreateRecordAndUploadFileActivity extends Activity {

	final static int CAMARA_REQUEST_CODE = 1000;
	
	final static String SHAREPOINT_SITE = "https://lagashsystems365.sharepoint.com/sites/Argentina/Produccion/";
	final static String CLIENT_ID = "c2c8ce1c-2a18-4ea6-8034-96935c451dd6";
	final static String REDIRECT_URL = "https://www.lagash.com/login";
	final static String OFFICE_365_DOMAIN = "lagash.com";
	final static String CLIENT_SECRET = "OCT+qIaOXMEmaQ5slKM7dd+24JcLUimWpSiGlqUHYUg=";
	
	private Credentials mCredentials = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_record_and_upload_file);
		
		setTitle("Sharepoint Online Demo");
		
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
	}

	protected void loadValues() {
		final String text = ((EditText)findViewById(R.id.txtTitle)).getText().toString();
		String txtNumber = ((EditText)findViewById(R.id.txtNumber)).getText().toString();
		int n = 0;
		
		try {
			n = Integer.parseInt(txtNumber);
		} catch (Throwable error) {
		}
		
		final int number = n;
		
		
		OfficeFuture<Void> credentialsFuture = loadCredentials();
		credentialsFuture.onError(new DefaultErrorCallback());
		credentialsFuture.done(new Action<Void>() {
			
			@Override
			public void run(Void obj) throws Exception {
				
				try {
					SharepointClient client = new SharepointClient(SHAREPOINT_SITE, mCredentials);
	
					SPList list = client.getList("RegularList").get();
					
					List<SPListField> fields = client.getListFields("RegularList").get();
					
					String numberColumnFieldName = null;
					for (SPListField field : fields) {
						if (field.getTitle().equals("NumberColumn")) {
							numberColumnFieldName = field.getEntityPropertyName();
						}
					}
					
					Map<String, Object> values = new HashMap<String, Object>();
					values.put("Title", text);
					values.put(numberColumnFieldName, number); // EntityPropertyName[NumberColumn]
					
					client.insertListItem(list, values).get();
					
					createAndShowDialog("Values created", "Success!");
				
				} catch (Throwable e) {
					createAndShowDialog(e);
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == CAMARA_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				final byte[] byteArray = stream.toByteArray();
				
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						OfficeFuture<Void> credentialsFuture = loadCredentials();
						credentialsFuture.onError(new DefaultErrorCallback());
						credentialsFuture.done(new Action<Void>() {
							
							@Override
							public void run(Void obj) throws Exception {
								
								try {
									SharepointClient client = new SharepointClient(SHAREPOINT_SITE, mCredentials);
									String fileName = UUID.randomUUID().toString();
					
									client.uploadFile("DocLib", fileName + ".jpg", byteArray).get();
									
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
		}
	}
	
	private void uploadPhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	
	    // start the image capture Intent
	    startActivityForResult(intent, CAMARA_REQUEST_CODE);
		
		
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
							OfficeFuture<SharepointOnlineCredentials> credentialsFuture = SharepointOnlineCredentials.requestCredentials(that, SHAREPOINT_SITE, CLIENT_ID, REDIRECT_URL, OFFICE_365_DOMAIN, CLIENT_SECRET);
							
							credentialsFuture.onError(new DefaultErrorCallback());
							credentialsFuture.done(new Action<SharepointOnlineCredentials>() {
								
								@Override
								public void run(SharepointOnlineCredentials credentials) throws Exception {
									mCredentials = credentials;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_record_and_upload_file, menu);
		return true;
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
