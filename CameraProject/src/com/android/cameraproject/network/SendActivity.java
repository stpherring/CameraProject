package com.android.cameraproject.network;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

import com.parse.ParseObject;
import com.parse.ParseUser;

import com.android.cameraproject.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SendActivity extends ListActivity
{
	private static final String TAG = "SendActivity";

	private ParseUser user;
	private CheckAdapter adapter;
	
	private EditText messageField;
	
	private ArrayList<CheckableItem> friendList;
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.send_activity_layout);
		user = ParseUser.getCurrentUser();
		
		messageField = (EditText)findViewById(R.id.messageField);
		
		JSONArray friendsJSON = user.getJSONArray("friends");
		
		Log.d(TAG, friendsJSON.toString());
		
		friendList = new ArrayList<CheckableItem>();
		
		for(int i = 0; i < friendsJSON.length(); i++)
		{
			// We don't have to retrieve the friends' accounts until the user selects them
			try 
			{
				CheckableItem friend = new CheckableItem(friendsJSON.getString(i), false);
				friendList.add(friend);
			} 
			catch (JSONException e) 
			{
				Log.e(TAG, "JSONException: " + e.getLocalizedMessage());
			}
		}
		
		adapter = new CheckAdapter(this, R.layout.custom_list_item_checkable,
				friendList);
		
		setListAdapter(adapter);
		
		
	}
	
	public void sendMessage(View v)
	{
		final String message = messageField.getText().toString();
		
		if(message.isEmpty())
		{
			Log.d(TAG, "You must write a message before sending");
			return;
		}
		
		friendList = (ArrayList<CheckableItem>) adapter.getItems();
		for(int i = 0; i < friendList.size(); i++)
		{
			CheckableItem friend = friendList.get(i);
			if(friend.isChecked())
			{
				Log.d(TAG, friend.getUsername());

				ParseObject interaction = new ParseObject("Interaction");
				interaction.put("from", user.getUsername());
				interaction.put("to", friend.getUsername());
				interaction.put("message", message);
				interaction.put("type", "stringrequest");
				interaction.put("hasSynced", false);
				
				interaction.saveInBackground();

			}
		}
		
		finish();
	}
}
