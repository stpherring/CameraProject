package com.android.cameraproject.provider;


import android.net.Uri;
import android.provider.BaseColumns;

public class Interaction 
{
	// Stores the constants for the ContentProvider
	public static final class Interactions implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse("content://" + 
				InteractionContentProvider.AUTHORITY + "/interactions");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.interactions";
		
		public static final String INTERACTION_ID = "_id";
		
		// This is the user sending the interaction
		public static final String FROMUSER = "fromuser";
		
		// This should be the user currently using the app
		public static final String TOUSER = "touser";
		
		// The type of interaction
		public static final String TYPE = "type";
		
		// The message in the interaction (if there is one)
		public static final String MESSAGE = "message";
		
		// The date the message was last updated
		public static final String DATE = "date";
		
		// The path of the image in the interaction (if applicable)
		public static final String IMAGEPATH = "imagepath";
		
		// Whether or not the user has clicked on the notification
		public static final String HASCLICKED = "hasclicked";
		
		// Whether or not the user has done anything with the notification (sent a picture, for example)
		public static final String HASACTED = "hasacted";
		
	}
}
