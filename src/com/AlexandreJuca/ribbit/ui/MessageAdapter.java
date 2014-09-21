package com.AlexandreJuca.ribbit.ui;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.AlexandreJuca.ribbit.R;
import com.AlexandreJuca.ribbit.utils.ParseConstants;
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
			holder.timeLabel = (TextView)convertView.findViewById(R.id.timeLabel);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
		Date createdAt = message.getCreatedAt();
		long now = new Date().getTime();
		String convertedDate = DateUtils.getRelativeTimeSpanString(
				createdAt.getTime(), 
				now, 
				DateUtils.SECOND_IN_MILLIS).toString();
		
		
		holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
		holder.timeLabel.setText(convertedDate);
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
		TextView timeLabel;
	}
	
	//Elegantly refill list instead of reloading the fragement everytime
	public void refill(List<ParseObject> messages)
	{
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}

	@Override
	public String toString() {
		return "MessageAdapter [mContext=" + mContext + ", mTag=" + mTag
				+ ", mMessages=" + mMessages + "]";
	}
}
