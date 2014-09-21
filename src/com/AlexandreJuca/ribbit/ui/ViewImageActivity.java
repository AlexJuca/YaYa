package com.AlexandreJuca.ribbit.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.AlexandreJuca.ribbit.R;
import com.AlexandreJuca.ribbit.utils.ParseConstants;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends Activity {
	
	protected int mDelay = 10*1000; //Miliseconds
	protected ImageView mImageView;
	protected TextView text;
	String senderName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_image);
		ActionBar action = getActionBar();
		action.hide();
	
		
		mImageView = (ImageView) findViewById(R.id.imageView);
		text = (TextView) findViewById(R.id.sender);
		
		Intent intent = getIntent();
		Uri imageUri = intent.getData();
		senderName = "Sender: "+ intent.getStringExtra(ParseConstants.KEY_SENDER_NAME);
		text.setText(senderName);
		Picasso.with(this).load(imageUri).into(mImageView);
		/*Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				finish();
				
			}
		}, mDelay);*/
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_image, menu);
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
