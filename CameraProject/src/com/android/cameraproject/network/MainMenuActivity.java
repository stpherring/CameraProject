package com.android.cameraproject.network;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import com.android.cameraproject.R;
import com.android.cameraproject.camera.CameraActivity;
import com.android.cameraproject.provider.SyncService;
import com.android.cameraproject.provider.Interaction.Interactions;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainMenuActivity extends ListActivity
{
	private static MainMenuAdapter adapter;
	private static ParseUser user;
	
	// private static final String TAG = "MainMenuActivity";
	private static List<ListItem> objects;
	
	private BroadcastReceiver receiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Log.d(TAG, "onReceive");
			adapter = new PopulateListViewTask().doInBackground(context);
			setListAdapter(adapter);
		}
	};
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this, "NggiqW5gj6wn7agvpMGXvkBqqO3MIft2g0s7AZIN", "eROwSRr1WWLRLDPMprPQMxbDHMBQjI9WHpRkH7vk");
		ParseAnalytics.trackAppOpened(getIntent());
		
		user = ParseUser.getCurrentUser();
		
		// If no user is logged in
		if(user == null)
		{
			// Log.d(TAG, "User not logged in");
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			setContentView(R.layout.list_activity_layout);
			// Log.i(TAG, user.getUsername());
		}
	}
	
	public void onStart()
	{
		super.onStart();
		
		// Starts the service to sync periodically with the server
		this.startService(new Intent(this, SyncService.class));
		adapter = new PopulateListViewTask().doInBackground(this);
		setListAdapter(adapter);
	}
	
	public void onResume()
	{
		super.onResume();
		registerReceiver(receiver, new IntentFilter(SyncService.BROADCAST_ACTION));
	}
	
	public void onPause()
	{
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		
		final ListItem current = objects.get(position);
		
		if(current.hasActed() == 1)
		{
			return;
		}
		
		ContentValues values = new ContentValues();
		values.put(Interactions.HASCLICKED, 1);
		
		String type = current.getType();
		
		if(type.equals("friendrequest"))
		{

			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("Friend Request")
			.setMessage("Accept " + current.getFrom() + "'s friend request?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					JSONArray friends = user.getJSONArray("friends");
					
					if(friends == null)
					{
						friends = new JSONArray();
					}
					
					String from = current.getFrom();
					friends.put(from);
					
					user.put("friends", friends);
					user.saveInBackground();
					
					
					ParseObject confirmation = new ParseObject("Interaction");
					confirmation.put("type", "friendconfirmation");
					confirmation.put("to", from);
					confirmation.put("from", user.getUsername());
					confirmation.put("hasSynced", false);
					confirmation.saveInBackground();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					current.setType("");
				}
			})
			.show();
			
			// Since the user has made their decision, the item shouldn't do anything when clicked
			values.put(Interactions.HASACTED, 1);
			
			// Sets the current ListItem to be acted (for friend requests) as a temporary thing until the 
			// ListView gets refreshed (and replaced by the value I update in the query above)
			current.setActed(1);
			
		}
		else if(type.equals("stringrequest"))
		{
			Intent i = new Intent(this, CameraActivity.class);
			
			i.putExtra("request", current.getMessage());
			i.putExtra("touser", current.getFrom());
			i.putExtra("objectid", current.getId());
			
			startActivity(i);
		}
		
		
		else if(type.equals("result"))
		{
			Intent i = new Intent(this, ResultActivity.class);
			
			File file = new File(this.getFilesDir().getPath().toString() + 
					File.pathSeparator + current.getImagePath());
			
			if(!file.exists())
			{
				// Log.e(TAG, "File does not exist");
			}
			
			i.putExtra("File", file);
			i.putExtra("message", current.getMessage());
			
			startActivity(i);
			
		}
		
		this.getContentResolver()
		.update(Interactions.CONTENT_URI, values,
			   Interactions.TOUSER + " = '" + user.getUsername() + "' AND " +
			   Interactions.INTERACTION_ID + " = " + current.getId(), null);
		
		objects.set(position, current);
		
		adapter.notifyDataSetChanged();
		
	}
	
	// Sets the ActionBar at the top of the screen
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.overlay, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.sendmessage:
				Intent i = new Intent(this, SendActivity.class);
				startActivity(i); break;
			case R.id.logout:
				new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Log Out")
				.setMessage("Are you sure you want to log out?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						MainMenuActivity.this.stopService(new Intent(MainMenuActivity.this, SyncService.class));
						ParseUser.logOut();
						Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();		
					}
				})
				.setNegativeButton("No", null)
				.show();
				
				break;
			case R.id.addfriend:
				i = new Intent(this, AddFriendActivity.class);
				startActivity(i);
				
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	// Populating the ListView on the main thread slows down the app significantly if there are
	// more than 7 or so items, so we send the task to another thread
	public static class PopulateListViewTask extends AsyncTask<Context, Integer, MainMenuAdapter>
	{
		
		@SuppressLint("SimpleDateFormat")
		@Override
		protected MainMenuAdapter doInBackground(Context... context) 
		{
			// Query the ContentProvider for all of the saved interactions
			Cursor cursor = context[0].getContentResolver()
					.query(Interactions.CONTENT_URI, null,
						   Interactions.TOUSER + " = '" + user.getUsername() + "'", null, null);
			
			ArrayList<ListItem> items = new ArrayList<ListItem>();
			while(cursor.moveToNext())
			{		
				String message = cursor.getString(0);
				
				String date = cursor.getString(7);
				
				DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
				
				Date d = null;
				try 
				{
					d = format.parse(date);
				} 
				catch (ParseException e) 
				{
					// Log.e(TAG, e.getMessage());
				}
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(d);
				
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				
				Calendar currenttime = Calendar.getInstance();
				currenttime.getTime();
				int curyear = currenttime.get(Calendar.YEAR);
				int curmonth = currenttime.get(Calendar.MONTH);
				int curday = currenttime.get(Calendar.DAY_OF_MONTH);
				int curhour = currenttime.get(Calendar.HOUR_OF_DAY);
				int curminute = currenttime.get(Calendar.MINUTE);
				
				if(curyear - year == 1)
				{
					date = "1 year ago";
				}
				else if(curyear - year > 0)
				{
					date = curyear - year + " years ago";
				}
				else if(curmonth - month == 1)
				{
					date = "1 month ago";
				}
				else if(curmonth - month > 0)
				{
					date = curmonth - month + " months ago";
				}
				else if(curday - day == 1)
				{
					date = "1 day ago";
				}
				else if(curday - day > 0)
				{
					date = curday - day + " days ago";
				}
				else if(curhour - hour == 1)
				{
					date = "1 hour ago";
				}
				else if(curhour - hour > 0)
				{
					date = curhour - hour + " hours ago";
				}
				else if(curminute - minute == 1)
				{
					date = "1 minute ago";
				}
				else if(curminute - minute > 0)
				{
					date = curminute - minute + " minutes ago";
				}
				else
				{
					date = "Less than a minute ago";
				}
				
				String type = cursor.getString(8);
				
				// Log.d(TAG, type);
				
				String imagepath = "";
				
				if(type.equals("result"))
				{
					imagepath = cursor.getString(2);
				}
				
				String from = cursor.getString(3);	
				
				int hasclicked = cursor.getInt(4);
				
				int objectId = cursor.getInt(1);
				// Log.d(TAG, objectId + "");
				
				int hasacted = cursor.getInt(6);
				
				ListItem item = new ListItem(from, type, message, date, imagepath, hasclicked, objectId, hasacted);
				
				items.add(item);
				
				// Log.d(TAG, "Cursor is being moved");
			}
			// Log.d(TAG, "Items size: " + items.size());
			MainMenuAdapter adapter = new MainMenuAdapter(context[0], items);
			adapter.notifyDataSetChanged();
			objects = adapter.getItems();
			
			return adapter;
		}
		
	}
}
