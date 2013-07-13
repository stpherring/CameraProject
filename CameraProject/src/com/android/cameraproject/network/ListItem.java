package com.android.cameraproject.network;


public class ListItem
{
	private String from;
	private String type;
	private String message;
	private String date;
	private String imagepath;
	
	private boolean hasClicked;

	public ListItem(String from, String type, String message, String date, String imagepath) 
	{
		this.from = from;
		this.type = type;
		this.message = message;
		this.date = date;
		this.imagepath = imagepath;
		hasClicked = false;
	}
	
	public String getFrom()
	{
		return from;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public String getImagePath()
	{
		return imagepath;
	}
	
	public boolean hasClicked()
	{
		return hasClicked;
	}
	
	public void setClicked(boolean hasClicked)
	{
		this.hasClicked = hasClicked;
	}

}
