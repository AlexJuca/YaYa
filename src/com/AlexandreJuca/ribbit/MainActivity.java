package com.AlexandreJuca.ribbit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	protected View main;
	protected final String mTAG = MainActivity.class.getSimpleName();
	protected Uri mMediaUri;
	
	protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener()
	{
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			handleCameraActions(which);
			
	}
		
	/* Handle camera events */
	private void handleCameraActions(int which) 
	{
			Intent cameraIntent;
			switch(which)
			{
				
				case 0:
					cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mMediaUri = getOutPutMediaUri(RequestConstants.MEDIA_TYPE_IMAGE);
					if(mMediaUri == null)
					{
						AlertMessages.alertOnError(MainActivity.this, getString(R.string.error_external_storage), AlertMessages.ERROR_TYPE_NO_DISK_SPACE);
					}
					else
					{
						//AlertMessages.alertOnError(MainActivity.this, getString(R.string.error_external_storage), AlertMessages.ERROR_TYPE_ERROR);
						cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
						//Set activity as new task to avoid reloading the MainActivity page and crashing the app
						//cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivityForResult(cameraIntent, RequestConstants.MEDIA_TYPE_IMAGE);
					}
					
					break;
				case 1:
					cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
					cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
					cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
					mMediaUri = getOutPutMediaUri(RequestConstants.MEDIA_TYPE_VIDEO);
					if(mMediaUri == null)
					{
						AlertMessages.alertOnError(MainActivity.this, getString(R.string.error_external_storage), AlertMessages.ERROR_TYPE_NO_DISK_SPACE);
					}
					else
					{
						//Set activity as new task to avoid reloading the MainActivity page and crashing the application
						cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
						cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivityForResult(cameraIntent, RequestConstants.MEDIA_TYPE_VIDEO);
					}
					break;
				case 2:
						Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
						choosePhotoIntent.setType("image/*");
						startActivityForResult(choosePhotoIntent, RequestConstants.CHOOSE_PHOTO_REQUEST);
						break;
				case 3:
						Toast.makeText(MainActivity.this, "Hey! Video should be less then 10MB in size.", Toast.LENGTH_LONG).show();
						Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
						chooseVideoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 10);
						chooseVideoIntent.setType("video/*");
						startActivityForResult(chooseVideoIntent, RequestConstants.CHOOSE_VIDEO_REQUEST);
						break;
				
			}
		}

		
	};
	
	protected boolean isExternalMemoryAvailable()
	{
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		
		}
		else
		{
			AlertMessages.alertOnError(MainActivity.this, getString(R.string.error_external_storage), AlertMessages.ERROR_TYPE_NO_DISK_SPACE);
			return false;
		}
	}
	
	protected Uri getOutPutMediaUri(int mediaType) {
		
		if(isExternalMemoryAvailable())
		{			
					
					String appName = MainActivity.this.getString(R.string.app_name);
					//File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					//		appName);
					File mediaVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName + "/Videos/");
					File mediaPicturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName + "/Pictures/");
					//Check if file exists and if not create them
					if(! mediaVideoDir.exists())
					{
						if(! mediaVideoDir.mkdirs())
						{
							Log.e(mTAG, "Failed to create video directory");
							return null;
						}
					}
					
					if(! mediaPicturesDir.exists())
					{
						if(! mediaPicturesDir.mkdirs())
						{
							Log.e(mTAG, "Failed to create video directory");
							return null;
						}
					}
					
					File mediaFile;
					//Get time stamp for our filename
					Date now = new Date();
					String timpestamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(now);
					//Get full path to store pictures and videos respectively
					String picturesPath = mediaPicturesDir.getPath()+ File.separator;
					String videoPath = mediaVideoDir.getPath() + File.separator;
					
					if (mediaType == RequestConstants.MEDIA_TYPE_IMAGE)
					{
						mediaFile = new File(picturesPath + appName+"_IMG_" + timpestamp + ".jpg");
					}
					else if (mediaType == RequestConstants.MEDIA_TYPE_VIDEO)
					{
						mediaFile = new File(videoPath + appName+"_" + timpestamp + ".mp4");;
					}
					else
					{
						return null;
					}
					Log.d(mTAG, Uri.fromFile(mediaFile).toString());
					//Return the file's URI so we can pass it to onActivityResult
					return Uri.fromFile(mediaFile);	
		}
		else
		{
			return null;
		}
		
		
	}

	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);
		ParseAnalytics.trackAppOpened(getIntent());
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null)
		{
			Intent loginIntent = new Intent(this, LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(loginIntent);
			
		}
		else
		{
			String loggedinUsername = currentUser.getUsername();
			Toast.makeText(this, "Welcome "+loggedinUsername+"!", Toast.LENGTH_LONG).show();
			Log.d(mTAG, "User has been logged in.");
		}
		
		
		// Set up the action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		
		// Create the adapter that will return a fragment for each of the two
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setIcon(mSectionsPagerAdapter.getIcon(i))
					.setTabListener(this));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode == RESULT_OK)
		{
			if (requestCode == RequestConstants.CHOOSE_PHOTO_REQUEST || requestCode == RequestConstants.CHOOSE_VIDEO_REQUEST)
			{
				if(data == null)
				{
					Toast.makeText(this, "General Error", Toast.LENGTH_LONG).show();
				}
				else
				{
					Log.d(mTAG, data.getDataString());//TODO: Remove log
					mMediaUri = data.getData();
				}
				InputStream inputStream = null;
				int fileSize;
				if(requestCode == RequestConstants.CHOOSE_VIDEO_REQUEST)
				{
					
					 try 
					 {
						inputStream = getContentResolver().openInputStream(mMediaUri);
						fileSize = inputStream.available();
						Log.d(mTAG, "Set fileSize to : " + fileSize) ;
					 } catch (FileNotFoundException e) 
					 {
						Toast.makeText(this, "Error with selected file. Select a new one 1.", Toast.LENGTH_LONG).show();
						return;
						
					 }catch (IOException e) {
						
						 Toast.makeText(this, "Error with selected file. Select a new one 2.", Toast.LENGTH_LONG).show();
						return;
					 }finally
					 {
						 try 
						{
							inputStream.close();
						}catch (IOException e){
							Log.d(mTAG, "Failed to close inputStream in onActivityResult method");
							e.printStackTrace();
						}
					 }
					 
					 if (fileSize >= RequestConstants.FILE_SIZE_LIMIT)
					 {
						 Toast.makeText(this, "File size of selected file is too large. Please select a new one.", Toast.LENGTH_LONG).show();
						 return;
					 }
					 else
					 {
						 Log.d(mTAG, " File size is good!") ;
					 }
				}
				
			}
			else
			{
				Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				mediaScanIntent.setData(mMediaUri);
				sendBroadcast(mediaScanIntent);
			}
			Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
			recipientsIntent.setData(mMediaUri);
			
			String fileType;
			if(requestCode == RequestConstants.CHOOSE_PHOTO_REQUEST || requestCode == RequestConstants.TAKE_PHOTO_REQUEST)
			{
				fileType = ParseConstants.TYPE_PHOTO;
			}
			else
			{
				fileType = ParseConstants.TYPE_VIDEO;
			}
			
			recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
			startActivity(recipientsIntent);
			
		}
		else if( resultCode != RESULT_CANCELED)
		{
			
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id)
		{
			case R.id.action_logout :
				ParseUser.logOut();
				logout();
				break;
			case R.id.edit_friends:
				Intent editFriendsIntent = new Intent(this, EditFriendsActivity.class);
				//ActivityOptions options = ActivityOptions.makeScaleUpAnimation(main, 9, 90, 10, 10);
				//Bundle bundle = options.toBundle();
				startActivity(editFriendsIntent);
				break;
			case R.id.action_camera:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				AlertDialog dialog = builder.create();
				builder.setItems(R.array.camera_choices, mDialogListener);
				builder.show();
				break;
			case R.id.action_about:
				Intent about = new Intent(this, AboutActivity.class);
				about.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				startActivity(about);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void logout()
	{
		Intent logOutIntent = new Intent(this, LoginActivity.class);
		logOutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		logOutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(logOutIntent);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	
	

}
