package com.nfc.application;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.UiSettings;
import android.location.Geocoder;
import android.content.Context;
import android.location.Address;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }




    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;


        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address!=null && address.size()!=0) {
                Address location=address.get(0);

                return new LatLng(location.getLatitude(),location.getLongitude());
            }

        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
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
        Bundle b = getIntent().getExtras();
        String address = b.getString("address");
        System.out.println(address);
        LatLng company_location =  getLocationFromAddress(address);


        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        if (company_location!= null){
            mMap.addMarker(new MarkerOptions().position(company_location).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(company_location,14.0f));

            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setZoomGesturesEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);

            mUiSettings.setCompassEnabled(true);
        }




    }
}
