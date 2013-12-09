package com.example.office365_app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.microsoft.office365.sdk.ErrorCallback;
import com.microsoft.office365.sdk.OfficeFuture;
import com.microsoft.office365.sdk.SPList;
import com.microsoft.office365.sdk.SPListItem;
import com.microsoft.office365.sdk.SharepointClient;
import com.microsoft.office365.sdk.http.BasicAuthenticationCredentials;

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
	
	/* To enable AAD, uncomment this
	
	AuthenticationContext mContext;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mContext.onActivityResult(requestCode, resultCode, data);
	}
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		BasicAuthenticationCredentials credentials = new BasicAuthenticationCredentials("lagashsystems\\barrya", "Password11");
		mClient = new SharepointClient("http://shpint:5050", credentials);
		
		((Button)findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				clickButton1();
			}			
		});
	}
	
	private void clickButton1() {
		
		try {
			OfficeFuture<Void> uploadFuture = mClient.uploadFile("ListaDoc", "textFile.txt", "Hello world!".getBytes());
			uploadFuture.onError(new DefaultErrorCallback());
			
			uploadFuture.get(); //wait for completion
			
			createAndShowDialog("File uploaded!", "Success!");
			

			OfficeFuture<SPList> listFuture = mClient.getList("ListaComun");
			listFuture.onError(new DefaultErrorCallback());
			
			SPList list = listFuture.get();
			
			String message = String.format("List received! Name: %s, ItemType: %s", list.getTitle(), list.getListItemEntityTypeFullName());
			createAndShowDialog(message, "Success!");
			
			OfficeFuture<List<SPListItem>> itemsFuture = mClient.getListItems(list);
			
			itemsFuture.onError(new DefaultErrorCallback());
			
			List<SPListItem> items = itemsFuture.get();
			
			createAndShowDialog(String.format("Received %s items", items.size()), "Success!");
			
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("Title", "Hello Android!");
			values.put("NumberColumn", 42);
			
			OfficeFuture<Void> insertFuture = mClient.insertListItem(list, values);
			
			insertFuture.get(); // wait for completion.
			
			createAndShowDialog("Inserted!", "Success!");
		} catch (Throwable e) {
			createAndShowDialog(e.toString(), "Error");
		}
		
	}
	
	class DefaultErrorCallback implements ErrorCallback {

		@Override
		public void onError(Throwable error) {
			createAndShowDialog(getExceptionContent(error), "Error");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Creates a dialog and shows it
	 * 
	 * @param message
	 *            The dialog message
	 * @param title
	 *            The dialog title
	 */
	private void createAndShowDialog(final String message, final String title) {
		final Activity that = this;
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(that);

				builder.setMessage(message);
				builder.setTitle(title);
				builder.create().show();
			}
		});
	}

}
