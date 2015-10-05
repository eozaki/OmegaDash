package com.eozaki.omegadash;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Map extends FragmentActivity implements SensorEventListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SensorManager sensorManager;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setUpMapIfNeeded();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setBearingRequired(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        focusOnLocation(location);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                focusOnLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
    }

    public void show(View v) {
        EditText textBar = (EditText) findViewById(R.id.addressText);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        if (textBar.getVisibility() == View.GONE) {
            textBar.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
        } else {
            textBar.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
        }
    }

    public void searchAddress(View v){
        EditText textBar = (EditText)findViewById(R.id.addressText);
        String location = textBar.getText().toString();
        List<android.location.Address> addresses = new ArrayList();
        if(location != null || !location.equals("")) {
            Geocoder geo = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geo.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                Address address = addresses.get(0);
                LatLng position = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(position).title(location));

            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degrees = Math.round(event.values[0]);
        location.setBearing(degrees);
        focusOnLocation(location);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }



    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
    }

    private void spinCameraTo(float degrees) {

    }

    private void focusOnLocation(Location location){
        if (location != null)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(19)                   // Sets the zoom
                    .bearing(location.getBearing())                // Sets the orientation of the camera to east
                    .tilt(85)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
