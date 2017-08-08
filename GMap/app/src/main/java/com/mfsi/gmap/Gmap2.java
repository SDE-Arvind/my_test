package com.mfsi.gmap;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *   map like ola cab to choose location by dropping pin
 * Created by Bhaskar Pande on 7/26/2017.
 */
public class Gmap2 extends FragmentActivity implements OnMapReadyCallback {
    private TextView myLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.e("TAG ", "On Create");
        myLocationTextView = (TextView) findViewById(R.id.myLocationTextview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.e("TAG ", "On Map ready");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(Gmap2.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        googleMap.setMyLocationEnabled(true);
//        CameraUpdate zoom = CameraUpdateFactory.zoomTo(100);
//        googleMap.animateCamera(zoom);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //get current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location;
        //gps is giving last location so using network first
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
       else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
        else
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
        //animation to set camera on location
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));

//         lat long  at middle on on camera change
        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition arg0) {

                // put marker on center when move on map
                Log.e("TAG ", "On camera change lat :" + arg0.target.latitude + "   long " + arg0.target.longitude);
                myLocationTextView.setText(getMyLocation(arg0.target));
            }
        });
    }

    /**
     * get complete address from LatLong
     *
     * @param position latlong position
     * @return complete address
     */
    private String getMyLocation(LatLng position) {
        String msg = "";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(position.latitude, position.longitude, 1);
            if (addresses.size() > 0) {
//                Log.e("TAG ", "admin area :" + addresses.get(0).getAdminArea());
//                Log.e("TAG ", "subLocality :" + addresses.get(0).getSubLocality());
//                Log.e("TAG ", "sub adminArea:" + addresses.get(0).getSubAdminArea());
//                Log.e("TAG ", "feature name :" + addresses.get(0).getFeatureName());
//                Log.e("TAG ", "url :" + addresses.get(0).getUrl());
//
//                Log.e("TAG ", "locality city :" + addresses.get(0).getLocality());
//                Log.e("TAG ", "locality city :" + addresses.get(0).getLocality());
//                Log.e("TAG ", "country :" + addresses.get(0).getCountryName());

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                msg = strReturnedAddress.toString();
            } else {
                // do your staff
                Log.e("TAG ", "addresses size is 0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
