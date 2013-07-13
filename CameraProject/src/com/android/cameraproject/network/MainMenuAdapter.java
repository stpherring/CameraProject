package com.android.cameraproject.network;

import java.util.List;

import com.android.cameraproject.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
	
	private static final String TAG = "MainMenuAdapter";
	
	public MainMenuAdapter(Context context, List<ListItem> items)
	{
		
		super(context, layoutResourceId, items);

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
		
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
			
		holder.date = (TextView)row.findViewById(R.id.datetext);
		holder.message = (TextView)row.findViewById(R.id.objectmessage);
		holder.drawable = (ImageView)row.findViewById(R.id.typedrawable);
			
		row.setTag(holder);
		
		ListItem item = items.get(position);
		
		String type = item.getType();
		Log.d(TAG, type);
		
		String from = item.getFrom();
		
		if(type.equals("friendconfirmation"))
		{
			holder.message.setText(from + " has accepted your friend request");
		}
		if(type.equals("friendrequest"))
		{
			holder.message.setText(from + " wants to be your friend");	
		}
		if(type.equals("stringrequest"))
		{
			String message = item.getMessage();
			
			holder.message.setText(from + ": " + message);	
		}
		if(type.equals("result"))
		{
			holder.message.setText(from + " has replied");	
		}
		
		String date = item.getDate();
		
		holder.date.setText(date);
		
		Log.d(TAG, date);
		
		holder.drawable.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
		
		return row;
		
	}
	
	static class ItemHolder
	{
		ImageView drawable;
		TextView message;
		TextView date;
	}
}
