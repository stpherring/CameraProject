package com.android.cameraproject.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import com.android.cameraproject.R;
import com.android.cameraproject.network.MainMenuActivity;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

// Preview the picture taken in CameraActivity
public class PreviewActivity extends Activity
{
	private static final String TAG = "PreviewActivity";
	
	private ImageView preview;
	
	private ParseUser user;
	
	private byte[] array;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);
		
		preview = (ImageView)findViewById(R.id.picturepreview);

		File f = (File) this.getIntent().getSerializableExtra("File");
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
		
		// For some reason the picture is always taken in landscape mode during testing.  
		// This checks if  the picture is in landscape and rotates accordingly, but it 
		// skews the picture. I have rework this.
		if(b.getWidth() > b.getHeight())
		{
			Runtime.getRuntime().freeMemory();
			Matrix mat = new Matrix();
			mat.postRotate(90);
			Bitmap c = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), mat, true);
			preview.setImageBitmap(c);
			array = new CompressImageTask().doInBackground(c);
		}
		else
		{
			preview.setImageBitmap(b);
			array = new CompressImageTask().doInBackground(b);
		}
		
		user = ParseUser.getCurrentUser();
	}
	
	public void accept(View v)
	{
		String toUser = this.getIntent().getStringExtra("touser");
		Log.d(TAG, "toUser: " + toUser);
		
		ParseFile file = new ParseFile(array);
		
		final ParseObject ret = new ParseObject("Interaction");
		
		ret.put("type", "result");
		
		ret.put("photo", file);
		
		ret.put("from", user.getUsername());
		
		ret.put("hasSynced", false);
		
		ret.put("to", this.getIntent().getStringExtra("touser"));
		
		ret.saveInBackground();
		
		Intent i = new Intent(PreviewActivity.this, MainMenuActivity.class);
		startActivity(i);
		
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