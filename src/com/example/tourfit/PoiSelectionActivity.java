package com.example.tourfit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

import com.example.locationtrackingexample.R;

public class PoiSelectionActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	ArrayList<String> itemList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_selection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.poi_selection, menu);
		return true;
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.poi0:
			if (checked) {
				this.itemList.add("0");
			} else {
				remove();
			}
			break;
		case R.id.poi1:
			if (checked) {
				this.itemList.add("1");
			} else {
				remove();
			}
			break;
		case R.id.poi2:
			if (checked) {
				this.itemList.add("2");
			} else {
				remove();
			}
			break;
		case R.id.poi3:
			if (checked) {
				this.itemList.add("3");
			} else {
				remove();
			}
			break;
		case R.id.poi4:
			if (checked) {
				this.itemList.add("4");
			} else {
				remove();
			}
			break;
		case R.id.poi5:
			if (checked) {
				this.itemList.add("5");
			} else {
				remove();
			}
			break;
		}
	}
	
	public void showOnMap(View view)
	{
		Intent intent = new Intent(this, ShowPoiActivity.class);
		String message = "";
		for(String s: this.itemList){
			message += s + ",";
		}
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	public void remove()
	{
		this.itemList.remove(this.itemList.size()-1);
	}
}
