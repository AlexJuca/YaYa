package com.AlexandreJuca.ribbit;

import android.app.Application;

import com.AlexandreJuca.ribbit.ui.MainActivity;
import com.AlexandreJuca.ribbit.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class RibbitApplication extends Application{

	public void onCreate() 
	{
		  Parse.initialize(this, "WL0y2AcL1l0SyyRRv7cHJNky74HZHUIyqO10CMhs", "yv0vZ5hKmNTzzMf8JLdC6n8Ux0Bo5dp1gP2ycL31");	
		  PushService.setDefaultPushCallback(this, MainActivity.class);
		  ParseInstallation.getCurrentInstallation().saveInBackground();
	}
	
	public static void updateParseIntsallatiion(ParseUser user)
	{
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID,  user.getObjectId());
		installation.saveInBackground();
	}
	
	
}
