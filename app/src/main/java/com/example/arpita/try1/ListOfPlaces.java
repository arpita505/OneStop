package com.example.arpita.try1;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class ListOfPlaces extends AppCompatActivity {

    List<HashMap<String, String>> listOfPlaces;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_places);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        listOfPlaces=MainActivity.hashMapList;
        listView=(ListView)findViewById(R.id.list_places);

        PlaceListAdapter placeListAdapter=new PlaceListAdapter(listOfPlaces);
        listView.setAdapter(placeListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i= new Intent(ListOfPlaces.this,PlaceDetailsActivity.class);
                i.putExtra("reference",listOfPlaces.get(position).get("reference"));
                startActivity(i);
            }
        });

    }

    private class PlaceListAdapter extends BaseAdapter{

        class PlaceViewHolder{

            TextView addressTv;
            TextView nameTv;
        }

        private List<HashMap<String, String>> mPlaces;

        public PlaceListAdapter(List<HashMap<String, String >> mPlaces){
            this.mPlaces=mPlaces;
        }

        @Override
        public int getCount() {
            return mPlaces.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return mPlaces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li=getLayoutInflater();
            PlaceViewHolder placeViewHolder;

            if (convertView==null){

                convertView=li.inflate(R.layout.item_list,null);
                placeViewHolder=new PlaceViewHolder();
                placeViewHolder.nameTv=(TextView)convertView.findViewById(R.id.place_name_tv);
                placeViewHolder.addressTv=(TextView)convertView.findViewById(R.id.address_tv);
                convertView.setTag(placeViewHolder);
            }else {
                placeViewHolder = (PlaceViewHolder)convertView.getTag();
            }
            HashMap<String, String> thisPlace = getItem(position);
            placeViewHolder.nameTv.setText(thisPlace.get("place_name"));
            placeViewHolder.addressTv.setText(thisPlace.get("vicinity"));

            return convertView;
        }
    }
}
