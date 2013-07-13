package com.android.cameraproject.provider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import com.android.cameraproject.network.MainMenuActivity;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service
{
	private static ParseUser user;
	
	private static final String TAG = "SyncService";
	
	private NotificationManager mNM;
	
	private int NOTIFICATION = 1;
	
	Timer t;
	
	private final Context context = this;
	
	public class LocalBinder extends Binder
	{
		SyncService getService()
		{
			return SyncService.this;
		}
	}
	
	public void onCreate()
	{
		Parse.initialize(this, "NggiqW5gj6wn7agvpMGXvkBqqO3MIft2g0s7AZIN", "eROwSRr1WWLRLDPMprPQMxbDHMBQjI9WHpRkH7vk");
		
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
	}
	
	public void onDestroy()
	{
		mNM.cancel(NOTIFICATION);
		user = null;
		t.cancel();
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(TAG, "Received start id " + startId + ": " + intent);
		
		// If we have already called onStartCommand
		if(t != null)
		{
			Log.d(TAG, "SyncService already started");
			return START_STICKY;
		}
		
		user = ParseUser.getCurrentUser();
		
		t = new Timer();
		
		// Using a SyncAdapter doesn't make sense unless I'm using an AccountManager, which
		// is why I'm using a timer to sync
		TimerTask task = new TimerTask()
		{

			@Override
			public void run() 
			{
				sync();
			}
			
		};
		
		t.scheduleAtFixedRate(task, 0, 600000);
		
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}
	
	private  final IBinder mBinder = new LocalBinder();
	
	@SuppressWarnings("deprecation")
	private void showNotification(String type, String from)
	{
		CharSequence text = "";
		
		if(type.equals("stringrequest"))
		{
			text = from + " has sent you a message";
		}
		
		if(type.equals("result"))
		{
			text = from + " has replied to your message";
		}
		
		if(type.equals("friendrequest"))
		{
			text = from + " has sent you a friend request";
		}
		
		if(type.equals("friendconfirmation"))
		{
			text = from + " has confirmed your friend request";
		}
		
		Notification notification = new Notification(android.R.drawable.stat_notify_sync, text, System.currentTimeMillis());
	
		Intent i = new Intent(this, MainMenuActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
		
		notification.setLatestEventInfo(this, "CameraProject", text, contentIntent);
		
		mNM.notify(NOTIFICATION, notification);
	}
	
	public void sync()
	{
		
		ParseQuery query = new ParseQuery("Interaction");
		query.whereEqualTo("to", user.getUsername());
		query.whereEqualTo("hasSynced", false);
		query.orderByDescending("updatedAt");
		
		query.findInBackground(new FindCallback()
		{
			public void done(List<ParseObject> toList, ParseException e)
			{
				// Set elements to be the list of elements retrieved from database
				if(e == null)
				{
					Log.d(TAG, "Retrieved: " + toList.size() + " scores");
					
					// For every element in the list returned by the query
					for(int i = 0; i < toList.size(); i++)
					{	
						ParseObject item = toList.get(i);
						Log.d(TAG, "Item " + i + " " + item.getObjectId());
						
						
						ContentValues values = new ContentValues();
						
						String type = (String) item.get("type");
						String message = item.getString("message");
						String from = item.getString("from");
						
						
						values.put("type", type);
						values.put("message", message);
						values.put("fromuser", from);
						values.put("touser", user.getUsername());
						
						Date date = item.getUpdatedAt();
						
						values.put("date", date.toString());
						
						String imagepath = "";
						
						if(type.equals("result"))
						{
							imagepath = item.getObjectId();
							
							File file = new File(context.getFilesDir().getPath().toString() + 
									File.pathSeparator + imagepath);
							
							ParseFile f = (ParseFile)item.get("photo");
							byte[] imageData;
							BufferedOutputStream bos = null;
							
							try 
							{
								imageData = f.getData();
								FileOutputStream fos = new FileOutputStream(file);
								bos = new BufferedOutputStream(fos);
								bos.write(imageData);
							}
							
							catch (ParseException e1)
							{
								Log.e(TAG, "ParseException: " + e1.getLocalizedMessage());
							}
							catch(FileNotFoundException e1)
							{
								Log.e(TAG, "FileNotFoundException: " + e1.getLocalizedMessage());
							} 
							catch (IOException e1) 
							{
								Log.e(TAG, "IOException: " + e1.getLocalizedMessage());
							}
						}
						
						values.put("imagepath", imagepath);
						
						if(type.equals("friendconfirmation"))
						{
							JSONArray array = user.getJSONArray("friends");
							ParseUser friend = toList.get(i).getParseUser("from");
							try
							{
								friend = friend.fetchIfNeeded();
							} 
							catch (ParseException e1) 
							{
								Log.e(TAG, "ParseException: " + e1.getLocalizedMessage());
							}
							try
							{
								array.put(friend.getUsername());
							}
							catch(NullPointerException e1)
							{
								array = new JSONArray();
								array.put(friend.getUsername());
							}
							user.put("friends", array);
							user.saveInBackground();
							
						}
						
						showNotification(type, from);
						
						getContentResolver().insert(Interaction.Interactions.CONTENT_URI, values);
						
						// Refresh the ListView in MainMenuActivity
						MainMenuActivity.notifyDataSetChanged();
						
						// Update the item we retrieved so we don't have to get it again
						ParseObject updatedItem = toList.get(i);
						updatedItem.put("hasSynced", true);
						updatedItem.saveInBackground();
						
					}
				}
				else
				{
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}

		});
	}

}
