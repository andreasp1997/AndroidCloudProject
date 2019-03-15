package com.example.healthexercise;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;


import android.location.LocationListener;

import android.location.LocationManager;

import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ExerciseFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    public GoogleMap mMap;

    double latitude;
    double longitude;
    double distance;
    DecimalFormat df;

    TextView distanceMoved;

    Marker marker;
    boolean exerciseStarted;

    public static Thread t1;

    private ArrayList<LatLng> points;

    Polyline polyline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.exercise_frag, container, false);

        // reference to textview displaying how far the user has walked/jogged (meters)
        distanceMoved = v.findViewById(R.id.text_distance);
        // average double to only show 3 decimals
        df = new DecimalFormat("###.###");

        // arraylist containing
        points = new ArrayList<LatLng>();

        // reference to mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // button for starting exercise. Exercise starts/ends when user presses button
        final Button exerciseBtn = (Button) v.findViewById(R.id.start_exercise_btn);
        exerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exerciseStarted == true){
                    exerciseStarted = false;
                    mMap.clear();
                    points.clear();
                } else if (exerciseStarted == false){
                    exerciseStarted = true;
                }
            }
        });

        // thread checking if exercise is on or off. Updates button state and distance
        t1 = new Thread() {
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (exerciseStarted == true){
                                    exerciseBtn.setText("Stop Exercise");
                                    exerciseBtn.setBackgroundColor(Color.parseColor("#C50000"));
                                    distanceMoved.setText(df.format(distance) + " meter(s)");
                                } else if (exerciseStarted == false){
                                    exerciseBtn.setText("Start Exercise");
                                    exerciseBtn.setBackgroundColor(Color.parseColor("#57BC90"));
                                }

                            }
                        });//runOnUiThread ends here
                    }//While ends here
                } catch (InterruptedException e) {
                }//Catch ends here
            }

        };

        t1.start();

        return v;
    }

    // method starting googleMap.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        showMap();

    }

    // set map configurations.
    public void showMap() {

        // Enable Zoom
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        //set Map TYPE
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //enable current location
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }


        LocationManager locationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 2000, 0, (android.location.LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("TEST", "WORKING");

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);

        if (exerciseStarted){

            points.add(latLng);
            distance = SphericalUtil.computeLength(points);

            if (marker!=null){
                marker.remove();
            }

            marker =  mMap.addMarker(new MarkerOptions().position(latLng).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

            redrawLine();
        } else if (!exerciseStarted) {

            if (marker!=null){
                marker.remove();
            }

            marker =  mMap.addMarker(new MarkerOptions().position(latLng).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        }

    }

    private void redrawLine(){

        PolylineOptions options = new PolylineOptions().width(5).color(Color.GREEN).geodesic(true);
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        polyline = mMap.addPolyline(options); //add Polyline
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getActivity().getBaseContext(), "Gps is turned off!!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getActivity().getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }
}
