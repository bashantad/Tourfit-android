package com.example.locationtrackingexample;
// uses Google's Directions API to return a path
// Link to Google's Directions API: https://developers.google.com/maps/documentation/directions/
// for example, try typing into the browser this URL:
// http://maps.googleapis.com/maps/api/directions/xml?origin=-37.6396884,145.0870123&destination=-37.7838757,144.9515533&sensor=false&units=metric&mode=walking
// based on from http://stackoverflow.com/questions/16262837/how-to-draw-route-in-google-maps-api-v2-from-my-location/16271683#16271683
// but this uses an asynctask to send the HTTP query, otherwise, we get a Network in UI Thread exception!
// S.W. Loke 2013

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
//import android.content.Context;

public class GMapV2Direction extends AsyncTask<LatLng,Void,Document>{
	// have to the HTTP connection in a non-UI thread;
	// so, use an Async Task
	public final static String MODE_DRIVING = "driving";
	public final static String MODE_WALKING = "walking";

	LatLng start,end;
	String m;
	Document doc;
	
	public GMapV2Direction(LatLng sl, LatLng el, String mode) {
		start = sl;
		end = el;
		m = mode;
	}

	@Override
	protected Document doInBackground(LatLng... params) {
		// the LatLng params are just for show...
		return getDocument(start,end,m);
	}
	

    protected void onPostExecute(Document resultdoc) {
        System.out.println("Downloaded doc");
      
        //Thread.sleep(2000);
    }


	
	public Document getDocument(LatLng start, LatLng end, String mode) {
    String url = "http://maps.googleapis.com/maps/api/directions/xml?"
            + "origin=" + start.latitude + "," + start.longitude
            + "&destination=" + end.latitude + "," + end.longitude
            + "&sensor=false&units=metric&mode="+mode;
    Log.d("url", url);
    try {
        HttpClient httpClient = new DefaultHttpClient();
   //     HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(url);
        

        System.out.println("getting the doc!"+url);
        HttpEntity httpEntity;
		// Set some parameters
		HttpParams httpParams = httpClient.getParams();

		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);

		// Execute the call
		HttpResponse httpResponse = httpClient.execute(httpGet);

        System.out.println("getting the doc!");

		// Analyze status response
		StatusLine statusLine = httpResponse.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

	        System.out.println("getting the doc!ok");
			// Get the response
			httpEntity = httpResponse.getEntity();
		//	String response = EntityUtils.toString(httpEntity);
		} else {
			// Unexpected status response

	        System.out.println("getting the doc!not ok"+HttpStatus.SC_OK+
	        		statusLine.getStatusCode());
			throw new IOException(statusLine.getReasonPhrase());
		}
        /*
        HttpResponse response = httpClient.execute(httpGet, localContext);
        */
    	
		
        InputStream in = httpEntity.getContent();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = builder.parse(in);
        System.out.println("got the doc!");
        return doc;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
	}

	public String getDurationText(Document doc) {
    try {

        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "text"));
        Log.i("DurationText", node2.getTextContent());
        return node2.getTextContent();
    } catch (Exception e) {
        return "0";
    }
	}

	public int getDurationValue(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("duration");
        Node node1 = nl1.item(0);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DurationValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    } catch (Exception e) {
        return -1;
    }
	}

	public String getDistanceText(Document doc) {
    /*
     * while (en.hasMoreElements()) { type type = (type) en.nextElement();
     * 
     * }
     */

    try {
        NodeList nl1;
        nl1 = doc.getElementsByTagName("distance");

        Node node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = null;
        nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.d("DistanceText", node2.getTextContent());
        return node2.getTextContent();
    } catch (Exception e) {
        return "-1";
    }

    /*
     * NodeList nl1; if(doc.getElementsByTagName("distance")!=null){ nl1=
     * doc.getElementsByTagName("distance");
     * 
     * Node node1 = nl1.item(nl1.getLength() - 1); NodeList nl2 = null; if
     * (node1.getChildNodes() != null) { nl2 = node1.getChildNodes(); Node
     * node2 = nl2.item(getNodeIndex(nl2, "value")); Log.d("DistanceText",
     * node2.getTextContent()); return node2.getTextContent(); } else return
     * "-1";} else return "-1";
     */
	}

	public int getDistanceValue(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("distance");
        Node node1 = null;
        node1 = nl1.item(nl1.getLength() - 1);
        NodeList nl2 = node1.getChildNodes();
        Node node2 = nl2.item(getNodeIndex(nl2, "value"));
        Log.i("DistanceValue", node2.getTextContent());
        return Integer.parseInt(node2.getTextContent());
    } catch (Exception e) {
        return -1;
    }
    /*
     * NodeList nl1 = doc.getElementsByTagName("distance"); Node node1 =
     * null; if (nl1.getLength() > 0) node1 = nl1.item(nl1.getLength() - 1);
     * if (node1 != null) { NodeList nl2 = node1.getChildNodes(); Node node2
     * = nl2.item(getNodeIndex(nl2, "value")); Log.i("DistanceValue",
     * node2.getTextContent()); return
     * Integer.parseInt(node2.getTextContent()); } else return 0;
     */
	}

	public String getStartAddress(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("start_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    } catch (Exception e) {
        return "-1";
    }

	}

	public String getEndAddress(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("end_address");
        Node node1 = nl1.item(0);
        Log.i("StartAddress", node1.getTextContent());
        return node1.getTextContent();
    } catch (Exception e) {
        return "-1";        
    }
	}
	public String getCopyRights(Document doc) {
    try {
        NodeList nl1 = doc.getElementsByTagName("copyrights");
        Node node1 = nl1.item(0);
        Log.i("CopyRights", node1.getTextContent());
        return node1.getTextContent();
    } catch (Exception e) {
    return "-1";
    }

	}

	public ArrayList<LatLng> getDirection(Document doc) {
    NodeList nl1, nl2, nl3;
    ArrayList<LatLng> listGeopoints = new ArrayList<LatLng>();

    System.out.println("in get dir!");
    nl1 = doc.getElementsByTagName("step");
    System.out.println("in get dir2!");
    if (nl1.getLength() > 0) {
        System.out.println("in get dir3!");
        for (int i = 0; i < nl1.getLength(); i++) {
            Node node1 = nl1.item(i);
            nl2 = node1.getChildNodes();

            Node locationNode = nl2
                    .item(getNodeIndex(nl2, "start_location"));
            nl3 = locationNode.getChildNodes();
            Node latNode = nl3.item(getNodeIndex(nl3, "lat"));
            double lat = Double.parseDouble(latNode.getTextContent());
            Node lngNode = nl3.item(getNodeIndex(nl3, "lng"));
            double lng = Double.parseDouble(lngNode.getTextContent());
            listGeopoints.add(new LatLng(lat, lng));

            locationNode = nl2.item(getNodeIndex(nl2, "polyline"));
            nl3 = locationNode.getChildNodes();
            latNode = nl3.item(getNodeIndex(nl3, "points"));
            ArrayList<LatLng> arr = decodePoly(latNode.getTextContent());
            for (int j = 0; j < arr.size(); j++) {
                listGeopoints.add(new LatLng(arr.get(j).latitude, arr
                        .get(j).longitude));
            }

            locationNode = nl2.item(getNodeIndex(nl2, "end_location"));
            nl3 = locationNode.getChildNodes();
            latNode = nl3.item(getNodeIndex(nl3, "lat"));
            lat = Double.parseDouble(latNode.getTextContent());
            lngNode = nl3.item(getNodeIndex(nl3, "lng"));
            lng = Double.parseDouble(lngNode.getTextContent());
            listGeopoints.add(new LatLng(lat, lng));
        }
    }

    return listGeopoints;
	}

	private int getNodeIndex(NodeList nl, String nodename) {
    for (int i = 0; i < nl.getLength(); i++) {
        if (nl.item(i).getNodeName().equals(nodename))
            return i;
    }
    return -1;
	}

	private ArrayList<LatLng> decodePoly(String encoded) {
    ArrayList<LatLng> poly = new ArrayList<LatLng>();
    int index = 0, len = encoded.length();
    int lat = 0, lng = 0;
    while (index < len) {
        int b, shift = 0, result = 0;
        do {
            b = encoded.charAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
        lat += dlat;
        shift = 0;
        result = 0;
        do {
            b = encoded.charAt(index++) - 63;
            result |= (b & 0x1f) << shift;
            shift += 5;
        } while (b >= 0x20);
        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
        lng += dlng;

        LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
        poly.add(position);
    }
    return poly;
	}
}
