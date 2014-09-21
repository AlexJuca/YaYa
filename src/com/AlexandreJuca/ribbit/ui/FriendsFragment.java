package com.AlexandreJuca.ribbit.ui;

import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.AlexandreJuca.ribbit.R;
import com.AlexandreJuca.ribbit.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment extends Fragment {
	protected final String mTAG = InboxFragment.class.getSimpleName();
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendRelations;
	protected ParseUser mCurrentUser;
	private boolean mAreFriendsloaded = false;
	protected GridView mGridView;
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "2";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static FriendsFragment newInstance(int sectionNumber) {
		FriendsFragment fragment = (FriendsFragment) new Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FriendsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.user_grid, container,
				false);
		mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}
	
	@Override
	public void onPause() {
		mFriendRelations.getQuery().cancel();
		super.onPause();
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		if (mAreFriendsloaded == false)
		{
			getActivity().setProgressBarIndeterminateVisibility(true);
			mCurrentUser = ParseUser.getCurrentUser();
			//ParseQuery<ParseUser> query = mFriendRelations.getQuery();
			//query.addAscendingOrder(ParseConstants.KEY_USERNAME);
			mFriendRelations = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATIONS);
			mFriendRelations.getQuery().findInBackground( new FindCallback<ParseUser>() 
			{
				
				@Override
				public void done(List<ParseUser> friends, ParseException e) 
				{
					
					if (e == null)
					{
						
						mFriends = friends;
						String[] usernames = new String[mFriends.size()];
						int i = 0;
						for(ParseUser users :  friends)
						{
							usernames[i] = users.getUsername();
							i++;
						}
						if (mGridView.getAdapter() == null)
						{
							UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
							mGridView.setAdapter(adapter);
							getActivity().setProgressBarIndeterminateVisibility(false);
						}
						else
						{
							((UserAdapter)mGridView.getAdapter()).refill(mFriends);
						}
					}
					else
					{
						getActivity().setProgressBarIndeterminateVisibility(false);
					}
					mAreFriendsloaded = true;	
					
				}
			});
			
		}
		else
		{
			//Screens already loaded dont refresh
		}
		
		
	}}
