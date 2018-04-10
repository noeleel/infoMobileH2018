package com.example.pinor.inf8405runner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.pinor.inf8405runner.R;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    double latitude,longitude;


    private MyGestureListener mDetector;
    private MotionEvent motionEvent;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDetector.TouchEvent(motionEvent.getAction()));
                Intent intent = new Intent(MapsActivity.this, DrawerActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Permet de s'assurer que l'utilisateur a bien activite la localisation
        // Sinon, un DialogBox s'affichera pour demander a l'utilisateur de l'activer
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Permet la generation de la carte Google Maps si l'utilisateur a bien activite la localisation
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Permet la mise a jour de la position de l'utilisateur
        // lorsque celui-ci se deplace
        final double[] nLatitude = new double[1];
        final double[] nLongitude = new double[1];
        BitmapDescriptor bitmapDescriptor;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();
        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Position actuelle");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        if(client != null)
        {
            // Si l'utilisateur n'est plus la ou que sa fonction de localisation est desactive
            // on le supprime
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
                }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // ***********************************************************************************************
    // ***********************************************************************************************
    // ***********************************************************************************************

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        /*
        Classe privee pour gerer les mouvements de l'utilisateur.
        Si l'utilisateur appuie sur l'ecran, la methode onTouchEvent retourne True,
        sinon, elle retourne False. Ceci permettra de gerer le retour automatique
        vers la classe DrawerActivity une fois que l'utilisateur a fini de consulter la carte.
         */
        private static final String DEBUG_TAG = "Gestures";
        public boolean TouchEvent(int eventAction){

            switch(eventAction) {
                case (0) :
                    Log.d(DEBUG_TAG,"Action was DOWN");
                    return true;
                case (1) :
                    Log.d(DEBUG_TAG,"Action was UP");
                    return true;
                case (2) :
                    Log.d(DEBUG_TAG,"Action was MOVE");
                    return true;
                case (3) :
                    Log.d(DEBUG_TAG,"Action was CANCEL");
                    return true;
                case (4) :
                    Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                            "of current screen element");
                    return true;
                case (8) :
                    Log.d(DEBUG_TAG,"Action was SCROLL");
                    return true;
                default :
                    return false;
            }
        }

    }

}
