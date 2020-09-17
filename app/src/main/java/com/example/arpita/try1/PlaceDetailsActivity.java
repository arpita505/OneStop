package com.example.arpita.try1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class PlaceDetailsActivity extends AppCompatActivity {

    TextView nameTv;
    TextView addressTv;
    TextView phoneNoTv;
    TextView websiteTv;
    TextView inPhoneTv;
    TextView vicinityTv;
    TextView latitudeTv;
    TextView longitudeTv;
    TextView ratingTv;
    ScrollView scrollView;
    Button directionBtn;
    String latitude;
    String longitude;
    public static final String MY_PREF_NAME = "SavedLocationDetailsPreference";
    private static final String PLACE_NAME = "Place Name";
    private static final String PLACE_ADDRESS = "Place Address";
    public static final String VICINITY = "Vicinity";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String PHONE_NO = "Phone Number";
    public static final String RATING = "Rating";
    private static final String WEBSITE = "website Name";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        nameTv = (TextView) findViewById(R.id.place_name);
        addressTv = (TextView) findViewById(R.id.address);
        phoneNoTv = (TextView) findViewById(R.id.phone_no);
        websiteTv = (TextView) findViewById(R.id.website);
        inPhoneTv = (TextView) findViewById(R.id.in_phone_no);
        vicinityTv = (TextView) findViewById(R.id.vicinity);
        latitudeTv = (TextView) findViewById(R.id.latitude);
        longitudeTv = (TextView) findViewById(R.id.longitude);
        ratingTv = (TextView) findViewById(R.id.rating);
        scrollView = (ScrollView) findViewById(R.id.place_detail_view);
        directionBtn = (Button)findViewById(R.id.direction_btn);


        if (MainActivity.uri!=null) {
            int imageRes = getResources().getIdentifier(MainActivity.uri, null, getPackageName());
            Drawable res = getResources().getDrawable(imageRes);
           // scrollView.setAlpha(0.5f);
            scrollView.setBackground(res);
        }


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        final String reference = getIntent().getStringExtra("reference");
        String placeId=getIntent().getStringExtra("place_id");

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        if (reference!=null) {
            sb.append("reference=" + reference);
        }else {
            Log.e("The ERROR IS: ", placeId);
            sb.append("placeid="+placeId);
        }
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyC3_pYW7LjjCCwYVt7O5Dr6dIBaWjtO5yk");

        PlacesTask placesTask = new PlacesTask();

        placesTask.execute(sb.toString());

        directionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(latitude!=null) {
                    Intent directionIntent = new Intent(getApplicationContext(), MapsActivity.class);
                    directionIntent.putExtra("Place Latitude", latitude);
                    directionIntent.putExtra("Place Longitude", longitude);
                    directionIntent.putExtra("reference",reference);
                    startActivity(directionIntent);

                }
                else
                    Toast.makeText(PlaceDetailsActivity.this,"Wait for a while",Toast.LENGTH_SHORT).show();
            }
        });

    };

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        }catch(Exception e){
            Log.d("Exceptiondownloadingurl", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        ProgressBar p;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = (ProgressBar)findViewById(R.id.progressBar2);
            if (p.getVisibility()!= View.VISIBLE){
                p.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
            p.setVisibility(View.INVISIBLE);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, HashMap<String,String>>{

        JSONObject jObject;

        @Override
        protected HashMap<String,String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                hPlaceDetails = placeDetailsJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return hPlaceDetails;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> hPlaceDetails){

            String name = hPlaceDetails.get("name");
            String icon = hPlaceDetails.get("icon");
            String vicinity = hPlaceDetails.get("vicinity");
            String lat = hPlaceDetails.get("lat");
            String lng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            String formatted_phone = hPlaceDetails.get("formatted_phone");
            String website = hPlaceDetails.get("website");
            String rating = hPlaceDetails.get("rating");
            String international_phone_number = hPlaceDetails.get("international_phone_number");
            String url = hPlaceDetails.get("url");

            nameTv.setText(name);
            addressTv.setText(formatted_address);
            latitudeTv.setText(lat);
            longitudeTv.setText(lng);
            phoneNoTv.setText(formatted_phone);
            inPhoneTv.setText(international_phone_number);
            websiteTv.setText(website);
            vicinityTv.setText(vicinity);
            ratingTv.setText(rating);

            latitude = lat;
            longitude = lng;

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREF_NAME,MODE_PRIVATE).edit();
            editor.putString(PLACE_NAME, name);
            editor.putString(PLACE_ADDRESS, formatted_address);
            editor.putString(VICINITY, vicinity);
            editor.putString(LATITUDE, lat);
            editor.putString(LONGITUDE, lng);
            editor.putString(PHONE_NO, international_phone_number);
            editor.putString(RATING, rating);
            editor.putString(WEBSITE, website);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences settings = getSharedPreferences(MY_PREF_NAME,MODE_PRIVATE);
        settings.edit().clear().commit();
        super.onBackPressed();
    }
}
