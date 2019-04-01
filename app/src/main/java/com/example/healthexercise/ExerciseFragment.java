package com.example.healthexercise;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;


import android.location.LocationListener;

import android.location.LocationManager;

import android.os.Bundle;

import android.preference.PreferenceManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ExerciseFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    FirebaseFirestore db;
    DocumentReference documentReference;

    public GoogleMap mMap;

    static Button startExerciseBtn;
    static Button stopExerciseBtn;

    private String storedEmail;
    private String storedPassword;
    private String maptoggle;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private double latitude;
    private String latitudeString;
    private double longitude;
    private String longitudeString;
    private double distance;
    private String distanceString;

    private ArrayList <String> latList;
    private ArrayList<String> lonList;
    private String latListText;
    private String lonListText;

    DecimalFormat df;

    private String dbLat;
    private String dbLng;
    private String dbDistance;

    TextView distanceMoved;

    Marker marker;
    boolean exerciseStarted;

    private ArrayList<LatLng> points;

    Polyline polyline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.exercise_frag, container, false);

        // Getting account info
        SharedPreferences info = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
        storedEmail = info.getString("email", "");
        storedPassword = info.getString("password", "");
        Log.d("EMAIL: ", storedEmail);
        Log.d("PASSWORD", storedPassword);



        // init database + doc reference
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("users").document(storedEmail);

        // reference to textview displaying how far the user has walked/jogged (meters)
        distanceMoved = v.findViewById(R.id.text_distance);

        // average double to only show 3 decimals
        df = new DecimalFormat("###.###");

        // arraylist containing lat,lng
        points = new ArrayList<LatLng>();
        latList = new ArrayList<String>();
        lonList = new ArrayList<String>();

        // reference to mapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // buttons for starting/stopping exercise. Exercise starts/ends when user presses button
        startExerciseBtn = (Button) v.findViewById(R.id.start_exercise_btn);
        stopExerciseBtn = (Button) v.findViewById(R.id.stop_exercise_btn);

        if(exerciseStarted){
            startExerciseBtn.setBackgroundColor(Color.parseColor("#888888"));
            startExerciseBtn.setEnabled(false);
        } else {
            stopExerciseBtn.setBackgroundColor(Color.parseColor("#888888"));
            stopExerciseBtn.setEnabled(false);
        }

        startExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseStarted = true;

                startExerciseBtn.setBackgroundColor(Color.parseColor("#888888"));
                stopExerciseBtn.setBackgroundColor(Color.parseColor("#C50000"));
                startExerciseBtn.setEnabled(false);
                stopExerciseBtn.setEnabled(true);
                distanceMoved.setText("0");
            }
        });


        stopExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseStarted = false;

                mMap.clear();
                points.clear();

                stopExerciseBtn.setBackgroundColor(Color.parseColor("#888888"));
                startExerciseBtn.setBackgroundColor(Color.parseColor("#57BC90"));
                startExerciseBtn.setEnabled(true);
                stopExerciseBtn.setEnabled(false);
            }
        });

        return v;
    }

    // Method contains snapshot listener for getting updated info from database
    @Override
    public void onStart() {
        super.onStart();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (documentSnapshot.exists()) {

                    dbDistance = documentSnapshot.getString("distancecover");

                    distanceMoved.setText("Distance covered (meters): " + dbDistance);

                }
            }

        });
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

        latitudeString = String.valueOf(latitude);
        longitudeString = String.valueOf(longitude);

        LatLng latLng = new LatLng(latitude, longitude);

        if (exerciseStarted){

            latList.add(String.valueOf(latitude));
            lonList.add(String.valueOf(longitude));

            if (latList.size() == 25){
                latList.clear();
                latListText = "";
            }

            if (lonList.size() == 25){
                lonList.clear();
                lonListText = "";
            }

            for (String object: latList){
                latListText = latListText + ", " +  object;
            }

            for (String object: lonList){
                lonListText = lonListText + ", " + object;
            }

            points.add(latLng);
            distance = SphericalUtil.computeLength(points);
            distanceString = df.format(distance);

            Map<String, Object> user = new HashMap<>();
            user.put("latitude", latitudeString);
            user.put("longitude", longitudeString);
            user.put("distancecover", distanceString);
            user.put("lonlist", lonListText);
            user.put("latlist", latListText);

            documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

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
