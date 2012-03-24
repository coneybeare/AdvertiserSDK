package com.vungle.sdk.download.attribution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


/**
 * Purpose of this class is to handle different responses of connection
 *
 */
class VungleConnectionHandler {

	private final Context context;
	
	private VungleDownloadAsyncTask downloadAsyncTask = null;
	private VungleEventAsyncTask eventAsyncTask = null;
	public static final String TAG_VUNGLE = "Vungle";

	public VungleConnectionHandler(Context ctx) {
		this.context   = ctx;
	}
	
	/**
	 * This method starts an AsyncTask to track application installation in background 
	 */
	public void trackInstallationAsync() {
		((Activity) this.context).runOnUiThread( new Runnable() {

			@Override
			public void run() {
				VungleConnectionHandler.this.downloadAsyncTask = new VungleDownloadAsyncTask();
				VungleConnectionHandler.this.downloadAsyncTask.execute();
			}
		});
	}
	
	public void postEventAsync(final String event) {
		if (VungleUtil.isStringEmptyOrNull(event)) {
			return;
		}
		
		((Activity) this.context).runOnUiThread( new Runnable() {

			@Override
			public void run() {
				VungleConnectionHandler.this.eventAsyncTask = new VungleEventAsyncTask();
				VungleConnectionHandler.this.eventAsyncTask.execute(event);
			}
		});
	}

	/**
	 * This method send a request to Vungle server to track Installation of application,
	 * On success response it'll save a flag as true in preferences.
	 * On success failure it'll save false.   
	 */
	private void trackInstallation() {
		String url = buildTrackInstallationRequest();
		if( !VungleUtil.isStringEmptyOrNull(url)) {
			ArrayList<String> responseArray = httpGetRequest( url );
			boolean prefValue = false; 
			if( responseArray != null && !responseArray.isEmpty() ) {
				if( "OK".equals( responseArray.get(0) ) ) {
					prefValue = true;
				}
			} 
			VungleUtil.setInSharedPreference(this.context, Vungle.INSTALLATION_PREF_FILE, Vungle.IS_VG_APP_INSTALLED, prefValue);
		}
	}
	
	private void submitEvent(String[] params) {
		String url = buildEventRequest(params[0]);
		if( !VungleUtil.isStringEmptyOrNull(url)) {
			httpGetRequest( url );
		}
	}
	
	/**
	 * This method build URL by appending 
	 * 	1- Android_Id
	 *  2- Package Name
	 *  3- Mac address
	 * The Final URL will be http://api.vungle.com/api/v1/new?isu=<Android_Id>&app_is=<PackageName>&ma=<MAC Address>
	 * 
	 * @return URL  
	 */
	private String buildTrackInstallationRequest() {
		String apiUrlNew = "http://api.vungle.com/api/v1/new";
		StringBuilder builder = new StringBuilder( apiUrlNew );
		builder.append("?isu=");
		builder.append(VungleUtil.getAndroidId(this.context));
		builder.append("&app_id=");
		builder.append(this.context.getPackageName());
		builder.append("&ma=");
		builder.append(VungleUtil.getMacAddress(this.context));
		String serial = VungleUtil.getSerialNumber();
		if (!VungleUtil.isStringEmptyOrNull(serial)) {
			builder.append("&serial=");
			builder.append(serial);	
		}
		
		return builder.toString();
	}
	
	private String buildEventRequest(String event) {
		String apiUrlNew = "http://api.vungle.com/api/v1/event";
		StringBuilder builder = new StringBuilder( apiUrlNew );
		builder.append("?isu=");
		builder.append(VungleUtil.getAndroidId(this.context));
		builder.append("&app_id=");
		builder.append(this.context.getPackageName());
		builder.append("&ma=");
		builder.append(VungleUtil.getMacAddress(this.context));
		String serial = VungleUtil.getSerialNumber();
		if (!VungleUtil.isStringEmptyOrNull(serial)) {
			builder.append("&serial=");
			builder.append(serial);	
		}
		
		builder.append("&str=");
		builder.append(event);
		
		return builder.toString();
	}
	
	/**
	 * This method sends a request to passed URL using GET method. 
	 * 
	 * @param requestUrl Complete URL that needs to be hit
	 * @return ArrayList<String> This is an array list in which response message is stored on 0 index and data part is on index 1
	 */
	private static ArrayList<String> httpGetRequest(String requestUrl) {
		ArrayList<String> responseArray = new ArrayList<String>();
		HttpURLConnection connection = null;
		URL url = null;
		try {
			url = new URL(requestUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setUseCaches(false);
	        connection.setConnectTimeout(30000);
 
	        connection.connect();

	        Log.d(VungleConnectionHandler.TAG_VUNGLE, "GET Response Code : " + connection.getResponseCode());
        	
	        responseArray.add(0, connection.getResponseMessage() );
	        if (HttpURLConnection.HTTP_OK == connection.getResponseCode() ) {
	        	InputStream is = connection.getInputStream();
	        	StringBuilder response = new StringBuilder();
	        	char[] buffer = new char[1024];
	        	try {
	        		BufferedReader reader = new BufferedReader(new InputStreamReader(is, HTTP.UTF_8));
	        		while (reader.read(buffer) != -1) {
	        			response.append(buffer);
	        		}
	        		responseArray.add(1, response.toString());
	        		Log.d(VungleConnectionHandler.TAG_VUNGLE, "GET Response : " + response.toString());
	        	} finally {
	        		if( is != null ) {
	        			is.close();
	        		}	        		
	        	}
	        }
		} catch (MalformedURLException malFormedExp) {
            malFormedExp.printStackTrace();
            
        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        } 
       
        return responseArray;
	}
	
	
	/**
	 * This is an AsyncTask. With the help of this class we perform network operations asynchronously 
	 */
	private class VungleDownloadAsyncTask extends AsyncTask<Void, Void, Void> {

		
		private Handler handler = null;
		final long DEFAULT_RETRY_THRESHOLD = 30000L; // 30 Seconds
		
		@Override
		protected void onPreExecute() {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> onPreExecute()" );
			if(this.DEFAULT_RETRY_THRESHOLD != 0 ) {
				if( this.handler != null) {
					this.handler.removeCallbacks( this.killAsyncTaskRunnable );
					this.handler = null;
				}
				this.handler = new Handler(VungleConnectionHandler.this.context.getMainLooper());
				this.handler.postDelayed( this.killAsyncTaskRunnable, this.DEFAULT_RETRY_THRESHOLD );
				Log.d(VungleConnectionHandler.TAG_VUNGLE, "AsyncTask Kill Time " + this.DEFAULT_RETRY_THRESHOLD );
				Log.d(VungleConnectionHandler.TAG_VUNGLE, "this.handler set at : " + new Date(System.currentTimeMillis()).getSeconds() );
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> doInBackground(Void... params)" );
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "Tracking Installations");
			trackInstallation();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> onPostExecute(Void result)" );
			VungleConnectionHandler.this.downloadAsyncTask = null;
			if( this.DEFAULT_RETRY_THRESHOLD != 0 ) {
				if( this.handler != null) {
					this.handler.removeCallbacks( this.killAsyncTaskRunnable);
					this.handler = null;
				}
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> onCancelled()" );
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "onCancelled() call at : " + new Date(System.currentTimeMillis()).getSeconds() );
			VungleConnectionHandler.this.downloadAsyncTask = null;
			this.handler = null;
		}
		
		
		/**
		 * This Runnable runs after server configured time to:
		 *  1- Cancel running AsyncTask. 
		 *  2- Stop all ongoing network operations.
		 */
		private final Runnable killAsyncTaskRunnable = new Runnable() {
			
			@Override
			public void run() {
				if( VungleConnectionHandler.this.downloadAsyncTask != null && !VungleConnectionHandler.this.downloadAsyncTask.isCancelled()) {
					Log.d(VungleConnectionHandler.TAG_VUNGLE, "Runnable call at : " + new Date(System.currentTimeMillis()).getSeconds() );
					VungleConnectionHandler.this.downloadAsyncTask.cancel(true);
				}
			}
		};
	}
	
	/**
	 * This is an AsyncTask. With the help of this class we perform network operations asynchronously 
	 */
	private class VungleEventAsyncTask extends AsyncTask<String, Void, Void> {

		private Handler handler = null;
		final long DEFAULT_RETRY_THRESHOLD = 30000L; // 30 Seconds
		
		@Override
		protected void onPreExecute() {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> onPreExecute()" );
			if(this.DEFAULT_RETRY_THRESHOLD != 0 ) {
				if( this.handler != null) {
					this.handler.removeCallbacks( this.killAsyncTaskRunnable );
					this.handler = null;
				}
				this.handler = new Handler(VungleConnectionHandler.this.context.getMainLooper());
				this.handler.postDelayed( this.killAsyncTaskRunnable, this.DEFAULT_RETRY_THRESHOLD );
				Log.d(VungleConnectionHandler.TAG_VUNGLE, "AsyncTask Kill Time " + this.DEFAULT_RETRY_THRESHOLD );
				Log.d(VungleConnectionHandler.TAG_VUNGLE, "this.handler set at : " + new Date(System.currentTimeMillis()).getSeconds() );
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> doInBackground(String... params)" );
			submitEvent(params);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> onPostExecute(Void result)" );
			VungleConnectionHandler.this.eventAsyncTask = null;
			if( this.DEFAULT_RETRY_THRESHOLD != 0 ) {
				if( this.handler != null) {
					this.handler.removeCallbacks( this.killAsyncTaskRunnable);
					this.handler = null;
				}
			}
		}

		@Override
		protected void onCancelled() {
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "----> onCancelled()" );
			Log.d(VungleConnectionHandler.TAG_VUNGLE, "onCancelled() call at : " + new Date(System.currentTimeMillis()).getSeconds() );
			VungleConnectionHandler.this.eventAsyncTask = null;
			this.handler = null;
		}
		
		
		/**
		 * This Runnable runs after server configured time to:
		 *  1- Cancel running AsyncTask. 
		 *  2- Stop all ongoing network operations.
		 */
		private final Runnable killAsyncTaskRunnable = new Runnable() {
			
			@Override
			public void run() {
				if( VungleConnectionHandler.this.eventAsyncTask != null && !VungleConnectionHandler.this.eventAsyncTask.isCancelled()) {
					Log.d(VungleConnectionHandler.TAG_VUNGLE, "Runnable call at : " + new Date(System.currentTimeMillis()).getSeconds() );
					VungleConnectionHandler.this.eventAsyncTask.cancel(true);
				}
			}
		};
	}
}
