package com.android.cameraproject.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.android.cameraproject.R;
import com.android.cameraproject.network.MainMenuActivity;
import com.android.cameraproject.provider.Interaction.Interactions;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

// Preview the picture taken in CameraActivity
public class PreviewActivity extends Activity
{
	// private static final String TAG = "PreviewActivity";
	
	private int objectid;
	
	private ImageView preview;
	
	private ParseUser user;
	
	private String message;
	
	private byte[] array;

	private Button accept;
	
	private Button reject;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);
		
		objectid = this.getIntent().getIntExtra("objectid", -1);
		
		preview = (ImageView)findViewById(R.id.picturepreview);

		File f = (File) this.getIntent().getSerializableExtra("File");
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
		
		ExifInterface exif = null;
		try 
		{
			exif = new ExifInterface(f.getAbsolutePath());
		} 
		catch (IOException e) 
		{
			// Log.e(TAG, "IOException");
		}
		
		// Log.d(TAG, "Exif: " + exif.getAttribute(ExifInterface.TAG_ORIENTATION));
		
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6"))
		{
			b = rotate(b, 90);
		}
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8"))
		{
			b = rotate(b, 270);
		}
		if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3"))
		{
			b = rotate(b, 180);
		}
		
		preview.setImageBitmap(b);
		
		array = new CompressImageTask().doInBackground(b);
		
		user = ParseUser.getCurrentUser();
		
		message = this.getIntent().getStringExtra("message");
		
		accept = (Button)findViewById(R.id.acceptpicture);
		
		accept.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				// String toUser = PreviewActivity.this.getIntent().getStringExtra("touser");
				// Log.d(TAG, "toUser: " + toUser);
				
				ParseFile file = new ParseFile(array);
				
				final ParseObject ret = new ParseObject("Interaction");
				
				ret.put("type", "result");
				ret.put("photo", file);
				ret.put("from", user.getUsername());
				ret.put("hasSynced", false);
				ret.put("to", PreviewActivity.this.getIntent().getStringExtra("touser"));
				ret.put("message", message);
				
				ret.saveInBackground();
				
				ContentValues values = new ContentValues();
				
				values.put(Interactions.HASACTED, 1);
				
				PreviewActivity.this.getContentResolver()
				.update(Interactions.CONTENT_URI, values,
					   Interactions.TOUSER + " = '" + user.getUsername() + "' AND " +
					   Interactions.INTERACTION_ID + " = " + objectid, null);
				
				Toast.makeText(PreviewActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
				
				Intent i = new Intent(PreviewActivity.this, MainMenuActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
			
		});
		
		reject = (Button)findViewById(R.id.rejectpicture);
		
		reject.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				finish();
			}
			
		});
	}
	
	public static Bitmap rotate(Bitmap bitmap, int degree) 
	{
		
	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();

	    Matrix mtx = new Matrix();
	    mtx.postRotate(degree);

	    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}
	
}

class CompressImageTask extends AsyncTask<Bitmap, Integer, byte[]>
{

	@Override
	protected byte[] doInBackground(Bitmap... b) 
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		b[0].compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}
	
}