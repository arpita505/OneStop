package com.example.arpita.try1;

/**
 * Created by Chandan on 09-08-2016.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maps1 extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map1;
    private static final int REQ_CODE = 1;
    private static final int REQ_CODE1 = 2;

    String to,from;
    TextView toloc1,froloc,toast;
    Button but;
    LatLng currLoc,toloc,fromloc;
    float[] results;

    final String TAG = "PathGoogleMapActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps1);
        // Initializing array List

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map1);
        toloc1=(TextView)findViewById(R.id.location_finder);
        froloc=(TextView)findViewById(R.id.to);
        toast=(TextView)findViewById(R.id.distance_tv);
        but=(Button)findViewById(R.id.but);

        toast.setVisibility(View.INVISIBLE);
        // Getting Map for the SupportMapFragment
        fm.getMapAsync(this);

        currLoc = MainActivity.currentLocation;

    }



    public void getLocation(View view){

        Intent intent = new Intent(this, PlacesAutoCompleteActivity.class);
        startActivityForResult(intent,REQ_CODE);
    }

    public void getLocation1(View view){

        Intent intent = new Intent(this, PlacesAutoCompleteActivity.class);
        startActivityForResult(intent,REQ_CODE1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1){
            if (resultCode==RESULT_OK){


                     to=data.getStringExtra("Location Data");

                    double lat = Double.parseDouble(data.getStringExtra("Lat"));
                    double lng = Double.parseDouble(data.getStringExtra("Lng"));
                     toloc=new LatLng(lat,lng);




            }
        }

        if (requestCode==2) {
            if (resultCode == RESULT_OK) {

                from = data.getStringExtra("Location Data");
                double lat1 = Double.parseDouble(data.getStringExtra("Lat"));
                double lng2 = Double.parseDouble(data.getStringExtra("Lng"));

                fromloc=new LatLng(lat1,lng2);

            }
        }

        toloc1.setText(to);
        froloc.setText(from);
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map1 = googleMap;
        map1.clear();


        //noinspection MissingPermission
        map1.setMyLocationEnabled(true);

        LatLng India = new LatLng(30.3, 76.35);
        map1.moveCamera(CameraUpdateFactory.newLatLng(India));
        map1.animateCamera(CameraUpdateFactory.zoomTo(5));

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toloc == null || fromloc == null)
                    Toast.makeText(Maps1.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                else {
                    map1.clear();
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    //LatLng point = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                    MarkerOptions options = new MarkerOptions();
                    MarkerOptions options1 = new MarkerOptions();
                    toast.setVisibility(View.VISIBLE);

                    options.position(toloc);
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Origin");
                    options1.position(fromloc);
                    options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(" Destination");


                    Marker dest = map1.addMarker(options);
                    Marker origin = map1.addMarker(options1);
                    builder.include(dest.getPosition());
                    builder.include(origin.getPosition());
                    LatLngBounds bounds = builder.build();

                    int padding = 100;
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    map1.animateCamera(cu);


                    String url = getDirectionsUrl(toloc, fromloc);

                    if (url != null) {
                        DownloadTask downloadTask = new DownloadTask();

                        downloadTask.execute(url);
                        getDistance(toloc, fromloc);
                    } else {
                        Toast.makeText(Maps1.this, "Sorry! No path found between the 2 provided locations!!", Toast.LENGTH_SHORT).show();
                    }

                    getNotification();
                }
            }
        });

    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionJsonParser parser = new DirectionJsonParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(20);
                lineOptions.color(Color.DKGRAY);
            }

            // Drawing polyline in the Google Map for the i-th route
            map1.addPolyline(lineOptions);
        }
    }

    public void getDistance(LatLng currLoc,LatLng point){

        View v=new View(Maps1.this);
        results = new float[1];
        Location.distanceBetween(currLoc.latitude, currLoc.longitude,
                point.latitude, point.longitude, results);
        TextView distanceTv = (TextView) findViewById(R.id.distance_tv);
        distanceTv.setText("The Distance Between the two Locations is : "+results[0]/1000+" kms ");

    }

    public void getNotification(){

        Intent intent = new Intent(getApplicationContext(),Maps1.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,intent,0);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("The Distance Between the Locations")
                    .setContentText("Distance is => "+results[0]/1000+" kms")
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.arrow_up_float,"Go Over!",pendingIntent)
                    .setSmallIcon(R.drawable.icon_app)
                    .build();
        }else {
            Toast.makeText(Maps1.this, "The Notificaton could not be shown!!", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1,notification);

    }
}



