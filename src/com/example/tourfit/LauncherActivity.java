package com.example.tourfit; 

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.locationtrackingexample.R;

public class LauncherActivity extends Activity {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		
		
	}

	/*
	 * call the poi selection screen
	 */
    public void selectPois(View v)
    {
    	Intent intent = new Intent(this, PoiSelectionActivity.class);
    	startActivity(intent);
    }

    /*
     * handles search of particular places like cafe, atm, hospital etc
     * uses google places api
     */
    public void searchPlaces(View v)
    {
    	Intent intent = new Intent(this, ShowPlacesActivity.class);
    	EditText searchType = (EditText) findViewById(R.id.search_type);
    	EditText searchRadius = (EditText) findViewById(R.id.search_radius);
    	String typeText = searchType.getText().toString();
    	String radiusText = searchRadius.getText().toString();
    	Bundle extras = new Bundle();
    	extras.putString("EXTRA_PLACE",typeText);
    	extras.putString("EXTRA_RADIUS",radiusText);
    	intent.putExtras(extras);
    	startActivity(intent);
    }
}

