package com.AlexandreJuca.ribbit;

import android.app.Application;

import com.parse.Parse;

public class RibbitApplication extends Application{

	public void onCreate() 
	{
		  Parse.initialize(this, "WL0y2AcL1l0SyyRRv7cHJNky74HZHUIyqO10CMhs", "yv0vZ5hKmNTzzMf8JLdC6n8Ux0Bo5dp1gP2ycL31");		  
	}
	
	
	
}
