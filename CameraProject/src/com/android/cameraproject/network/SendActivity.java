package com.android.cameraproject.network;

import java.util.ArrayList;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.ParseObject;
import com.parse.ParseUser;

import com.android.cameraproject.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendActivity extends ListActivity
{
	// private static final String TAG = "SendActivity";

	private ParseUser user;
	private CheckAdapter adapter;
	
	private EditText messageField;
	
	private ArrayList<CheckableItem> friendList;
	
	private TextView tv;
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.send_activity_layout);
		user = ParseUser.getCurrentUser();
		
		tv = (TextView)findViewById(R.id.nofriends);
		tv.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				Intent i = new Intent(SendActivity.this, AddFriendActivity.class);
				startActivity(i);
			}
			
		});
		
		messageField = (EditText)findViewById(R.id.messageField);
	
		// Create header
		LayoutInflater li = getLayoutInflater();
		
		ViewGroup header = (ViewGroup)li.inflate(R.layout.header, getListView(), false);
		
		getListView().addHeaderView(header, null, false);
		
		TextView headerText = (TextView)findViewById(R.id.header);
		headerText.setText("Friends");
		
		// Populate list view and set adapter
		populateListView();
		
		// This allows for up navigation (the little arrow at the top of the screen)
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Send Message");
		
	}
	
	public void onResume()
	{
		super.onResume();
		populateListView();
	}
	
	public void sendMessage()
	{
		final String message = messageField.getText().toString();
		
		if(message.isEmpty())
		{
			Toast toast = Toast.makeText(this, "You must write a message before sending", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		int numChecked = 0;
		
		friendList = (ArrayList<CheckableItem>) adapter.getItems();
		for(int i = 0; i < friendList.size(); i++)
		{
			CheckableItem friend = friendList.get(i);
			
			if(friend.isChecked())
			{
				// Log.d(TAG, friend.getUsername());

				ParseObject interaction = new ParseObject("Interaction");
				interaction.put("from", user.getUsername());
				interaction.put("to", friend.getUsername());
				interaction.put("message", message);
				interaction.put("type", "stringrequest");
				interaction.put("hasSynced", false);
				
				interaction.saveInBackground();
				
				numChecked++;

			}
			
			if(numChecked == 0)
			{
				Toast toast = Toast.makeText(this, "You must select at least one friend before sending", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}
		
		finish();
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.overlay_send_message, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this); break;
			case R.id.sendmessage:
				sendMessage(); break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void populateListView()
	{
		JSONArray friendsJSON = user.getJSONArray("friends");
		
		friendList = new ArrayList<CheckableItem>();
		
		TreeSet<String> names = new TreeSet<String>();
		if(friendsJSON != null)
		{
			for(int i = 0; i < friendsJSON.length(); i++)
			{
				try 
				{
					names.add(friendsJSON.getString(i));
				} 
				catch (JSONException e) 
				{
					// Log.e(TAG, e.getMessage());
				}
			}
		}
		for(String name : names)
		{
			CheckableItem friend = new CheckableItem(name, false);
			friendList.add(friend);
		}
		
		// Log.d(TAG, friendList.size() + "");
		
		if(friendList.size() != 0)
		{
			adapter = new CheckAdapter(this, R.layout.custom_list_item_checkable,
					friendList);
			
			setListAdapter(adapter);
		}
		else
		{
			tv.setVisibility(View.VISIBLE);
		}
	}
}
