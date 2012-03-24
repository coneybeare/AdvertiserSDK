package com.vungle.sdk.download.attribution;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

/**
 * This class is destined to hold utility methods.
 */
class VungleUtil {

	/**
	 * This method can be used to find if string is null or empty?
	 *
	 * @param 	checkString String to be verified
	 * @return 	true if String is Null or Empty, false otherwise
	 */
	public static boolean isStringEmptyOrNull(String checkString) {
		return !(checkString != null && !"".equals(checkString));
	}

	/**
	 * @param context Context of application
	 * @return 	MAC address
	 */
	public static  String getMacAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.getConnectionInfo().getMacAddress();
	}

	/**
	 * Retrieve ANDROID_ID.
	 *
	 * @param context Context of application
	 * @return This will return unique identifier for device.
	 */
	public static  String getAndroidId( Context context ) {
		return	Settings.Secure.getString( context.getContentResolver(), Settings.Secure.ANDROID_ID);
	}
	
	/**
	 * This method retrieve Serial number of the device,
	 * Only if android.os.Build.VERSION.SDK is greater or equal android API Level 9
	 *    
	 * @return 	Serial Number address
	 */
	public static  String getSerialNumber( ) {
		String serialNum = "";
		if( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD ) {
			serialNum = Build.SERIAL;
		}
		return serialNum;
	}
	
	/**
	 *
	 * Check whether network is connected or not
	 *
	 * @param context Context of application
	 * @return true if connected false otherwise
	 */
	public static  boolean isNetworkAvailable( Context context ) {

		boolean connected = false;
		ConnectivityManager connectivityManager = null;
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
		if (netInfo != null) {
			connected = netInfo.isConnected();
		}
		return connected;
	}

	/**
	 * @param context Host application context
	 * @param prefFileName Preference file name
	 * @param attributeName Attribute Name
	 * @param attributeValue Attribute Value
	 */
	public static void setInSharedPreference(Context context, String prefFileName, String attributeName, boolean attributeValue) {
		SharedPreferences myPref = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myPref.edit();

		if (editor != null) {
			editor.putBoolean(attributeName, attributeValue);
			editor.commit();
		}
	}
	
	/**
	 * @param context Context of Host application
     * @param prefFileName Preference file name
	 * @param attributeName Attribute name
     * @param defaultValue Default value of attribute
	 * @return boolean Value
	 */
	public static boolean getFromSharedPreference(Context context, String prefFileName, String attributeName, boolean defaultValue) {
		SharedPreferences myPref = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
		return myPref.getBoolean(attributeName, defaultValue);
	}

}
