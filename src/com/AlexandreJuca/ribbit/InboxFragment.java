package com.AlexandreJuca.ribbit;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class InboxFragment extends ListFragment {
	protected final String mTAG = InboxFragment.class.getSimpleName();
	protected ParseUser mCurrentUser;
	protected List<ParseObject> mMessages;
	String senderName;
	protected ParseQuery<ParseObject> mQuery;
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "1";

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	
	public static InboxFragment newInstance(int sectionNumber) {
		InboxFragment fragment = (InboxFragment) new Fragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public InboxFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		return rootView;
	}
	
	@Override
	public void onPause() {
		mQuery.cancel();
		super.onPause();
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		getActivity().setProgressBarIndeterminateVisibility(true);
		mQuery = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
		mQuery.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDs, ParseUser.getCurrentUser().getObjectId());
		mQuery.addAscendingOrder(ParseConstants.CREATED_AT);
		mQuery.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (e == null)
				{
					mMessages = messages;
					String[] mess = new String[mMessages.size()];
					int i = 0;
					for(ParseObject message :  mMessages)
					{
						mess[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					if(getListView().getAdapter() == null)
					{
						MessageAdapter messageAdapter = new MessageAdapter(getActivity()
								, messages);
						setListAdapter(messageAdapter);
					}
					else
					{
						//Refresh list
						((MessageAdapter)getListView().getAdapter()).refill(messages);
					}
					
				}
				else
				{
					Toast.makeText(getActivity(), "No new messages!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		Uri urifile = Uri.parse(file.getUrl());
		senderName = mMessages.get(position).getString(ParseConstants.KEY_SENDER_NAME);
		
		if(messageType.equals(ParseConstants.TYPE_PHOTO))
		{
			Intent openPhotoIntent = new Intent(getActivity(), ViewImageActivity.class);
			openPhotoIntent.setData(urifile);
			openPhotoIntent.putExtra(ParseConstants.KEY_SENDER_NAME, senderName);
			startActivity(openPhotoIntent);
		}
		else
		{
			Intent openVideoIntent = new Intent(Intent.ACTION_VIEW);
			openVideoIntent.setDataAndType(urifile, "video/*");
			startActivity(openVideoIntent);
		}
	}
	

}
