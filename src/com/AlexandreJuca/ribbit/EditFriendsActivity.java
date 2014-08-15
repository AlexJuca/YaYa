package com.AlexandreJuca.ribbit;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends ListActivity {
	
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendRelations;
	protected ParseUser mCurrentUser;
	protected final String mTAG = EditFriendsActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.activity_edit_friends);
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	@Override
	protected void onResume() 
	{
		
		// TODO Auto-generated method stub
		super.onResume();
		boolean netOracle = true;
		if(netOracle)
		{
			searchFriendRelations();
		}
		else
		{
			AlertMessages.alertOnError(this, "No Network Connection. Please check internet connection.", AlertMessages.ERROR_TYPE_NO_NETWORK);
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
					
					mUsers = listusers;
					String[] usernames = new String[mUsers.size()];
					int i = 0;
					for(ParseUser users :  listusers)
					{
						usernames[i] = users.getUsername();
						i++;
					}
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
					setListAdapter(adapter);
					setProgressBarIndeterminateVisibility(false);
					addFriendCheckMarks();
				
				}
				else
				{
					setProgressBarIndeterminateVisibility(false);
					AlertMessages.alertOnError(EditFriendsActivity.this, "Error fetching users.", AlertMessages.ERROR_TYPE_ERROR);
				}
				
			}
			
			
		});
	}
	
	/* Implementation below could be slow when we have a huge amount of friends */
	private void addFriendCheckMarks() {
		
		mFriendRelations.getQuery().findInBackground( new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				
				if(e == null)
				{
					//Loop through returned users
					for (int i = 0; i < mUsers.size(); i++)
					{
						ParseUser user = mUsers.get(i);//assign a user to the ParseUser object for comparison
						for (ParseUser friend : friends)
						{
							if (friend.getObjectId().equals(user.getObjectId()))//Check if the user is our friend
							{
								getListView().setItemChecked(i, true);//Check Item
							}
						}
					}
					
				}else
				{
					Log.e(mTAG, e.getMessage());
				}
				
			}
		});
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		if(getListView().isItemChecked(position))
		{
			addFriend(position);
		}
		if(getListView().isItemChecked(position)== false)
		{
			removeFriend(position);
		}
		
	}

	private void removeFriend(final int position) {
		
		mFriendRelations.remove(mUsers.get(position));
		
		mCurrentUser.saveInBackground( new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
			
				if(e != null)
				{
					Log.e(mTAG, e.getMessage());
				}
				else
				{
					alertOnRemoveFriend(position);
				}	
			}
		});		
	}
	
	
	//Alert the user when a user has been succesfuly removed
	private void alertOnRemoveFriend(int position) {
		Toast.makeText(this, "Successfully removed "+mUsers.get(position).getUsername()+" from friends list", Toast.LENGTH_LONG).show();
	}
	
	private void alertOnAddFriend(int position) {
		Toast.makeText(this, ""+mUsers.get(position).getUsername()+" is now your friend. Say Hi.", Toast.LENGTH_LONG).show();
	}

	private void addFriend(final int position) {
		mFriendRelations.add(mUsers.get(position));
		mCurrentUser.saveInBackground( new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if(e != null)
				{
					Log.e(mTAG, "Execption: "+e);
				}
				else
				{
					alertOnAddFriend(position);
				}	
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
		
	}

	
}
