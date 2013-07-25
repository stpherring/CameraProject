package com.android.cameraproject.network;

import java.util.List;

import com.android.cameraproject.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// List adapter for the main menu.
public class MainMenuAdapter extends ArrayAdapter<ListItem>
{
	private static Context context;
	private final static int layoutResourceId = R.layout.custom_list_item_mainmenu;
	
	private static List<ListItem> items;
	
//	private static final String TAG = "MainMenuAdapter";
	
	// In this constructor, items returns all the objects from a query to the content
	// provider.  Because of the way things are ordered in it, the list has to be reversed
	// before being stored in the adapter.
	public MainMenuAdapter(Context context, List<ListItem> items)
	{
		
		super(context, layoutResourceId, items);

		for(int i = 0; i < items.size()/2; i++)
		{
			ListItem temp = items.get(i);
			items.set(i, items.get(items.size()-i-1));
			items.set(items.size()-i-1, temp);
		}
		MainMenuAdapter.items = items;
		
		MainMenuAdapter.context = context;
		
	}
	
	public List<ListItem> getItems()
	{
		return items;
	}
	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final ItemHolder holder = new ItemHolder();
		
		LayoutInflater inflater = LayoutInflater.from(context);
		row = inflater.inflate(layoutResourceId, parent, false);
			
		holder.date = (TextView)row.findViewById(R.id.datetext);
		holder.message = (TextView)row.findViewById(R.id.objectmessage);
		holder.drawable = (ImageView)row.findViewById(R.id.typedrawable);
			
		row.setTag(holder);
		
		ListItem item = items.get(position);
		
		String type = item.getType();
		// Log.d(TAG, type);
		
		String from = item.getFrom();
		
		if(type.equals("friendconfirmation"))
		{
			holder.message.setText(from + " accepted your friend request");
			
			holder.drawable.setImageDrawable(context.getResources().getDrawable(R.drawable.friend_confirmation));
		}
		if(type.equals("friendrequest"))
		{
			holder.message.setText(from + " wants to be your friend");
			holder.drawable.setImageDrawable(context.getResources().getDrawable(R.drawable.friend_request));
		}
		if(type.equals("stringrequest"))
		{
			String message = item.getMessage();
			
			holder.message.setText(from + ": " + message);	
			
			holder.drawable.setImageDrawable(context.getResources().getDrawable(R.drawable.message_request));
		}
		if(type.equals("result"))
		{
			holder.message.setText(from + " replied to your message");	
		
			holder.drawable.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_result));
		}
		
		String date = item.getDate();
		
		holder.date.setText(date);
		
		if(item.hasClicked() == 0)
		{
			holder.message.setTypeface(null, Typeface.BOLD);
		}
		else
		{
			holder.message.setTypeface(null, Typeface.NORMAL);
		}
		
		// Log.d(TAG, date);
		
		return row;
		
	}
	
	static class ItemHolder
	{
		ImageView drawable;
		TextView message;
		TextView date;
	}
}
