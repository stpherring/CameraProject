package com.android.cameraproject.network;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.cameraproject.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddFriendActivity extends Activity
{
	private EditText friend_username_field;
	
	private static final String TAG = "AddFriendActivity";
	
	private static ParseUser user;
	
	private JSONArray friends;
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.add_friend_activity_layout);
		
		user = ParseUser.getCurrentUser();
		Log.i(TAG, user.getUsername());
		
		friend_username_field = (EditText)findViewById(R.id.friend_username_entry);
	}
	
	public void addFriend(View v)
	{
		String friend_username = friend_username_field.getText().toString().trim();
		
		if(friend_username.equals(user.getUsername()))
		{
			Log.d(TAG, "You cannot friend yourself");
			return;
		}
		
		friends = user.getJSONArray("friends");
		
		try
		{
			for(int i = 0; i < friends.length(); i++)
			{
				if(friends.get(i).equals(friend_username))
				{
					Log.d(TAG, "You are already friends with " + friend_username);
					return;
				}
			}
		}
		catch(JSONException e)
		{
			Log.e(TAG, "JSONException");
		}
		catch(NullPointerException e)
		{
			Log.d(TAG, "NullPointerException");
		}
		
		ParseQuery query = ParseUser.getQuery();
		
		query.whereEqualTo("username", friend_username);
		Log.i(TAG, "Username field: " + friend_username);
		
		
		query.getFirstInBackground(new GetCallback() {

			@Override
			public void done(ParseObject object, ParseException e) 
			{
				if(e == null)
				{
					
					ParseUser friend = (ParseUser)object;					
					
					Log.i(TAG, "Friend username: " + friend.getUsername());
					
					ParseObject interaction = new ParseObject("Interaction");
					interaction.put("from", user);
					interaction.put("to", friend);
					interaction.put("type", "friendrequest");
					interaction.put("hasSynced", false);
					interaction.saveInBackground(new SaveCallback(){
					
						public void done(ParseException e)
						{
							if(e != null)
							{
								Log.e(TAG, "Failed to save file: " + e.getLocalizedMessage());
								return;
							}
							Log.i(TAG, "Save successful");
						}
					});
				}
				else
				{
					Log.d(TAG, "Error: " + e.getLocalizedMessage());
				}
			}
		});
	}
}
