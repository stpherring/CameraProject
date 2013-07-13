package com.android.cameraproject.network;

// Represents a list item that can be checked (used when sending messages to friends)
public class CheckableItem 
{
	private String username;
	private boolean isChecked;
	
	public CheckableItem(String username, boolean isChecked)
	{
		this.username = username;
		this.isChecked = isChecked;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public boolean isChecked()
	{
		return isChecked;
	}
	
	public void setChecked(boolean isChecked)
	{
		this.isChecked = isChecked;
	}

}
