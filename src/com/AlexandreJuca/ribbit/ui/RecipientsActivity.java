package com.AlexandreJuca.ribbit.ui;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import com.AlexandreJuca.ribbit.R;
import com.AlexandreJuca.ribbit.utils.FileHelper;
import com.AlexandreJuca.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class RecipientsActivity extends Activity 
{
	protected ParseRelation<ParseUser> mFriendRelations;
	protected ParseUser mCurrentUser;
	public static List<ParseUser> mFriends;
	protected final String mTAG = EditFriendsActivity.class.getSimpleName();
	protected MenuItem mSendMenuItem;
	protected int mItemsClicked = 0;
	protected Uri mMediaUri;
	protected String mFileType;
	public GridView mGridView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);
		
		mGridView = (GridView)findViewById(R.id.friendsGrid);
		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);
		mMediaUri = getIntent().getData();
		mFileType =  getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);
		mSendMenuItem = menu.getItem(0);
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
		case R.id.action_send:
			ParseObject message = createMessage();
			if (message == null)
			{
				AlertMessages.alertOnError(this, getString(R.string.upload_file_error), AlertMessages.ERROR_TYPE_ERROR);
			}
			else
			{
				send(message);
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private ParseObject createMessage() {
		/*
		 * This method constructs the message that we will send to other users
		 * Here we bundle the necessary data and reduce the image size so we can send it more efficiently
		 */
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		message.put(ParseConstants.KEY_RECIPIENT_IDs, getRecipients());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
		byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
		
		if(fileBytes == null)
		{
			return null;
		}
		else
		{
			if (mFileType.equals(ParseConstants.TYPE_PHOTO))
			{
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);
			}
			String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);
			message.put(ParseConstants.KEY_FILE, file);
		}
		return message;
	
	}
	
	private ArrayList<String> getRecipients(){
		/*
		 *  Get The recipents that have been marked by the user and return
		 */
		ArrayList<String> recipients = new ArrayList<String>();
		for(int i = 0; i < mGridView.getCount(); i++)
		{
			if(mGridView.isItemChecked(i))
			{
				recipients.add(mFriends.get(i).getObjectId());
			}
			
		}
		return recipients;
	}
	
	private void playSound(){
		/*
		 * Play this funky sound whenever we send a new message
		 */
		MediaPlayer mediaplayer = MediaPlayer.create(this, R.raw.internet_amigos);
		mediaplayer.start();
		mediaplayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
				mp = null;
				
			}
		});
	}
	
	
	private void send(ParseObject message) {
		/*
		 *  Send the message and Push Notification to selected users and show error if something went wrong
		 */
		message.saveInBackground( new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null)
				{
					sendPushNotification();
					playSound();
					Toast.makeText(RecipientsActivity.this, getString(R.string.message_sucess), Toast.LENGTH_LONG).show();
					
				}
				else
				{
					AlertMessages.alertOnError(RecipientsActivity.this, getString(R.string.failed_message), AlertMessages.ERROR_TYPE_ERROR);
				}
				
			}
		});
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		boolean netOracle = true;
		// TODO: Check if network is available before trying to actually load users
		if(netOracle)
		{
			searchFriendRelations();
		}
		else
		{
			AlertMessages.alertOnError(this, getString(R.string.no_network_connection), AlertMessages.ERROR_TYPE_NO_NETWORK);
		}
	}
	
	
	private void searchFriendRelations() {
		/*
		 *  Search users friends relations and display them on the screen, show error on failure.
		 */
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendRelations = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(ParseConstants.MAX_QUERY_LIMIT);
		setProgressBarIndeterminateVisibility(true);
		query.findInBackground( new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> listusers, ParseException e) {
				if (e == null)
				{
					mFriends = listusers;
					String[] usernames = new String[mFriends.size()];
					int i = 0;
					for(ParseUser users :  listusers)
					{
						usernames[i] = users.getUsername();
						i++;
					}
					if (mGridView.getAdapter() == null)
					{
						UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
						
						setProgressBarIndeterminateVisibility(false);
					}
					else
					{
						((UserAdapter)mGridView.getAdapter()).refill(mFriends);
					}
				}
				else
				{
					setProgressBarIndeterminateVisibility(false);
					AlertMessages.alertOnError(RecipientsActivity.this, getString(R.string.error_on_fecth_users), AlertMessages.ERROR_TYPE_ERROR);
				}	
			}
		});
	}
	
	
	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		
			if (mGridView.getCheckedItemCount() == 0) {
				mSendMenuItem.setVisible(true);
			}
			else {
				mSendMenuItem.setVisible(false);
			}
			mSendMenuItem.setVisible(true);
			ImageView checkImageView = (ImageView)view.findViewById(R.id.user_gravatar_checkmarck);

			if (mGridView.isItemChecked(position)) {
				// add the recipient
				checkImageView.setVisibility(View.VISIBLE);
			}
			else {
				// remove the recipient
				checkImageView.setVisibility(View.INVISIBLE);
			}
		}
	};
	
	
	
	private void sendPushNotification(){
		/*
		 *  Send Push Notification to selected users in the background
		 */
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipients());
		
		// Send push notification
		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message,
				ParseUser.getCurrentUser().getUsername()));
		push.sendInBackground();
	}

	
	
	
	

	
}
