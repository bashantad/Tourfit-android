package com.example.tourfit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.locationtrackingexample.R;

public class IndoorPositionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indoor_show);
	}
	
	public void firstBuilding(View v)
	{
		Intent intent = new Intent(this, ShowImg.class);
		startActivity(intent);
	}
	
	public void secondBuilding(View v)
	{
		Intent intent = new Intent(this, ShowImg.class);
		startActivity(intent);
	}
	
	public void thirdBuilding(View v)
	{
		Intent intent = new Intent(this, ShowImg.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_list, menu);
		return true;
	}

}
