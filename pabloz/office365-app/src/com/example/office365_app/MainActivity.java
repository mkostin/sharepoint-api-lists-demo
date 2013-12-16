package com.example.office365_app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.office365.sdk.Action;
import com.microsoft.office365.sdk.ErrorCallback;
import com.microsoft.office365.sdk.OfficeFuture;
import com.microsoft.office365.sdk.SPList;
import com.microsoft.office365.sdk.SPListField;
import com.microsoft.office365.sdk.SPListItem;
import com.microsoft.office365.sdk.SharepointClient;
import com.microsoft.office365.sdk.SharepointOnlineCredentials;

public class MainActivity extends Activity {

	SharepointClient mClient;

	private String getExceptionContent(Throwable t) {
		StringBuilder sb = new StringBuilder();
		
		while (t != null) {
			sb.append(t.toString());
			sb.append(": " + t.getMessage());
			sb.append("\n");
			t = t.getCause();
		}
		
		return sb.toString();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((Button)findViewById(R.id.btnLoadValues)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickButton1();
			}			
		});
	}
	
	private void clickButton1() {
		
		try {
			final String sharepointSite = "https://lagashsystems365.sharepoint.com/sites/Argentina/Produccion/";
			String clientId = "c2c8ce1c-2a18-4ea6-8034-96935c451dd6";
			String redirectUrl = "https://www.lagash.com/login";
			String office365Domain = "lagash.com";
			String clientSecret = "OCT+qIaOXMEmaQ5slKM7dd+24JcLUimWpSiGlqUHYUg=";
			
			OfficeFuture<SharepointOnlineCredentials> credentialsFuture = SharepointOnlineCredentials.requestCredentials(this, sharepointSite, clientId, redirectUrl, office365Domain, clientSecret);
			
			credentialsFuture.onError(new DefaultErrorCallback());
			credentialsFuture.done(new Action<SharepointOnlineCredentials>() {
				
				@Override
				public void run(SharepointOnlineCredentials credentials) throws Exception {
					//BasicAuthenticationCredentials credentials = new BasicAuthenticationCredentials("lagashsystems\\barrya", "Password11");
					mClient = new SharepointClient(sharepointSite, credentials);

					OfficeFuture<SPList> listFuture = mClient.getList("RegularList");
					
					SPList list = listFuture.get();
					
					OfficeFuture<String> titleFuture = mClient.getWebTitle();
					String title = titleFuture.get(); //wait for completion
					log("Title: " + title, "Success!");
					
					OfficeFuture<Void> uploadFuture = mClient.uploadFile("DocLib", "textFile.txt", "Hello world!".getBytes());
					
					uploadFuture.get(); //wait for completion
					
					log("File uploaded!", "Success!");
										
					String message = String.format("List received! Name: %s, ItemType: %s", list.getTitle(), list.getListItemEntityTypeFullName());
					log(message, "Success!");
					
					OfficeFuture<List<SPListItem>> itemsFuture = mClient.getListItems(list);
					
					List<SPListItem> items = itemsFuture.get();
					
					log(String.format("Received %s items", items.size()), "Success!");
					
					OfficeFuture<List<SPListField>> fieldsFuture = mClient.getListFields("RegularList");
					
					List<SPListField> fields = fieldsFuture.get();
					
					String numberColumnFieldName = "";
					
					for (SPListField field : fields) {
						if (field.getTitle().equals("NumberColumn")) {
							numberColumnFieldName = field.getEntityPropertyName();
						}
					}
					
					Map<String, Object> values = new HashMap<String, Object>();
					values.put("Title", "Hello Android!");
					values.put(numberColumnFieldName, 42); // EntityPropertyName[NumberColumn]
					
					OfficeFuture<Void> insertFuture = mClient.insertListItem(list, values);
					
					insertFuture.get(); // wait for completion.
					
					log("Inserted!", "Success!");
				}
			});
			
			
		} catch (Throwable e) {
			log(e.toString(), "Error");
		}
		
	}
	
	class DefaultErrorCallback implements ErrorCallback {

		@Override
		public void onError(Throwable error) {
			error.printStackTrace();
			log(getExceptionContent(error), "Error");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void log(final String message, final String title) {
		//final Activity that = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				EditText log = (EditText)findViewById(R.id.txtLog);
				
				log.append(title + ": " + message + "\n");
				
				/*
				AlertDialog.Builder builder = new AlertDialog.Builder(that);

				builder.setMessage(message);
				builder.setTitle(title);
				builder.create().show();
				*/
			}
		});
	}

}
