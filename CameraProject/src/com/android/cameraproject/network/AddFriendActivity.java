package com.android.cameraproject.network;

import java.util.ArrayList;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.cameraproject.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddFriendActivity extends ListActivity
{
	private EditText friend_username_field;
	
	// private static final String TAG = "AddFriendActivity";
	
	private static ParseUser user;
	
	private JSONArray friends;
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.add_friend_activity_layout);
		
		user = ParseUser.getCurrentUser();
		// Log.i(TAG, user.getUsername());
		
		friend_username_field = (EditText)findViewById(R.id.friend_username_entry);
		
		// This allows for up navigation (the little arrow at the top of the screen)
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getActionBar().setTitle("Add Friends");
		
		// Create header
		LayoutInflater li = getLayoutInflater();
		
		ViewGroup header = (ViewGroup)li.inflate(R.layout.header, getListView(), false);
		
		getListView().addHeaderView(header, null, false);
		
		TextView headerText = (TextView)findViewById(R.id.header);
		headerText.setText("Current Friends");
		
		// Show all current friends
		JSONArray friendsJSON = user.getJSONArray("friends");
		
		ArrayList<String> friendList = new ArrayList<String>();
		
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
			friendList.add(name);
		}
		
		// Log.d(TAG, friendList.size() + "");
		
		if(friendList.size() != 0)
		{
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
					R.layout.add_friends_list_item, friendList);
			setListAdapter(adapter);
		}
	}
	
	public void addFriend()
	{
		String friend_username = friend_username_field.getText().toString().trim();
		
		if(friend_username.equals(""))
		{
			Toast toast = Toast.makeText(this, "Enter a username first", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		if(friend_username.equals(user.getUsername()))
		{
			Toast toast = Toast.makeText(this, "You cannot friend yourself", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		
		friends = user.getJSONArray("friends");
		
		try
		{
			for(int i = 0; i < friends.length(); i++)
			{
				if(friends.get(i).equals(friend_username))
				{
					Toast toast = Toast.makeText(this, "You are already friends with " + friend_username, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
			}
		}
		catch(JSONException e)
		{
			// Log.e(TAG, "JSONException");
		}
		catch(NullPointerException e)
		{
			// Log.d(TAG, "NullPointerException");
		}
		
		ParseQuery query = ParseUser.getQuery();
		
		query.whereEqualTo("username", friend_username);
		// Log.i(TAG, "Username field: " + friend_username);
		

		query.getFirstInBackground(new GetCallback() {

			@Override
			public void done(ParseObject object, ParseException e) 
			{
				if(e == null)
				{
					if(object == null)
					{
						Toast toast = Toast.makeText(AddFriendActivity.this, "Could not add friend", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					ParseUser friend = (ParseUser)object;					
					
					// Log.i(TAG, "Friend username: " + friend.getUsername());
					
					ParseObject interaction = new ParseObject("Interaction");
					interaction.put("from", user.getUsername());
					interaction.put("to", friend.getUsername());
					interaction.put("type", "friendrequest");
					interaction.put("hasSynced", false);
					interaction.saveInBackground(new SaveCallback(){
					
						public void done(ParseException e)
						{
							if(e != null)
							{
								// Log.e(TAG, "Failed to save file: " + e.getLocalizedMessage());
								return;
							}
							Toast toast = Toast.makeText(AddFriendActivity.this, "Request sent", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							// Log.i(TAG, "Save successful");
						}
					});
				}
				else
				{
					Toast toast = Toast.makeText(AddFriendActivity.this, "Could not add friend", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.overlay_add_friend, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this); break;
			case R.id.addfriend:
				addFriend();
		}
		return super.onOptionsItemSelected(item);
	}
}
