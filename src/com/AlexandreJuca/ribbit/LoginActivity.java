package com.AlexandreJuca.ribbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {
	protected TextView mSignUp;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSignUp = (TextView) findViewById(R.id.signup_textView);
		mSignUp.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				signUp();
		
			}
		});
		
		setContentView(R.layout.activity_login);
	}
	
	private void signUp()
	{
		Intent signupIntent = new Intent(this, SignUpActivity.class);
		startActivity(signupIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
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
