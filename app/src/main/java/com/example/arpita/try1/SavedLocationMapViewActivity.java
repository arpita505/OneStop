package com.example.arpita.try1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;

public class SavedLocationMapViewActivity extends AppCompatActivity {

    ImageView savedLocationMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_location_map_view);

        savedLocationMapView = (ImageView) findViewById(R.id.saved_loc_map_view);
        Intent mapIntent = getIntent();
        String imagePath = mapIntent.getStringExtra("IMAGE PATH");
        File imageFile = new File(imagePath);
        openScreenshot(imageFile);
    }
    public void openScreenshot(File imageFile){

        Bitmap myBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        savedLocationMapView.setImageBitmap(myBitmap);
    }
}
