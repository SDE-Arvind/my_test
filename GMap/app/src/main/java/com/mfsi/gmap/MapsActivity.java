package com.mfsi.gmap;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * dragable marker and ger address of marker location like, city local,state
 *
 */
public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    LatLng position =new LatLng(28.5355,77.3910);
    final Marker marker_final = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 8));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        googleMap.animateCamera(zoom);

        googleMap.addMarker(new MarkerOptions()
                .title("Shop")
                .snippet("Is this the right location?")
                .position(position))
                .setDraggable(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        // map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng position0 = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
                position0.latitude,
                position0.longitude));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng position0 = marker.getPosition();

        Log.d(getClass().getSimpleName(),
                String.format("Dragging to %f:%f", position0.latitude,
                        position0.longitude));
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        position = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Dragged to %f:%f",
                position.latitude,
                position.longitude));

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(position.latitude,position.longitude, 1);
            if (addresses.size() > 0)
            {
                Log.e("TAG ","locality city :"+addresses.get(0).getLocality());
                Log.e("TAG ","country :"+addresses.get(0).getCountryName());
                Log.e("TAG ","admin area :"+addresses.get(0).getAdminArea());
                Log.e("TAG ","feature name :"+addresses.get(0).getFeatureName());
                Log.e("TAG ","sub admin :"+addresses.get(0).getSubAdminArea());
                Log.e("TAG ","sub locality :"+addresses.get(0).getSubLocality());
                String msg=addresses.get(0).getSubAdminArea()+", "+addresses.get(0).getLocality()+","+addresses.get(0).getCountryName();
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
            }
            else
            {
                // do your staff
                Log.e("TAG ","addresses size is 0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
