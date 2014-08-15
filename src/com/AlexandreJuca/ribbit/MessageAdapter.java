package com.AlexandreJuca.ribbit;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.AlexandreJuca.ribbit.R;
import com.parse.ParseObject;


public class MessageAdapter extends ArrayAdapter<ParseObject>{
	
	protected Context mContext;
	protected String mTag = MessageAdapter.class.getSimpleName();
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> message)
	{
		super(context, R.layout.message_item, message);
		mContext = context;
		mMessages = message;
		Log.d(mTag, "In MessageAdapter constructor");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Log.d(mTag, "Just before convertView == null Check");
		if(convertView == null)
		{
			Log.d(mTag, "After convertView == null Check");
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			Log.d(mTag, "After holder");
			holder.iconImage = (ImageView)convertView.findViewById(R.id.message_icon);
			holder.nameLabel = (TextView)convertView.findViewById(R.id.sender_label);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
		
		holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_PHOTO))
		{
			holder.iconImage.setImageResource(R.drawable.ic_picture);
		}
		else
		{
			holder.iconImage.setImageResource(R.drawable.ic_video);
		}
		return convertView;
	}
	
	private static class ViewHolder
	{
		ImageView iconImage;
		TextView nameLabel;
	}
	
	//Elegantly refill list instead of reloading the fragement everytime
	public void refill(List<ParseObject> messages)
	{
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}
}
