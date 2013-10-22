package com.example.locationtrackingexample;
// Cobbled together from http://www.truiton.com/2013/05/android-mapfragment-example,
// from http://stackoverflow.com/questions/16262837/how-to-draw-route-in-google-maps-api-v2-from-my-location/16271683#16271683
// and from http://developer.android.com/training/location/retrieve-current.html
// S.W. Loke 2013
// Tested on a Nexus 7 

import java.util.ArrayList;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackMyLocationActivity extends Activity  implements
   GooglePlayServicesClient.ConnectionCallbacks,
   GooglePlayServicesClient.OnConnectionFailedListener {
	
	  // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    GoogleMap mMap;
    GMapV2Direction md;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_main);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                 .getMap();
		

		// shows the get my location button on top right hand corner
		// works but I will do it myself, see getLocation(..)
		//mMap.setMyLocationEnabled(true); 	
		
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_main, menu);
		return true;
	}
	
    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();

    }
	
    /*
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
          //  stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
    }
    
    
    
    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
            }
            return false;
        }
    }

    
    
    /**
     * Invoked by the "Get Location" button.
     *
     * Calls getLastLocation() to get the current location
     *
     * @param v The view object associated with this method, in this case a Button.
     */
    public void getLocation(View v) {

        // If Google Play Services is available
        if (servicesConnected()) {

            // Get the current location
            Location currentLocation = mLocationClient.getLastLocation();

            // show location in std out
            System.out.println("GPS coordinates:"+currentLocation.getLatitude()+", "+
            currentLocation.getLongitude());
           
            
            // show position as a marker
            LatLng cll = new LatLng(currentLocation.getLatitude(), 
            		currentLocation.getLongitude());   
            // see more on Marker: https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/Marker
            mMap.addMarker(new MarkerOptions()
            .position(cll)
            .title("Here I am!")
            );
      
            // set location in the middle of the map, zoom level 11
            // for details, see https://developers.google.com/maps/documentation/android/views
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cll, 11));
            
            
            //just for fun, show path from current location to the Melbourne Zoo
            // Melbourne zoo has coordinates: 144.9515533 , -37.7838757
            // see https://developers.google.com/maps/documentation/android/shapes
            // for marker configuration:
            // see https://developers.google.com/maps/documentation/android/reference/com/google/android/gms/maps/model/BitmapDescriptor
            LatLng zll = new LatLng(-37.7838757, 144.9515533);    
            mMap.addMarker(new MarkerOptions()
            .position(zll)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .title("Melbourne Zoo")
            );
            // create the path/line
            PolylineOptions pathToZoo = new PolylineOptions()
            .add(cll)
            .add(zll)
            .width(3)
            .color(Color.BLUE)
            .geodesic(true);
            // add the path/line to the map
            Polyline polyline = mMap.addPolyline(pathToZoo);
            
            //draw a walking path from current location to the Melbourne zoo
            md = new GMapV2Direction(cll, zll, GMapV2Direction.MODE_WALKING);
     
            // also can get the driving path
            // md = new GMapV2Direction(cll, zll, GMapV2Direction.MODE_DRIVING)
  
            // fire an asynctask with an instance of GMapV2direction to ask Google for
            // directions
            md.execute();
            
            Document doc=null;
           // Document doc = md.getDocument(cll, zll,
             //       GMapV2Direction.MODE_DRIVING);
            try {
            	// get the result from the asynctask returned by Google,
            	// wait if necessary
	            doc = md.get();
	            
	            // now process/parse the results from Google 
	            ArrayList<LatLng> directionPoint = md.getDirection(doc);
	            
	            // here, draw the lines based on the direction points
	            PolylineOptions rectLine = new PolylineOptions().width(3).color(
	                    Color.RED);
	
	            for (int i = 0; i < directionPoint.size(); i++) {
	                rectLine.add(directionPoint.get(i));
	            }
	            Polyline polylin = mMap.addPolyline(rectLine);
            
            } catch (Exception e) {
            	// just ignore here, possible exceptions thrown by the md.get() call:
            	// see http://developer.android.com/reference/android/os/AsyncTask.html#get()
            }
            
            // show current coordinates,etc in a toast
            Context context = getApplicationContext();
            CharSequence text = "Path to walk to the Melb zoo from GPS coordinates:"
            		+currentLocation.getLatitude()+", "
            		+currentLocation.getLongitude();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            // Display the current location in the UI
            //mLatLng.setText(LocationUtils.getLatLng(this, currentLocation));
        }
    }
    
    
    
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
      //  mConnectionStatus.setText(R.string.connected);

        //if (mUpdatesRequested) {
        //    startPeriodicUpdates();
       // }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
       // mConnectionStatus.setText(R.string.disconnected);
    }
    
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
        }
    }

    
    
    

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


}

