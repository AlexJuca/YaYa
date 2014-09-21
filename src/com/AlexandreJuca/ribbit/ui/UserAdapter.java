package com.AlexandreJuca.ribbit.ui;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.AlexandreJuca.ribbit.R;
import com.AlexandreJuca.ribbit.utils.MD5Utils;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


public class UserAdapter extends ArrayAdapter<ParseUser>{
	
	protected Context mContext;
	protected String mTag = UserAdapter.class.getSimpleName();
	protected List<ParseUser> mUsers;
	
	public UserAdapter(Context context, List<ParseUser> users)
	{
		super(context, R.layout.user_item, users);
		mContext = context;
		mUsers = users;
		Log.d(mTag, "In UserAdapter constructor");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Log.d(mTag, "Just before convertView == null Check");
		if(convertView == null)
		{
			Log.d(mTag, "After convertView == null Check");
			convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, null);
			holder = new ViewHolder();
			Log.d(mTag, "After holder");
			holder.gravatarCheckedImage = (ImageView)convertView.findViewById(R.id.user_gravatar_checkmarck);
			holder.gravatarImage = (ImageView)convertView.findViewById(R.id.user_gravatar);
			holder.nameLabel = (TextView)convertView.findViewById(R.id.nameLabel);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseUser user = mUsers.get(position);
		
		
		holder.nameLabel.setText(user.getUsername());
		
		GridView gridView = (GridView)parent;
		
		if(gridView.isItemChecked(position))
		{
			//Sholder.gravatarCheckedImage.setImageResource(R.drawable.avatar_selected);
			holder.gravatarCheckedImage.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.gravatarCheckedImage.setVisibility(View.INVISIBLE);
		}
		
		String email = user.getEmail().toLowerCase(Locale.getDefault());
		if(email.equals(""))
		{
			holder.gravatarImage.setImageResource(R.drawable.avatar_empty);
		}
		else
		{
			// Here we create some dummy profile pics which are static images
			String hash = MD5Utils.md5Hex(email);
			String gravatarUrl = "http://www.gravatar.com/avatar/"+ hash 
					+ "?s=204&d=404";
			Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.avatar_empty).into(holder.gravatarImage);
			Log.d(mTag, gravatarUrl);
			if(user.getUsername().equals("Alex"))
			{
				holder.gravatarImage.setImageResource(R.drawable.my_baby);
			}
			if(user.getUsername().toLowerCase().equals("ineza"))
			{
				holder.gravatarImage.setImageResource(R.drawable.ineza);
			}
			if(user.getUsername().equals("elizabeth"))
			{
				holder.gravatarImage.setImageResource(R.drawable.elizabeth);
			}
			if(user.getUsername().toLowerCase().equals("abilio"))
			{
				holder.gravatarImage.setImageResource(R.drawable.abilio);
			}
			if(user.getUsername().toLowerCase().equals("adriana"))
			{
				holder.gravatarImage.setImageResource(R.drawable.adriana);
			}
			if(user.getUsername().toLowerCase().equals("ruben cabangu"))
			{
				holder.gravatarImage.setImageResource(R.drawable.ruben);
			}
			
			
		}
		
		/*if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_PHOTO))
		{
			holder.iconImage.setImageResource(R.drawable.ic_picture);
		}
		else
		{
			holder.iconImage.setImageResource(R.drawable.ic_video);
		}*/
		return convertView;
	}
	
	private static class ViewHolder
	{
		ImageView gravatarImage;
		ImageView gravatarCheckedImage;
		TextView nameLabel;
		
	}
	
	//Elegantly refill list instead of reloading the fragement everytime
	public void refill(List<ParseUser> users)
	{
		mUsers.clear();
		mUsers.addAll(users);
		notifyDataSetChanged();
	}

	@Override
	public String toString() {
		return "MessageAdapter [mContext=" + mContext + ", mTag=" + mTag
				+ ", mMessages=" + mUsers + "]";
	}
}
