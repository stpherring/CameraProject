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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity
{
	// Intent flag to confirm credentials
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";
	
	// The Intent extra to store password
	public static final String PARAM_PASSWORD = "password";
	
	//The Intent extra to store username
	public static final String PARAM_USERNAME = "username";
	
	// The Intent extra to store authtoken
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
	
	// The tag used to log to adb console
	public static final String TAG = "LoginActivity";
	
	private EditText mPasswordEdit;
	private EditText mUsernameEdit;
	
	private TextView mErrorText;
	private EditText mPasswordConfirmEdit;
	private TextView mTextView;
	private Button mButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.i(TAG, "onCreate(" + savedInstanceState + ")");
		super.onCreate(savedInstanceState);
		
		Parse.initialize(this, "NggiqW5gj6wn7agvpMGXvkBqqO3MIft2g0s7AZIN", "eROwSRr1WWLRLDPMprPQMxbDHMBQjI9WHpRkH7vk");
		
		Log.i(TAG, "loading data from Intent");
		getIntent();
		
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.login_layout);

		mUsernameEdit = (EditText) findViewById(R.id.username_entry);
		mPasswordEdit = (EditText) findViewById(R.id.password_entry);
		mPasswordConfirmEdit = (EditText) findViewById(R.id.confirm_password);
		mTextView = (TextView) findViewById(R.id.create_account_button);
		mButton = (Button) findViewById(R.id.login_button);
		mErrorText = (TextView) findViewById(R.id.login_error_text);


		
	}
	
	public void switchViews(View v)
	{
		
		int visibility = mPasswordConfirmEdit.getVisibility();
		
		if(visibility == View.VISIBLE)
		{
		
			mPasswordConfirmEdit.setVisibility(View.INVISIBLE);
			mTextView.setText("Don't have an account? Click here");
			mButton.setText("Log In");
		}
		else
		{
			mPasswordConfirmEdit.setVisibility(View.VISIBLE);
			mTextView.setText("Already have an account? Click here");
			mButton.setText("Create Account");
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
				Log.d(TAG, "Username must be at least 7 characters");
				mErrorText.setText("Username must be at least 7 characters");
				mErrorText.setVisibility(View.VISIBLE);
				return;
			}
			if(password.length() < 7)
			{
				mErrorText.setText("Password must be at least 7 characters");
				mErrorText.setVisibility(View.VISIBLE);
				return;
			}
			if(!password.equals(confirm_password))
			{
				mErrorText.setText("Password entries do not match");
				mErrorText.setVisibility(View.VISIBLE);
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
					}
					else
					{
						Log.e(TAG, e.getMessage());
						mErrorText.setText("Error signing up");
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
					}
					else
					{
						Log.e(TAG, e.getMessage());
						mErrorText.setText("Error logging in");
					}
				}
			});
		}
		
	}
 
}
