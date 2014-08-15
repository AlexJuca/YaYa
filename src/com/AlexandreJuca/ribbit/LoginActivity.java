package com.AlexandreJuca.ribbit;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	protected TextView mSignUp;
	protected EditText mUserName;
	protected EditText mPassword;
	protected Button mButtonLogin;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		ActionBar action = getActionBar();
		action.hide();
		
		mSignUp = (TextView) findViewById(R.id.signup_textView);
		mUserName = (EditText) findViewById(R.id.userNameField);
		mPassword = (EditText) findViewById(R.id.userPasswordField);
		mButtonLogin = (Button) findViewById(R.id.loginButton);
		
		
		mButtonLogin.setOnClickListener( new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				setProgressBarIndeterminateVisibility(true);
				verifyLogInFields();
			
			}
		});
		
		
		mSignUp.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				signUp();
				
			}
		});
	}
		
		private void verifyLogInFields() 
		{
				String username = mUserName.getText().toString();
				String password = mPassword.getText().toString();
				//Trim
				setProgressBarIndeterminateVisibility(true);
				//Check if Fields are empty
				if (username.isEmpty() || password.isEmpty())
				{
					setProgressBarIndeterminateVisibility(false);
					alertOnlogInError(getString(R.string.login_validate_error_msg));
				}
				else
				{
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						
						@Override
						public void done(ParseUser user, ParseException e) {
							
							if (e == null)
							{
								setProgressBarIndeterminateVisibility(false);
								login();
							}
							else
							{
								setProgressBarIndeterminateVisibility(false);
								alertOnlogInError(getString(R.string.failed_login));
								
							}
							
						}

						private void login() {
							
							Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
							loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(loginIntent);
							
						}
					});
					
					
				}
	}
			
			
	private void alertOnlogInError(String mSignUpErrorMsg)
	{
				AlertDialog.Builder builder  =  new Builder(this);
				builder.setTitle(R.string.alert_login_failed).setMessage(mSignUpErrorMsg).setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
	}
			
	private void signUp()
	{
		Intent signupIntent = new Intent(this, SignUpActivity.class);
		startActivity(signupIntent);
	}



}
