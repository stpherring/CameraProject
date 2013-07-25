package com.android.cameraproject.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.cameraproject.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;

// The activity to take a picture
public class CameraActivity extends Activity 
{
	// private static final String TAG = "CameraActivity";
	
	private Camera mCamera;
	private Camera.Parameters params;
	private CameraPreview mPreview;
	
	private Button captureButton;
	private static int exif;
	private FrameLayout preview;
	private int objectid;
	private String message;
	
	private CheckBox flashCheckBox;
	
	private PictureCallback mPicture = new PictureCallback()
	{
		@Override
		public void onPictureTaken(byte[] data, Camera camera) 
		{
			
			File pictureFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					, "temp.png");
			
			try
			{
				String to = CameraActivity.this.getIntent().getStringExtra("touser");
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				Intent i = new Intent(CameraActivity.this, PreviewActivity.class);
				i.putExtra("File", pictureFile);
				// Log.d(TAG, "" + exif);
				i.putExtra("exif", exif);
				i.putExtra("touser", to);
				i.putExtra("objectid", objectid);
				i.putExtra("message", message);
				
				startActivity(i);
			}
			catch (FileNotFoundException e)
			{
	            // Log.d("PictureCallback", "File not found: " + e.getMessage());
	        } 
			
			catch (IOException e)
	        {
	            // Log.d("PictureCallback", "Error accessing file: " + e.getMessage());
	        }
			
		}
	};
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		objectid = this.getIntent().getIntExtra("objectid", -1);
		// Log.d(TAG, objectid + "");
		message = this.getIntent().getStringExtra("request");
		// Log.d(TAG, message);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this, mCamera);
		params = mCamera.getParameters();
		
		params.setPictureFormat(ImageFormat.JPEG);
		
		flashCheckBox = (CheckBox) findViewById(R.id.flash);
		 
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);
		
		
		 Log.d("TAG", info.orientation + " degrees");
		
		int rotation = (360 - info.orientation) % 360;
		
		params.setRotation(rotation);
		
		// If the device doesn't have a flash
		if(!CameraActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
		{
			flashCheckBox.setVisibility(View.GONE);
		}
		else
		{
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
		}
		
		mCamera.setParameters(params);
		
		
		// This sets the preview orientation, not the camera orientation
		setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
		
		preview = (FrameLayout)findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		
		captureButton = (Button)findViewById(R.id.take_picture);
		captureButton.setOnClickListener(
				new OnClickListener() 
				{
					
					@Override
					public void onClick(View v) 
					{
						mCamera.takePicture(null, null, mPicture);
					}
				});
		Drawable d = flashCheckBox.getBackground();
		
		// This is to fix the problem of the image being cut off
		int w = d.getIntrinsicWidth();
		int h = d.getIntrinsicHeight();
		
		flashCheckBox.setWidth(w * 2);
		flashCheckBox.setHeight(h * 2);
		
		flashCheckBox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF))
				{
					params.setFlashMode(Parameters.FLASH_MODE_ON);
				}
				else
				{
					params.setFlashMode(Parameters.FLASH_MODE_OFF);
				}
				mCamera.setParameters(params);
			}
			
		});
	}
	
	public void onPause()
	{
		super.onPause();
		if(mCamera != null)
		{
			mCamera.release();
			mCamera = null;
			preview.removeView(mPreview);
		}
	}
	
	public void onResume()
	{
		super.onResume();
		if(mCamera == null)
		{
			mCamera = getCameraInstance();
			mPreview = new CameraPreview(this, mCamera);
			mCamera.setParameters(params);
			
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
			
			preview.addView(mPreview);
		}
	}
	
	public static Camera getCameraInstance()
	{
		Camera c = null;
		
		try
		{
			c = Camera.open();
		}
		catch(Exception e)
		{
			
		}
		
		return c;
	}
	
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera)
	{
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		
		switch(rotation)
		{
			case Surface.ROTATION_0: degrees = 0; exif = 1; break;
			case Surface.ROTATION_90: degrees = 90; exif = 6; break;
			case Surface.ROTATION_180: degrees = 180; exif = 3; break;
			case Surface.ROTATION_270: degrees = 270; exif = 8; break;
		}
		
		// Log.d(TAG, "Degrees: " + degrees);
		
		int result;
		if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		}
		else
		{
			result = (info.orientation - degrees + 360) % 360;
		}
		
		camera.setDisplayOrientation(result);
	}
    
}


