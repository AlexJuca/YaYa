package com.AlexandreJuca.ribbit;

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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {
	protected EditText mUserName;
	protected EditText mPassword;
	protected EditText mEmail;
	protected Button mButtonSignUp;
	protected Button mCancelSignUp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_sign_up);
		ActionBar action = getActionBar();
		action.hide();
		mUserName = (EditText) findViewById(R.id.username_signup);
		mPassword = (EditText) findViewById(R.id.password_signup);
		mEmail = (EditText) findViewById(R.id.email_signup);
		mButtonSignUp = (Button) findViewById(R.id.signup_btn);
		mCancelSignUp = (Button) findViewById(R.id.cancel_signup_btn);
		
		
		final String mSignUpErrorMsg = "Fields cannot be left blank";
		
		mCancelSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				finish();
			}
		});
		
		mButtonSignUp.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				setProgressBarIndeterminateVisibility(true);
				verifySignUpFields(mSignUpErrorMsg);
			}

			
		});
	}
	
			
	
	private void verifySignUpFields(final String mSignUpErrorMsg) {
		String username = mUserName.getText().toString();
		String password = mPassword.getText().toString();
		String email = mEmail.getText().toString();
		//Trim
		username = username.trim();
		password = password.trim();
		email = email.trim();
		//Check if Fields are empty
		if (username.isEmpty() || password.isEmpty() || email.isEmpty())
		{
			setProgressBarIndeterminateVisibility(false);
			alertOnSignUpError(mSignUpErrorMsg);
		}
		else
		{
			ParseUser newUser = new ParseUser();
			newUser.setUsername(username);
			newUser.setPassword(password);
			newUser.setEmail(email);
			newUser.signUpInBackground( new SignUpCallback() {
				
				@Override
				public void done(ParseException e) {
					
					if( e == null)
					{
						setProgressBarIndeterminateVisibility(false);
						alertOnSignUpError("Error");
					}
					else
					{
						setProgressBarIndeterminateVisibility(false);
						Intent signUpIntent = new Intent(SignUpActivity.this, MainActivity.class);
						signUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(signUpIntent);
					}
					
				}
			});
			
		}
		
	}
	
	private void alertOnSignUpError(final String mSignUpErrorMsg) {
		AlertDialog.Builder dialog  =  new Builder(SignUpActivity.this);
		dialog.setTitle("Empty Fields").setMessage(mSignUpErrorMsg).setPositiveButton(android.R.string.ok, null);
		dialog.show();
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
