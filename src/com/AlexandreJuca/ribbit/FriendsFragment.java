package com.AlexandreJuca.ribbit;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment extends ListFragment {
	protected final String mTAG = InboxFragment.class.getSimpleName();
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendRelations;
	protected ParseUser mCurrentUser;
	private boolean mAreFriendsloaded = false;
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
		View rootView = inflater.inflate(R.layout.fragment_friends, container,
				false);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setListAdapter(getListAdapter());
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
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1,
								usernames);
						setListAdapter(adapter);
						getActivity().setProgressBarIndeterminateVisibility(false);
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
