package com.example.tourfit;
import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.locationtrackingexample.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 *  This class is used to search places using Places API using keywords like police,hospital etc.
 *
 */
public class ShowPlacesActivity extends Activity {

	private final String TAG = getClass().getSimpleName();
	private GoogleMap mMap;
	private String[] places;
	private LocationManager locationManager;
	private Location loc;
	private String typeOfPlace;
	private double radiusNearby;
	private GMapV2Direction md;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_main);
		Bundle extras = getIntent().getExtras();
		typeOfPlace = extras.getString("EXTRA_PLACE");
		radiusNearby = Double.parseDouble(extras.getString("EXTRA_RADIUS"));
		initCompo();
		places = getResources().getStringArray(R.array.places);
		currentLocation();
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
				this, R.array.places, android.R.layout.simple_list_item_1),
				new ActionBar.OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						Log.e(TAG,
								typeOfPlace.toLowerCase().replace("-",
										"_"));
						if (loc != null) {
							mMap.clear();
							new GetPlaces(ShowPlacesActivity.this, typeOfPlace.toLowerCase().replace("-", "_").replace(" ", "_"), radiusNearby).execute();
						}
						return true;
					}

				});

	}	

	private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

		private ProgressDialog dialog;
		private Context context;
		private String places;
		private double radius;

		public GetPlaces(Context context, String places, double radius) {
			this.context = context;
			this.places = places;
			this.radius = radius;
		}

		@Override
		protected void onPostExecute(ArrayList<Place> result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			LatLng cll = new LatLng(loc.getLatitude(), 
            		loc.getLongitude());   
            // see more on Marker: https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/Marker
            mMap.addMarker(new MarkerOptions()
            .position(cll)
            .title("Current Location")
            );
			for (int i = 0; i < result.size(); i++) {
				mMap.addMarker(new MarkerOptions()
						.title(result.get(i).getName())
						.position(
								new LatLng(result.get(i).getLatitude(), result
										.get(i).getLongitude()))
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.pin))
						.snippet(result.get(i).getVicinity()));
				LatLng tempLoc = new LatLng(result.get(i).getLatitude(), result.get(i).getLongitude());
				md = new GMapV2Direction(cll, tempLoc, GMapV2Direction.MODE_WALKING);
				md.execute();
				Document doc = null;
				// Document doc = md.getDocument(cll, zll,
				// GMapV2Direction.MODE_DRIVING);
				try {
					// get the result from the asynctask returned by Google,
					// wait if necessary
					doc = md.get();

					// now process/parse the results from Google
					ArrayList<LatLng> directionPoint = md.getDirection(doc);

					// here, draw the lines based on the direction points
					PolylineOptions rectLine = new PolylineOptions().width(3)
							.color(Color.BLUE);

					for (int j = 0; j < directionPoint.size(); j++) {
						rectLine.add(directionPoint.get(j));
					}
					Polyline polylin = mMap.addPolyline(rectLine);

				} catch (Exception e) {
					// just ignore here, possible exceptions thrown by the
					// md.get() call:
					// see
					// http://developer.android.com/reference/android/os/AsyncTask.html#get()
				}
			}
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(result.get(0).getLatitude(), result
							.get(0).getLongitude())) // Sets the center of the map to
											// Mountain View
					.zoom(12) // Sets the zoom
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder
			mMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Loading..");
			dialog.isIndeterminate();
			dialog.show();
		}

		@Override
		protected ArrayList<Place> doInBackground(Void... arg0) {
			PlacesService service = new PlacesService(
					"AIzaSyB6TgunMbVSC965qiZHCONuO3nHGQeNd4E");
			ArrayList<Place> findPlaces = service.findPlaces(loc.getLatitude(), 
					loc.getLongitude(), places, radius); 

			for (int i = 0; i < findPlaces.size(); i++) {

				Place placeDetail = findPlaces.get(i);
				Log.e(TAG, "places : " + placeDetail.getName());
			}
			return findPlaces;
		}

	}

	private void initCompo() {
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

	private void currentLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String provider = locationManager
				.getBestProvider(new Criteria(), false);

		Location location = locationManager.getLastKnownLocation(provider);

		if (location == null) {
			locationManager.requestLocationUpdates(provider, 0, 0, listener);
		} else {
			loc = location;
			new GetPlaces(ShowPlacesActivity.this, typeOfPlace.toLowerCase().replace(
					"-", "_"), 1000).execute();
			Log.e(TAG, "location : " + location);
		}

	}

	private LocationListener listener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

		@Override
		public void onLocationChanged(Location location) {
			Log.e(TAG, "location update : " + location);
			loc = location;
			locationManager.removeUpdates(listener);
		}
	};

}

