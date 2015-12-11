package com.abhinav.forecast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.hamweather.aeris.maps.AerisMapView;
import com.hamweather.aeris.maps.MapViewFragment;
import com.hamweather.aeris.tiles.AerisTile;


public class MapFragmentGoogle extends MapViewFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_map,container,false);
        mapView = (AerisMapView)view.findViewById(R.id.aerisfragment_map);
        mapView.init(savedInstanceState, AerisMapView.AerisMapType.GOOGLE);
        System.out.println("hello");

        Bundle bundle = getArguments();
        String lat = bundle.getString("lat");
        String lng = bundle.getString("lng");

        double lattitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lng);
        initMap(lattitude,longitude);


        return view;
    }

    private void initMap(double lattitude, double longitude)
    {

        mapView.moveToLocation(new LatLng(lattitude, longitude), 9);
        mapView.addLayer(AerisTile.RADSAT);

    }
}
