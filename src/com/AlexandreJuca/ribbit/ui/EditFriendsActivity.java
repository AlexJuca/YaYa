package com.AlexandreJuca.ribbit.ui;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.AlexandreJuca.ribbit.R;
import com.AlexandreJuca.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditFriendsActivity extends Activity {
	
	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendRelations;
	protected ParseUser mCurrentUser;
	protected final String mTAG = EditFriendsActivity.class.getSimpleName();
	protected GridView mGridView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);
		setContentView(R.layout.user_grid);
		mGridView = (GridView) findViewById(R.id.friendsGrid);
		mGridView.setChoiceMode(mGridView.CHOICE_MODE_MULTIPLE);
		mGridView.setOnItemClickListener(mOnItemClickListener);
	}
	
	@Override
	protected void onResume() 
	{
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

	// TODO : Refactor, code repetition in RecipientsActivity
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
					
					mUsers = listusers;
					String[] usernames = new String[mUsers.size()];
					int i = 0;
					for(ParseUser users :  listusers)
					{
						usernames[i] = users.getUsername();
						i++;
					}
					
					if (mGridView.getAdapter() == null)
					{
						UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
						mGridView.setAdapter(adapter);
						
						setProgressBarIndeterminateVisibility(false);
					}
					else
					{
						((UserAdapter)mGridView.getAdapter()).refill(mUsers);
					}
				
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
		/*
		 *  This method adds the checkmarks after we click on a user 
		 */
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
								mGridView.setItemChecked(i, true);//Check Item
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
	
	
	private void removeFriend(final int position) {
		/*
		 *  This method removes the friend from the users friendsRelation table in the cloud
		 */
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
	
	
	
	
	private void alertOnRemoveFriend(int position) {
		/*
		 *  This methods alerts the user when someone has been removed
		 */
		Toast.makeText(this, "Successfully removed "+mUsers.get(position).getUsername()+" from friends list", Toast.LENGTH_LONG).show();
	}
	
	private void alertOnAddFriend(int position) {
		Toast.makeText(this, ""+mUsers.get(position).getUsername()+" is now your friend. Say Hi.", Toast.LENGTH_LONG).show();
	}

	private void addFriend(final int position) {
		/*
		 *  This method adds friends that has been selected by the user
		 *  @param position : The position of the item clicked
		 */	
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
	
	protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		
			ImageView checkImageView = (ImageView)view.findViewById(R.id.user_gravatar_checkmarck);

			if (mGridView.isItemChecked(position)) {
				// add the recipient
				checkImageView.setVisibility(View.VISIBLE);
				addFriend(position);
			}
			else {
				// remove the recipient
				checkImageView.setVisibility(View.INVISIBLE);
				removeFriend(position);
			}
		}
	};
	
	

	
}
