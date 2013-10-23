package com.example.tourfit;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

import com.example.locationtrackingexample.R;

public class PoiSelectionActivity extends Activity {
	private ArrayList<Poi> poiList;
	private ArrayList<Poi> selectionPoi;

	public PoiSelectionActivity() {
		this.poiList = new ArrayList<Poi>();
		this.selectionPoi = new ArrayList<Poi>();
		Poi p1 = new Poi(-37.7838757, 144.9515533, "Melbourne Zoo");
		Poi p2 = new Poi(-37.818616, 144.957558, "Rialto Tower");
		Poi p3 = new Poi(-37.812649, 144.980925, "Fitzroy Gardens");
		Poi p4 = new Poi(-37.807394, 144.973072,"Royal Exhibition Building and Carlton Gardens");
		Poi p5 = new Poi(-37.815887, 144.94807, "Telstra Dome");
		Poi p6 = new Poi(-37.8200000, 144.9600000, "Crown Casino Melbourne");
		poiList.add(p1);
		poiList.add(p2);
		poiList.add(p3);
		poiList.add(p4);
		poiList.add(p5);
		poiList.add(p6);
	}

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
				this.selectionPoi.add(this.poiList.get(0));
			} else {
				this.selectionPoi.remove(this.selectionPoi.size()-1);
			}
			break;
		case R.id.poi1:
			if (checked) {
				this.selectionPoi.add(this.poiList.get(1));
			} else {
				this.selectionPoi.remove(this.selectionPoi.size()-1);
			}
			break;
		case R.id.poi2:
			if (checked) {
				this.selectionPoi.add(this.poiList.get(2));
			} else {
				this.selectionPoi.remove(this.selectionPoi.size()-1);
			}
			break;
		case R.id.poi3:
			if (checked) {
				this.selectionPoi.add(this.poiList.get(3));
			} else {
				this.selectionPoi.remove(this.selectionPoi.size()-1);
			}
			break;
		case R.id.poi4:
			if (checked) {
				this.selectionPoi.add(this.poiList.get(4));
			} else {
				this.selectionPoi.remove(this.selectionPoi.size()-1);
			}
			break;
		case R.id.poi5:
			if (checked) {
				this.selectionPoi.add(this.poiList.get(5));
			} else {
				this.selectionPoi.remove(this.selectionPoi.size()-1);
			}
			break;
		}
	}
	
	public void showOnMap(View view)
	{
		//if none of the items are selected then display every points
		if(this.selectionPoi.size() == 0){
			for(Poi poi:this.poiList){
				this.selectionPoi.add(poi);
			}
		}
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		Intent intent = new Intent(this, ShowPoiActivity.class);
		Bundle extras = new Bundle();
		extras.putIntegerArrayList("EXTRA_MESSAGE", list);
	    intent.putExtras(extras);
	    startActivity(intent);
	}
}
