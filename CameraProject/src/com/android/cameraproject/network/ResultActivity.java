package com.android.cameraproject.network;

import java.io.File;
import com.android.cameraproject.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

// An activity to view images sent to the user
public class ResultActivity extends Activity
{
	private ImageView preview;
	
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_activity_layout);
		
		preview = (ImageView)findViewById(R.id.resultpreview);

		File f = (File) this.getIntent().getSerializableExtra("File");
		Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
		preview.setImageBitmap(b);
	}
}
