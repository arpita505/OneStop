package com.example.arpita.try1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arpita.try1.db.SavedLocationsTable;
import com.example.arpita.try1.model.SavedLocations;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SavedLocationsActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_locations);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.list_saved_locations);

        showList();
    }

    private class SavedLocationsListAdapter extends BaseAdapter {

        class SavedLocationViewHolder{
            TextView NameView;
            TextView AddressView;
        }
        private ArrayList<SavedLocations> savedLocationsArrayList;

        public SavedLocationsListAdapter(ArrayList<SavedLocations> savedLocationsArrayList){

            this.savedLocationsArrayList = savedLocationsArrayList;
        }
        @Override
        public int getCount() {
            return savedLocationsArrayList.size();
        }

        @Override
        public SavedLocations getItem(int position) {
            return savedLocationsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();
            SavedLocationViewHolder savedLocationViewHolder;

            if (convertView == null){
                convertView = layoutInflater.inflate(R.layout.saved_location_list_item, null);
                savedLocationViewHolder = new SavedLocationViewHolder();
                savedLocationViewHolder.NameView = (TextView) convertView.findViewById(R.id.saved_loc_name);
                savedLocationViewHolder.AddressView = (TextView) convertView.findViewById(R.id.saved_loc_address);
                convertView.setTag(savedLocationViewHolder);
            }
            else {
                savedLocationViewHolder =(SavedLocationViewHolder) convertView.getTag();
            }

            SavedLocations thisLocation = getItem(position);

            savedLocationViewHolder.NameView.setText(thisLocation.getName());
            savedLocationViewHolder.AddressView.setText(thisLocation.getAddress());
            return convertView;
        }
    }

    public ArrayList<SavedLocations> getList(){

        ArrayList<SavedLocations> locationsArrayList = new ArrayList<>();
        SQLiteDatabase myDb =DbOpener.openWritableDatabase(this);

        Cursor c1;
        c1 = myDb.rawQuery(SavedLocationsTable.TABLE_SELECT_ALL, null);

        if (c1!=null){
            if (c1.moveToFirst()){
                do{
                    String placeName = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.NAME));
                    String placeAddress = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.ADDRESS));
                    String vicinity = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.VICINITY));
                    String latitude = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.LATITUDE));
                    String longitude = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.LONGITUDE));
                    String phoneNo = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.PHONE_NO));
                    String rating = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.RATING));
                    String websiteName = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.WEBSITE));
                    String imagePath = c1.getString(c1.getColumnIndex(SavedLocationsTable.Columns.IMAGE_PATH));
                    int id = c1.getInt(c1.getColumnIndex(SavedLocationsTable.Columns.ID));

                    SavedLocations locations1 = new SavedLocations(id, placeName, placeAddress, latitude, longitude,
                            phoneNo, vicinity, rating, imagePath, websiteName);

                    locationsArrayList.add(locations1);

                }while(c1.moveToNext());
            }
        }
        c1.close();
        return locationsArrayList;
    }

    public void showList(){
        final ArrayList<SavedLocations> savedLocationsList = getList();

        final SavedLocationsListAdapter adapter;
        adapter = new SavedLocationsListAdapter(savedLocationsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder dataShowBuilder = new AlertDialog.Builder(SavedLocationsActivity.this);

                String placeName = savedLocationsList.get(position).getName();
                String placeAddress = savedLocationsList.get(position).getAddress();
                String vicinity = savedLocationsList.get(position).getVicinity();
                String phoneNo = savedLocationsList.get(position).getPhoneNo();
                String websiteName = savedLocationsList.get(position).getWebsiteName();
                String latitude = savedLocationsList.get(position).getLatitude();
                String longitude = savedLocationsList.get(position).getLongitude();
                String rating = savedLocationsList.get(position).getRating();
                final String imagePath = savedLocationsList.get(position).getImagePath();
                dataShowBuilder.setTitle("Details About the Saved Location => ");
                dataShowBuilder.setMessage("Place Name : "+placeName+
                                            "\n\nPlace Address : "+placeAddress+
                                            "\n\nVicinity of Place : "+vicinity+
                                            "\n\nContact Number : "+phoneNo+
                                            "\n\nWebsite Link : "+websiteName+
                                            "\n\nLatitude : "+latitude+
                                            "\n\nLongitude : "+longitude+
                                            "\n\nUser Rating : "+rating);
                dataShowBuilder.setCancelable(true);
                dataShowBuilder.setPositiveButton("DONE!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                dataShowBuilder.setNeutralButton("SEE THE MAP?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent mapViewIntent = new Intent(SavedLocationsActivity.this, SavedLocationMapViewActivity.class);
                        mapViewIntent.putExtra("IMAGE PATH", imagePath);
                        startActivity(mapViewIntent);
                        Toast.makeText(SavedLocationsActivity.this, "See the Map!!!", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDetailsLocation = dataShowBuilder.create();
                alertDetailsLocation.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SavedLocationsActivity.this);
                final SQLiteDatabase myDb1 = DbOpener.openWritableDatabase(SavedLocationsActivity.this);

                final int locationId = savedLocationsList.get(position).getId();
                alertBuilder.setTitle("Delete the Saved Location!!");
                alertBuilder.setMessage("Are You Sure You Wan't to Delete the Saved Location?");
                alertBuilder.setIcon(R.drawable.delete_saved_loc);
                alertBuilder.setCancelable(true);

                alertBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        myDb1.delete(SavedLocationsTable.TABLE_NAME, SavedLocationsTable.Columns.ID +"="+locationId, null);

                        Toast.makeText(SavedLocationsActivity.this, "The Saved Location has been sucessfully been deleted!", Toast.LENGTH_SHORT).show();
                        showList();
                    }
                });
                alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                AlertDialog alertDeletion = alertBuilder.create();
                alertDeletion.show();
                return true;
            }
        });
    }
}
