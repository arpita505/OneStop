package com.example.arpita.try1;

/**
 * Created by Chandan on 09-08-2016.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arpita.try1.db.SavedLocationsTable;
import com.example.arpita.try1.model.SavedLocations;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap map;
    String latitude;
    String longitude;
    LatLng currLoc;
    float[] results;
    public String reference;
    public static int databaseEntries = 0;

    final String TAG = "PathGoogleMapActivity";
    public static final String MY_PREF_NAME = "SavedLocationDetailsPreference";
    private static final String PLACE_NAME = "Place Name";
    private static final String PLACE_ADDRESS = "Place Address";
    public static final String VICINITY = "Vicinity";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String PHONE_NO = "Phone Number";
    public static final String RATING = "Rating";
    private static final String WEBSITE = "website Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Initializing array List

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        fm.getMapAsync(this);

        Intent intent = getIntent();
        latitude = intent.getStringExtra("Place Latitude");
        longitude = intent.getStringExtra("Place Longitude");
        reference = intent.getStringExtra("reference");
        currLoc = MainActivity.currentLocation;
        //currLoc = new LatLng(30.3516232,76.3643300);

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

        map = googleMap;
        map.clear();


        //noinspection MissingPermission
        map.setMyLocationEnabled(true);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        LatLng point = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        MarkerOptions options = new MarkerOptions();
        MarkerOptions options1 = new MarkerOptions();

        options.position(point);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Destination");
        options1.position(currLoc);
        options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("Your Location");


        Marker dest = map.addMarker(options);
        Marker origin = map.addMarker(options1);
        builder.include(dest.getPosition());
        builder.include(origin.getPosition());
        LatLngBounds bounds = builder.build();

        int padding=100;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);
        map.animateCamera(cu);


        String url = getDirectionsUrl(currLoc, point);

        if (url!=null){
        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);
        getDistance(currLoc,point);
        }else{
            Toast.makeText(MapsActivity.this, "Sorry! No path found between the 2 provided locations!!", Toast.LENGTH_SHORT).show();
        }

        getNotification();
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
                lineOptions.color(getResources().getColor(R.color.blue_light));
            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

    public void getDistance(LatLng currLoc,LatLng point){

        View v=new View(MapsActivity.this);
        results = new float[1];
        Location.distanceBetween(currLoc.latitude, currLoc.longitude,
                point.latitude, point.longitude, results);
        TextView distanceTv = (TextView) findViewById(R.id.distance_tv);
        distanceTv.setText("The Distance Between the two Locations is : "+results[0]/1000+" kms ");

    }

    public void getNavigation(View view){

        Intent intentNavigate = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+latitude+","+longitude));
        startActivity(intentNavigate);

    }
    public void getNotification(){

        Intent intent = new Intent(getApplicationContext(),PlaceDetailsActivity.class);
        intent.putExtra("reference",reference);
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
            Toast.makeText(MapsActivity.this, "The Notificaton could not be shown!!", Toast.LENGTH_SHORT).show();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(1,notification);

    }

    public void saveToDatabase(View view){
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREF_NAME, MODE_PRIVATE);
        String placeName, placeAddress, vicinity, phoneNo;
        String latitude, longitude, rating,websiteName;
        String imagePath=null;

        placeName = sharedPreferences.getString(PLACE_NAME, "No Name of the Place!");
        placeAddress = sharedPreferences.getString(PLACE_ADDRESS,"No Address Found!");
        vicinity = sharedPreferences.getString(VICINITY, "No Further Information");
        phoneNo = sharedPreferences.getString(PHONE_NO,"No Available Phone Number Found!");
        latitude = sharedPreferences.getString(LATITUDE,"Nothing Found!");
        longitude = sharedPreferences.getString(LONGITUDE,"Nothing Found!");
        rating = sharedPreferences.getString(RATING,"No Ratings Found!");
        websiteName = sharedPreferences.getString(WEBSITE,"No Website Link Found!");
        Bitmap bitmap = takeScreenshot();
        imagePath = saveBitmap(bitmap);
        Toast.makeText(MapsActivity.this, imagePath, Toast.LENGTH_SHORT).show();

        SQLiteDatabase myDb =DbOpener.openWritableDatabase(this);
        databaseEntries = databaseEntries + 1;

        SavedLocations loc = new SavedLocations(databaseEntries, placeName, placeAddress, latitude, longitude,
                phoneNo, vicinity, rating, imagePath , websiteName);

        ContentValues value = new ContentValues();
        value.put(SavedLocationsTable.Columns.NAME, loc.getName());
        value.put(SavedLocationsTable.Columns.ADDRESS, loc.getAddress());
        value.put(SavedLocationsTable.Columns.VICINITY, loc.getVicinity());
        value.put(SavedLocationsTable.Columns.PHONE_NO, loc.getPhoneNo());
        value.put(SavedLocationsTable.Columns.LATITUDE, loc.getLatitude());
        value.put(SavedLocationsTable.Columns.LONGITUDE, loc.getLongitude());
        value.put(SavedLocationsTable.Columns.RATING, loc.getRating());
        value.put(SavedLocationsTable.Columns.WEBSITE, loc.getWebsiteName());
        value.put(SavedLocationsTable.Columns.ID, loc.getId());
        value.put(SavedLocationsTable.Columns.IMAGE_PATH, loc.getImagePath());

        myDb.insert(SavedLocationsTable.TABLE_NAME, null, value);

    }

    public Bitmap takeScreenshot(){

        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public String saveBitmap(Bitmap bitmap){
        //Date date = new Date();
        String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+ "/screenshot.png";
        File mFile = new File(imagePath);
        FileOutputStream outputStream;
        try
        {
            outputStream = new FileOutputStream(mFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

}



