package com.abhinav.forecast;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.maps.MapFragment;
import com.hamweather.aeris.communication.AerisEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MapActivity extends AppCompatActivity {

    private HashMap<String, String> UserDataMap;
    String JsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        UserDataMap = (HashMap<String, String>) intent.getSerializableExtra("user_data");
        JsonData = intent.getStringExtra("json_data");

        double lat=0;
        double lng=0;

        try {
            JSONObject obj = new JSONObject(JsonData);
            lat = (double) obj.get("latitude");
            lng = (double) obj.get("longitude");
            //Toast.makeText(getApplicationContext(), Double.toString(lat), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        Bundle bundle = new Bundle();
        bundle.putString("lat", Double.toString(lat));
        bundle.putString("lng", Double.toString(lng));

        //Toast.makeText(MapActivity.this, bundle.toString(), Toast.LENGTH_SHORT).show();

        MapFragmentGoogle myfragment1 = new MapFragmentGoogle();

        myfragment1.setArguments(bundle);

        fragmentTransaction.add(R.id.MapFrame,myfragment1);
        fragmentTransaction.commit();
    }

    }


