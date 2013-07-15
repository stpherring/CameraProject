package com.android.cameraproject.network;

import java.io.File;
import java.util.ArrayList;
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
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainMenuActivity extends ListActivity
{
	private static MainMenuAdapter adapter;
	private static ParseUser user;
	private static Context context;
	
	private static final String TAG = "MainMenuActivity";
	private static List<ListItem> objects;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this, "NggiqW5gj6wn7agvpMGXvkBqqO3MIft2g0s7AZIN", "eROwSRr1WWLRLDPMprPQMxbDHMBQjI9WHpRkH7vk");
		ParseAnalytics.trackAppOpened(getIntent());
		
		user = ParseUser.getCurrentUser();
		
		context = getApplicationContext();
		
		// If no user is logged in
		if(user == null)
		{
			Log.d(TAG, "User not logged in");
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			finish();
		}
		else
		{
			setContentView(R.layout.list_activity_layout);
			Log.i(TAG, user.getUsername());
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
	
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		
		ListItem current = objects.get(position);
		
		String type = current.getType();
		
		if(type.equals("friendrequest"))
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
		else if(type.equals("stringrequest"))
		{
			Intent i = new Intent(this, CameraActivity.class);
			
			i.putExtra("request", current.getMessage());
			i.putExtra("touser", current.getFrom());
			
			startActivity(i);
		}
		
		
		else if(type.equals("result"))
		{
			Intent i = new Intent(this, ResultActivity.class);
			
			File file = new File(this.getFilesDir().getPath().toString() + 
					File.pathSeparator + current.getImagePath());
			
			if(!file.exists())
			{
				Log.e(TAG, "File does not exist");
			}
			
			i.putExtra("File", file);
			
			startActivity(i);
			
		}
		
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
				this.stopService(new Intent(this, SyncService.class));
				ParseUser.logOut();
				i = new Intent(this, LoginActivity.class);
				startActivity(i);
				finish();
				break;
			case R.id.addfriend:
				i = new Intent(this, AddFriendActivity.class);
				startActivity(i);
				
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	// Used in SyncService to refresh the ListView
	public static void notifyDataSetChanged()
	{
		adapter = new PopulateListViewTask().doInBackground(context);
		adapter.notifyDataSetChanged();
	}
	
	// Populating the ListView on the main thread slows down the app significantly if there are
	// more than 7 or so items, so we send the task to another thread
	public static class PopulateListViewTask extends AsyncTask<Context, Integer, MainMenuAdapter>
	{
		
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
				
				String date = cursor.getString(2);
				
				String type = cursor.getString(3);
				
				String imagepath = "";
				
				if(type.equals("result"))
				{
					imagepath = cursor.getString(5);
				}
				
				String from = cursor.getString(6);	
				
				ListItem item = new ListItem(from, type, message, date, imagepath);
				
				items.add(item);
				
				Log.d(TAG, "Cursor is being moved");
			}
			Log.d(TAG, "Items size: " + items.size());
			MainMenuAdapter adapter = new MainMenuAdapter(context[0], items);
			adapter.notifyDataSetChanged();
			objects = adapter.getItems();
			
			return adapter;
		}
		
	}
}
