package com.AlexandreJuca.ribbit;

import android.app.ListActivity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkOracle extends ListActivity {
	/*The primary responsibilities of this class are to:
	*
	*	Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
	*	
	*/
	
	public  boolean networkIsAvailable() 
	{
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected())
		{
			isAvailable = true;
		}
		return isAvailable;
	}

	
	
}
