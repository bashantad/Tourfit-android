package com.example.tourfit;

public class Poi {
	private double latitude;
	private double longitude;
	private String title;
	public Poi(double latitude, double longitude, String title)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
	}
	public double getLatitude()
	{
		return this.latitude;
	}
	
	public double getLongitude()
	{
		return this.longitude;
	}
	
	public String getTitle()
	{
		return this.title;
	}
}
