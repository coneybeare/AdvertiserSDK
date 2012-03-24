package com.vungle.sdk.download.attribution;


import android.content.Context;

public final class Vungle {

	static final String INSTALLATION_PREF_FILE = "INSTALLATION";
	static final String IS_VG_APP_INSTALLED = "isVgAppInstalled"; 
	/**
	 * This method send a request on Vungle server to track app installation
	 * when app runs first time. This will only submit one network call. It saves a flag in
	 * shared preference when it successfully updates the installation status on
	 * Vungle's server. This method call will check preference value to check
	 * whether this call already succeeded, if not then it reports the install
	 * via the Vungle API.
	 * 
	 * @param context
	 *            Host application context.
	 */
	public static void init(Context context) {
		if (!VungleUtil.getFromSharedPreference(context,
				Vungle.INSTALLATION_PREF_FILE,
				Vungle.IS_VG_APP_INSTALLED, false)) {
			if (VungleUtil.isNetworkAvailable(context)) {
				new VungleConnectionHandler(context)
						.trackInstallationAsync();
			} else {
				//  network not available
			}
		}
	}
	
	public static void event(Context context, String event) {
		if (VungleUtil.isNetworkAvailable(context)) {
			new VungleConnectionHandler(context).postEventAsync(event);
		}
	}
}