/* Author: Alexandre Antonio Juca
 * Date:  25 July 2014
 * This class contains AlertMessages that can be called statically from other classes
 */

package com.AlexandreJuca.ribbit;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.Settings;

public class AlertMessages extends Activity {
	
	//Constants
	public static final int ERROR_TYPE_LOGIN_FAILURE = 0x100;
	public static final int ERROR_TYPE_SIGNUP_FAILURE = 0x200;
	public static final int ERROR_TYPE_ERROR = 0x300;
	public static final int ERROR_TYPE_NO_NETWORK = 0x400;
	public static final int ERROR_TYPE_NO_DISK_SPACE = 0x500;
	
	public static final String mTag = AlertMessages.class.getSimpleName();
	
	
	
	
	public static void alertOnError(Context context, String mSignUpErrorMsg, int Error_TYPE)
	{	
		
		
		
		//TODO: Refactor
		if(Error_TYPE == 0x100)
		{
			AlertDialog.Builder builder  =  new Builder(context);
			builder.setTitle(R.string.alert_signup_failed).setMessage(mSignUpErrorMsg).setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		if (Error_TYPE == 0x200)
		{
			AlertDialog.Builder builder  =  new Builder(context);
			builder.setTitle(R.string.alert_login_failed).setMessage(mSignUpErrorMsg).setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
		else if (Error_TYPE == 0x300)
		{
			AlertDialog.Builder builder  =  new Builder(context);
			builder.setTitle(R.string.alertdialog_error_type_error).setMessage(mSignUpErrorMsg).setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		if (Error_TYPE == 0x400)
		{
			AlertDialog.Builder builder  =  new Builder(context);
			builder.setTitle(R.string.alert_no_network).setMessage(mSignUpErrorMsg).setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		if (Error_TYPE == 0x500)
		{
			AlertDialog.Builder builder  =  new Builder(context);
			builder.setTitle(R.string.alert_no_disk_space).setMessage(mSignUpErrorMsg).setPositiveButton("Free space", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					Intent freeSpaceIntent = new Intent(Settings.ACTION_SETTINGS);
					freeSpaceIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
					freeSpaceIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					freeSpaceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					AlertMessages as = new AlertMessages();
					as.startActivity(freeSpaceIntent);
					//TODO: Fix Error when no space is available and user tries to take photo or images ?? Remember to set handleCameraActions to return null
					
				}
			}).setNegativeButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		else
		{
			Exception exception = new Exception("Error "+Error_TYPE+" does not exist: ");
			exception.printStackTrace();
		}
				
	}
	
}
