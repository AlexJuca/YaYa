package com.AlexandreJuca.ribbit;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipientsActivity extends ListActivity 
{

	
	protected ParseRelation<ParseUser> mFriendRelations;
	protected ParseUser mCurrentUser;
	public static List<ParseUser> mFriends;
	protected final String mTAG = EditFriendsActivity.class.getSimpleName();
	protected MenuItem mSendButton;
	protected int mItemsClicked = 0;
	protected Uri mMediaUri;
	protected String mFileType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recipients);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		mMediaUri = getIntent().getData();
		mFileType =  getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);
		mSendButton = menu.getItem(0);
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
	
	private ArrayList<String> getRecipients()
	{
		ArrayList<String> recipients = new ArrayList<String>();
		for(int i = 0; i < getListView().getCount(); i++)
		{
			if(getListView().isItemChecked(i))
			{
				recipients.add(mFriends.get(i).getObjectId());
			}
			
		}
		return recipients;
	}
	private void playSound()
	{
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
		
		message.saveInBackground( new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e == null)
				{
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
		// TODO Auto-generated method stub
		super.onResume();
		boolean netOracle = true;
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
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipientsActivity.this, android.R.layout.simple_list_item_checked, usernames);
					setListAdapter(adapter);
					setProgressBarIndeterminateVisibility(false);
				}
				else
				{
					setProgressBarIndeterminateVisibility(false);
					AlertMessages.alertOnError(RecipientsActivity.this, getString(R.string.error_on_fecth_users), AlertMessages.ERROR_TYPE_ERROR);
				}	
			}
		});
	}//End SearchFriendRelations
	
	

	public void handleSendVisibility(int times_clicked)
	{
		if(times_clicked >= 1)
		{
			mSendButton.setVisible(true);
		}
		else if (times_clicked <= 0)
		{
			mSendButton.setVisible(false);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		if(getListView().isItemChecked(position))
		{	mItemsClicked += 1;
			handleSendVisibility(mItemsClicked);
		}
		if(getListView().isItemChecked(position)== false)
		{
			mItemsClicked -= 1;
			handleSendVisibility(mItemsClicked);
		}
		
	}

	
	
	
	

	
}
