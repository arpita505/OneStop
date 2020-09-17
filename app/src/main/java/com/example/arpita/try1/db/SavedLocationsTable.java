package com.example.arpita.try1.db;

/**
 * Created by Chandan Suri on 9/30/2016.
 */
public class SavedLocationsTable extends DbTable{

    public static final String TABLE_NAME="SavedLocations";

    public interface Columns{
        String ID = "id";
        String NAME = "place_name";
        String ADDRESS = "address";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String PHONE_NO = "international_phone_no";
        String VICINITY = "vicinity";
        String RATING = "rating";
        String WEBSITE = "website";
        String IMAGE_PATH="image_path";
    }

    public static final String TABLE_CREATE_CMD = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
            +LBR
            +Columns.ID+TYPE_INT_PK+COMMA
            +Columns.NAME+TYPE_TEXT+COMMA
            +Columns.ADDRESS+TYPE_TEXT+COMMA
            +Columns.LATITUDE+TYPE_TEXT+COMMA
            +Columns.LONGITUDE+TYPE_TEXT+COMMA
            +Columns.PHONE_NO+TYPE_TEXT+COMMA
            +Columns.VICINITY+TYPE_TEXT+COMMA
            +Columns.RATING+TYPE_TEXT+COMMA
            +Columns.WEBSITE+TYPE_TEXT+COMMA
            +Columns.IMAGE_PATH+TYPE_TEXT
            +RBR+" ; ";
    public static final String DELETE = "DELETE FROM "+TABLE_NAME+" WHERE "+Columns.ID+" = ";
    public static final String TABLE_SELECT_ALL="SELECT * FROM "+TABLE_NAME+" ORDER BY "+Columns.ID+" DESC "+"; ";
    public static final String TABLE_SELECT_ONE="SELECT * FROM "+TABLE_NAME+" WHERE "+Columns.ID+" = ";

}
