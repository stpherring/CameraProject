package com.android.cameraproject.network;

import java.io.File;
import com.android.cameraproject.R;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

// An activity to view images sent to the user
public class ResultActivity extends Activity
{
	private ImageView preview;
	private TextView text;
	
//	private static final String TAG = "ResultActivity";
	
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_activity_layout);
		
		preview = (ImageView)findViewById(R.id.resultpreview);

		File f = (File) this.getIntent().getSerializableExtra("File");
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
		preview.setImageBitmap(b);
		
		String message = this.getIntent().getStringExtra("message");
		
		text = (TextView)findViewById(R.id.result_textview);
		text.setText(message);
		
		Drawable textBackground = text.getBackground();
		textBackground.setAlpha(150);
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
