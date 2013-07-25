package com.android.cameraproject.network;

import com.android.cameraproject.R;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity
{	
	// The tag used to log to adb console
	public static final String TAG = "LoginActivity";
	
	private ImageView mImageView;
	
	private EditText mPasswordEdit;
	private EditText mUsernameEdit;
	private EditText mPasswordConfirmEdit;
	private TextView mTextView;
	private Button mButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// Log.i(TAG, "onCreate(" + savedInstanceState + ")");
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this, "NggiqW5gj6wn7agvpMGXvkBqqO3MIft2g0s7AZIN", "eROwSRr1WWLRLDPMprPQMxbDHMBQjI9WHpRkH7vk");
		
		// Log.i(TAG, "loading data from Intent");
		getIntent();
		
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.login_layout);

		mImageView = (ImageView) findViewById(R.id.login_icon);
		
		mUsernameEdit = (EditText) findViewById(R.id.username_entry);
		mPasswordEdit = (EditText) findViewById(R.id.password_entry);
		mPasswordConfirmEdit = (EditText) findViewById(R.id.confirm_password);
		mTextView = (TextView) findViewById(R.id.create_account_button);
		mButton = (Button) findViewById(R.id.login_button);

		
	}
	
	public void switchViews(View v)
	{
		
		int visibility = mPasswordConfirmEdit.getVisibility();
		final Animation up = AnimationUtils.loadAnimation(this, R.anim.alpha_up);
		
		final Animation down = AnimationUtils.loadAnimation(this, R.anim.alpha_down);
		
		if(visibility == View.VISIBLE)
		{
		
			up.setAnimationListener(new AnimationListener()
			{

				@Override
				public void onAnimationEnd(Animation arg0) 
				{
					
					mPasswordConfirmEdit.setVisibility(View.GONE);
					mTextView.startAnimation(down);
					mButton.startAnimation(down);
					mUsernameEdit.startAnimation(down);
					mPasswordEdit.startAnimation(down);
					mImageView.startAnimation(down);
					
					mTextView.setText("Don't have an account? Touch here");
					mButton.setText("// Log In");

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					
				}

				@Override
				public void onAnimationStart(Animation arg0) 
				{
					
				}
				
			});
			
			mUsernameEdit.startAnimation(up);
			mPasswordEdit.startAnimation(up);
			mButton.startAnimation(up);
			mTextView.startAnimation(up);
			mPasswordConfirmEdit.startAnimation(up);
			mImageView.startAnimation(up);
		}
		else
		{
			up.setAnimationListener(new AnimationListener()
			{

				@Override
				public void onAnimationEnd(Animation arg0) 
				{
					
					mPasswordConfirmEdit.setVisibility(View.VISIBLE);
					mPasswordConfirmEdit.startAnimation(down);
					mTextView.startAnimation(down);
					mButton.startAnimation(down);
					mUsernameEdit.startAnimation(down);
					mPasswordEdit.startAnimation(down);
					
					mTextView.setText("Already have an account? Touch here");
					
					mButton.setText("Create Account");

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					
				}

				@Override
				public void onAnimationStart(Animation arg0) 
				{
					
				}
				
			});
			
			mImageView.startAnimation(up);
			mUsernameEdit.startAnimation(up);
			mPasswordEdit.startAnimation(up);
			mButton.startAnimation(up);
			mTextView.startAnimation(up);
			
			
		}		
	}
	

    /*
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication. The button is configured to call
     * handleLogin() in the layout XML.
     *
     * @param view The Submit button for which this method is invoked
     */
	public void handleLogin(View view)
	{
		String username = mUsernameEdit.getText().toString();
		String password = mPasswordEdit.getText().toString();
		
		// If we want to create an account
		if(mPasswordConfirmEdit.getVisibility() == View.VISIBLE)
		{
			ParseUser user = new ParseUser();
			String confirm_password = mPasswordConfirmEdit.getText().toString();
			
			// Set TextView for each of these
			if(username.length() < 7)
			{
				// Log.d(TAG, "Username must be at least 7 characters");
				Toast.makeText(this, "Username must be at least 7 characters", Toast.LENGTH_LONG).show();
				return;
			}
			if(password.length() < 7)
			{
				Toast.makeText(this, "Password must be at least 7 characters", Toast.LENGTH_LONG).show();
				return;
			}
			if(!password.equals(confirm_password))
			{
				Toast.makeText(this, "Password entries do not match", Toast.LENGTH_LONG).show();
				return;
			}
			
			user.setUsername(username);
			user.setPassword(password);

			ParseACL acl = new ParseACL();
			acl.setPublicReadAccess(true);
			user.put("ACL", acl);

			user.signUpInBackground(new SignUpCallback()
			{

				@Override
				public void done(ParseException e) 
				{
					if(e == null)
					{
						Intent i = new Intent(LoginActivity.this, MainMenuActivity.class);
						startActivity(i);
						finish();
					}
					else
					{
						// Log.e(TAG, e.getMessage());
						Toast.makeText(LoginActivity.this, "Error signing up", Toast.LENGTH_LONG).show();
					}
					
				}
				
			});
		}
		
		// If we want to log in with an account
		else
		{
			ParseUser.logInInBackground(username, password, new LogInCallback()
			{
	
				@Override
				public void done(ParseUser user, ParseException e) 
				{
					if(e == null)
					{
						Intent i = new Intent(LoginActivity.this, MainMenuActivity.class);
						startActivity(i);
						finish();
					}
					else
					{
						// Log.e(TAG, e.getMessage());
						Toast.makeText(LoginActivity.this, "Error logging in", Toast.LENGTH_LONG).show();
					}
				}
			});
		}
		
	}
 
}
