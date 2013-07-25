package com.android.cameraproject.network;


public class ListItem
{
	private String from;
	private String type;
	private String message;
	private String date;
	private String imagepath;
	private int hasClicked;
	private int objectId;
	
	// This variable determines whether or not the user has done anything with the 
	private int hasActed;

	public ListItem(String from, String type, String message, String date, String imagepath, int hasClicked, int objectId, int hasActed) 
	{
		this.from = from;
		this.type = type;
		this.message = message;
		this.date = date;
		this.imagepath = imagepath;
		this.hasClicked = hasClicked;
		this.objectId = objectId;
		this.hasActed = hasActed;
		
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
	
	public int getId()
	{
		return objectId;
	}
	
	public int hasClicked()
	{
		return hasClicked;
	}
	
	public int hasActed()
	{
		return hasActed;
	}
	
	public void setClicked(int hasClicked)
	{
		this.hasClicked = hasClicked;
	}
	
	public void setActed(int hasActed)
	{
		this.hasActed = hasActed;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}

}
