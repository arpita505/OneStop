package com.example.arpita.try1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.arpita.try1.db.SavedLocationsTable;
import com.example.arpita.try1.model.SavedLocations;

/**
 * Created by Chandan Suri on 9/30/2016.
 */
public class DbOpener extends SQLiteOpenHelper {

    public static final String DB_NAME = "databaseLocations";
    public static final int DB_VER = 1;

    public static DbOpener dbOpener = null;

    public static SQLiteDatabase openReadableDatabase(Context c){
        if(dbOpener==null){
            dbOpener=new DbOpener(c);
        }
        return dbOpener.getReadableDatabase();
    }

    public static SQLiteDatabase openWritableDatabase(Context c){
        if(dbOpener==null){
            dbOpener=new DbOpener(c);
        }
        return dbOpener.getWritableDatabase();
    }

    public DbOpener(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SavedLocationsTable.TABLE_CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
