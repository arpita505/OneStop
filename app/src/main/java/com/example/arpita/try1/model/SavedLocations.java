package com.example.arpita.try1.model;

import java.util.Date;

/**
 * Created by Chandan Suri on 9/30/2016.
 */
public class SavedLocations {

    int id;
    String name;
    String address;
    String latitude;
    String longitude;
    String phoneNo;
    String vicinity;
    String rating;
    String imagePath;
    String websiteName;

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getAddress(){
        return address;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getPhoneNo(){
        return phoneNo;
    }
    public String getVicinity(){
        return vicinity;
    }
    public String getRating(){
        return rating;
    }
    public String getImagePath(){
        return  imagePath;
    }
    public String getWebsiteName(){
        return websiteName;
    }

    public SavedLocations(int id, String name, String address, String latitude, String longitude,
                          String phoneNo, String vicinity, String rating, String imagePath, String websiteName){

        this.id = id;
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.phoneNo = phoneNo;
        this.vicinity = vicinity;
        this.rating = rating;
        this.imagePath = imagePath;
        this.websiteName = websiteName;
    }
}
