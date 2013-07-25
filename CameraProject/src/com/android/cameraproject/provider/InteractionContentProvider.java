package com.android.cameraproject.provider;

import java.util.HashMap;

import com.android.cameraproject.provider.Interaction.Interactions;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class InteractionContentProvider extends ContentProvider
{

	// private static final String TAG = "InteractionContentProvider";
	
	private static final String DATABASE_NAME = "interactions.db";
	
	private static final int DATABASE_VERSION = 1;
	
	private static final String INTERACTIONS_TABLE_NAME = "interactions";

	public static final String AUTHORITY = "com.android.cameraproject.provider.InteractionContentProvider";
	
	private static final UriMatcher sUriMatcher;
	
	private static final int INTERACTIONS = 1;
	
	private static final int INTERACTIONS_ID = 2;
	
	private static HashMap<String, String> interactionsProjectionMap;
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL("CREATE TABLE " + INTERACTIONS_TABLE_NAME + " ( "
					+ Interactions.INTERACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Interactions.FROMUSER + " VARCHAR(255), " + Interactions.TOUSER
					+ " VARCHAR(255), " + Interactions.TYPE + " VARCHAR(255), " 
					+ Interactions.MESSAGE + " VARCHAR(255), " + Interactions.DATE
					+ " VARCHAR(255), " + Interactions.IMAGEPATH + " VARCHAR(255), "
					+ Interactions.HASCLICKED + " INTEGER, " + Interactions.HASACTED + " INTEGER );");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + INTERACTIONS_TABLE_NAME);
			onCreate(db);
		}
	}
	
	private DatabaseHelper dbHelper;
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) 
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		switch(sUriMatcher.match(uri))
		{
			case INTERACTIONS: 
				break;
			case INTERACTIONS_ID:
				where = where + "_id = " + uri.getLastPathSegment();
				break;
			default:
				// Log.e(TAG, "Unknown URI " + uri);
				throw new IllegalArgumentException();
		}
		
		int count = db.delete(INTERACTIONS_TABLE_NAME, where, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) 
	{
		switch(sUriMatcher.match(uri))
		{
			case INTERACTIONS:
				return Interactions.CONTENT_TYPE;
			default:
				// Log.e(TAG, "Unknown URI " + uri);
				throw new IllegalArgumentException();
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) 
	{
		if(sUriMatcher.match(uri) != INTERACTIONS)
		{
			// Log.e(TAG, "Unknown URI " + uri);
			throw new IllegalArgumentException();
		}
		
		ContentValues values;
		
		if(initialValues != null)
		{
			values = new ContentValues(initialValues);
		}
		else
		{
			values = new ContentValues();
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insert(INTERACTIONS_TABLE_NAME, Interactions.FROMUSER, values);
		
		if(rowId > 0)
		{
			Uri interactionUri = ContentUris.withAppendedId(Interactions.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(interactionUri, null);
			return interactionUri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() 
	{
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(INTERACTIONS_TABLE_NAME);
		qb.setProjectionMap(interactionsProjectionMap);
		
		switch(sUriMatcher.match(uri))
		{
			case INTERACTIONS:
				break;
			case INTERACTIONS_ID:
				selection = selection + "_id = " + uri.getLastPathSegment();
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) 
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		switch(sUriMatcher.match(uri))
		{
			case INTERACTIONS:
				count = db.update(INTERACTIONS_TABLE_NAME, values, where, whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		// Log.d(TAG, count + "");
		return count;
	}
	
	static
	{
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, INTERACTIONS_TABLE_NAME, INTERACTIONS);
		sUriMatcher.addURI(AUTHORITY, INTERACTIONS_TABLE_NAME + "/#", INTERACTIONS_ID);
		
		interactionsProjectionMap = new HashMap<String, String>();
		interactionsProjectionMap.put(Interactions.INTERACTION_ID, Interactions.INTERACTION_ID);
		interactionsProjectionMap.put(Interactions.FROMUSER, Interactions.FROMUSER);
		interactionsProjectionMap.put(Interactions.TOUSER, Interactions.TOUSER);
		interactionsProjectionMap.put(Interactions.TYPE, Interactions.TYPE);
		interactionsProjectionMap.put(Interactions.MESSAGE, Interactions.MESSAGE);
		interactionsProjectionMap.put(Interactions.DATE, Interactions.DATE);
		interactionsProjectionMap.put(Interactions.IMAGEPATH, Interactions.IMAGEPATH);
		interactionsProjectionMap.put(Interactions.HASCLICKED, Interactions.HASCLICKED);
		interactionsProjectionMap.put(Interactions.HASACTED, Interactions.HASACTED);
	}

}
