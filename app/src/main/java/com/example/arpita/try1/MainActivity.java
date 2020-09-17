package com.example.arpita.try1;

import android.*;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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


public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    private static final int LOCATION_REQ_CODE = 111;
    private static final int REQ_CODE = 1;
    GoogleMap mGoogleMap;
    //Spinner mSprPlaceType;

    FloatingActionButton fabList;
    FloatingActionButton fabFinder;
    Marker m;
    public static LatLng currentLocation;

    public static String uri;
    public static List<HashMap<String, String>> hashMapList = new ArrayList<>();

    LatLng latlng;
    Location newLoc;
    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;

    double mLatitude = 30.3;
    double mLongitude = 76.35;

    int listSize;
    //Button btnFind;
    //ListView list;

    int drawable;
    TextView locationFindTv;
    HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        locationFindTv = (TextView) findViewById(R.id.location_finder);

        locationFindTv.setVisibility(View.INVISIBLE);
        mPlaceType = getResources().getStringArray(R.array.place_type);

        mPlaceTypeName = getResources().getStringArray(R.array.place_type_name);

       //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPlaceTypeName);

       // list=(ListView)findViewById(R.id.list);
        //list.setAdapter(adapter);
       // mSprPlaceType = (Spinner) findViewById(R.id.spr_place_type);

       // mSprPlaceType.setAdapter(adapter);


       // btnFind = (Button) findViewById(R.id.btn_find);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else { // Google Play Services are available

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.view);

            fragment.getMapAsync(this);

            //fragment.getMapAsync(MainActivity.this);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
            }

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


            Criteria criteria = new Criteria();

            String provider = locationManager.getBestProvider(criteria, true);

            Location location = locationManager.getLastKnownLocation(provider);

            // mGoogleMap.setMyLocationEnabled(true);

            Log.w("onCreate: ", provider + "," + location);
         /*   if (location != null) {
                onLocationChanged(location);
               // m.remove();
            }*/


            //locationManager.requestLocationUpdates(provider, 500, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, this);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);




         /*   btnFind.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int selectedPosition = mSprPlaceType.getSelectedItemPosition();
                    String type = mPlaceType[selectedPosition];

                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                    Log.w("LOCATION ", "lat long" + mLatitude + ", " + mLongitude);
                    sb.append("location=" + mLatitude + "," + mLongitude);
                    sb.append("&radius=10000");
                    sb.append("&type=" + type);
                    //sb.append("&sensor=true");
                    sb.append("&key=AIzaSyDAohxosopot613QSmI1q30_AVgkH5zxhk");

                    PlacesTask placesTask = new PlacesTask();

                    placesTask.execute(sb.toString());
                    Toast.makeText(MainActivity.this, "The list size is: " + listSize, Toast.LENGTH_SHORT).show();
                }
            });*/




            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        }
    }




    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(" url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng India = new LatLng(30.3, 76.35);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(India));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
        }
        //onLocationChanged(newLoc);
        mGoogleMap.setMyLocationEnabled(true);
        //mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fabFinder = (FloatingActionButton) findViewById(R.id.fab_location_finder);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (newLoc!=null) {
                    onLocationChanged(newLoc);
                    locationFindTv.setText("Enter The Location To Find!!");
                    locationFindTv.setVisibility(View.INVISIBLE);
                    fabFinder.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(MainActivity.this, "Let it First Track Your Location!!", Toast.LENGTH_SHORT).show();
                }
            }

        });


        fabList = (FloatingActionButton) findViewById(R.id.fab_list);


        fabFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationFindTv.setVisibility(View.VISIBLE);
                fabList.setVisibility(View.INVISIBLE);
            }
        });


        fabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getBaseContext(),ListOfPlaces.class);
                startActivity(intent);
            }
        });

        fabList.setVisibility(View.INVISIBLE);

        //  mGoogleMap.addMarker(new MarkerOptions().position(India).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));



        //  LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnable()) {
            Toast.makeText(MainActivity.this, "GPS not Enabled", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setTitle("Enable GPS")
                    .setMessage("Are you sure you want to enable the GPS?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent(getBaseContext(), PlaceDetailsActivity.class);
                String reference = mMarkerPlaceLink.get(arg0.getId());
                intent.putExtra("reference", reference);
                startActivity(intent);
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                locationFindTv.setVisibility(View.INVISIBLE);
            }
        });



    }


    public void getLocation(View view){

        Intent intent = new Intent(this, PlacesAutoCompleteActivity.class);
        startActivityForResult(intent,REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQ_CODE){
            if (resultCode==RESULT_OK){
                locationFindTv.setText(data.getStringExtra("Location Data"));


                double lat = Double.parseDouble(data.getStringExtra("Lat"));
                double lng = Double.parseDouble(data.getStringExtra("Lng"));

                String placeId = data.getStringExtra("Place Id");
                setMarkerOnLocation(lat,lng,placeId);

                //Toast.makeText(MainActivity.this, data.getStringExtra("Location Data"), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "data of Location "+data.getStringExtra("Lat Lng Data"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setMarkerOnLocation(double lat, double lng, final String placeId){

        mGoogleMap.clear();
        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        final LatLng latLng = new LatLng(lat, lng);

        markerOptions.position(latLng);

        mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10f));
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                uri=null;
                Intent i = new Intent(MainActivity.this,PlaceDetailsActivity.class);
                i.putExtra("place_id",placeId);
                i.putExtra("location",latLng);
                startActivity(i);
                return true;

            }
        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void unsetMarkers(){
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        mGoogleMap.clear();
        unsetMarkers();
        int selectedPosition;
        int id=0;

        selectedPosition = item.getItemId();

         if(selectedPosition==R.id.us)
        {
            Intent i=new Intent(MainActivity.this,About_Us.class);
            startActivity(i);
            return true;
        }else if(selectedPosition==R.id.app)
        {
            Intent i=new Intent(MainActivity.this,AboutApp.class);
            startActivity(i);
            return true;
        }
         else if(selectedPosition==R.id.sav)
         {
             Intent i=new Intent(MainActivity.this,SavedLocationsActivity.class);
             startActivity(i);
             return true;
         }
         else if(selectedPosition==R.id.tra)
         {
             Intent i=new Intent(MainActivity.this,Maps1.class);
             startActivity(i);
             return true;
         }else if (newLoc!=null) {
            fabList.setVisibility(View.VISIBLE);
            locationFindTv.setVisibility(View.INVISIBLE);
            // Handle navigation view item clicks here.
            if (selectedPosition == R.id.airport) {
                id = 0;

                uri = "@drawable/back_airport";
                drawable = R.drawable.airport;
                // Handle the camera action
            } else if (selectedPosition == R.id.atm) {
                id = 1;
                uri = "@drawable/back_atm";
                drawable = R.drawable.atm;

            } else if (selectedPosition == R.id.bank) {
                id = 2;
                uri = "@drawable/back_bank";
                drawable = R.drawable.bank;

            } else if (selectedPosition == R.id.church) {
                id = 4;
                uri = "@drawable/back_church";
                drawable = R.drawable.church;
            } else if (selectedPosition == R.id.bus_station) {
                id = 3;
                uri = "@drawable/back_bus";
                drawable = R.drawable.busstand;

            } else if (selectedPosition == R.id.doctor) {
                id = 5;
                uri = "@drawable/back_doctor";
                drawable = R.drawable.clinic;

            } else if (selectedPosition == R.id.hospital) {
                id = 6;
                uri = "@drawable/back_hospital";
                drawable = R.drawable.hospital;

            } else if (selectedPosition == R.id.mosque) {
                id = 7;
                uri = "@drawable/back_mosque";
                drawable = R.drawable.mosque;
            } else if (selectedPosition == R.id.movie_theatre) {
                id = 8;
                uri = "@drawable/back_theatre";
                drawable = R.drawable.theatre;

            } else if (selectedPosition == R.id.hindu_temple) {
                id = 9;
                uri = "@drawable/back_temple";
                drawable = R.drawable.temple;

            } else if (selectedPosition == R.id.restaurant) {
                id = 10;
                uri = "@drawable/back_restaurant";
                drawable = R.drawable.rest;
            }



            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            // int selectedPosition = mSprPlaceType.getSelectedItemPosition();
            String type = mPlaceType[id];

            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            Log.w("LOCATION ", "lat long" + mLatitude + ", " + mLongitude);
            sb.append("location=" + mLatitude + "," + mLongitude);
            sb.append("&radius=10000");
            sb.append("&type=" + type);
            sb.append("&sensor=true");
            sb.append("&key=AIzaSyC3_pYW7LjjCCwYVt7O5Dr6dIBaWjtO5yk");

            PlacesTask placesTask = new PlacesTask();

            placesTask.execute(sb.toString());
            //Toast.makeText(MainActivity.this, "The list size is: " + listSize, Toast.LENGTH_SHORT).show();
            return true;
        }

      else{

            Toast.makeText(MainActivity.this, "Check your Internet Connection or Wait for a while", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    private class PlacesTask extends AsyncTask<String, Integer, String> {

        Dialog progress;
        String data = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = ProgressDialog.show(MainActivity.this, "Loading","Please Wait");

        }

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
                Log.w("The url is: ", "URL " + url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
            progress.dismiss();
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                Log.w("The data", "is " + jsonData[0]);
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            mGoogleMap.clear();
            onLocationChanged(newLoc);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            hashMapList.clear();
            listSize = list.size();
            for (int i = 0; i < list.size(); i++) {

                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                HashMap<String, String> hmPlace = list.get(i);

                hashMapList.add(i,hmPlace);
                double lat = Double.parseDouble(hmPlace.get("lat"));

                double lng = Double.parseDouble(hmPlace.get("lng"));

                String name = hmPlace.get("place_name");

                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                Marker m = mGoogleMap.addMarker(markerOptions);
                builder.include(m.getPosition());

                mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));

                GroundOverlayOptions map = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(drawable))
                        .position(latLng,120f,120f);
                GroundOverlay imageOverlay = mGoogleMap.addGroundOverlay(map);
            }
            if (list.size()>1) {
                LatLngBounds bounds = builder.build();
                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mGoogleMap.animateCamera(cu);
            }

        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
    */
    @Override
    public void onLocationChanged(Location location) {

       if(m!=null) m.remove();
        newLoc = location;
       // mGoogleMap.clear();


        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();

        latlng = new LatLng(mLatitude, mLongitude);
        currentLocation = latlng;

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
       // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        m=mGoogleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));


        Toast.makeText(this,"Location "+mLatitude+" "+mLongitude,Toast.LENGTH_SHORT).show();



    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public boolean isGPSEnable(){

        LocationManager service = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean enabled=service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return enabled;

    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}