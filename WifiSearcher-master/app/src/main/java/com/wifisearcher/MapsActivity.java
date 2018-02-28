package com.wifisearcher;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*

    La classe MapsActivity est une classe auto-generee par Android Studio. C'est le
    point d'entree de notre application. Elle nous permet de localiser l'utilisateur,
    d'afficher les reseaux wifis environnants, d'afficher la liste des reseaux favoris
    et permet d'acceder aux activites NavActivity et WifiScannerActivity.

 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    // Declaration du fragment permettant de gerer la carte Google Maps
    // Declaration de la location du client
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    double latitude,longitude;
    float pastbatterielevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Creation de la carte
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                viewFavoritesHandle(view);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Intent vers l'activite NavActivity : cela nous permet de recuperer
        // le niveau de la batterie au lancement de l'application.
        // Ce niveau sera passe en argument de l'intent ifilter et permettra
        // a l'activite NavActivity de calculer le pourcentage d'utilisation de la batterie
        // depuis le lancement de l'application.
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        pastbatterielevel = (batteryPct*100);
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

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
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
        List<Marker> markers = new ArrayList<Marker>();
        Context context = this.getApplicationContext();
        // Appel au WifiManager afin d'afficher les marqueurs contenant les reseaux wifi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final List<ScanResult> wifiList;
        StringBuilder sb = new StringBuilder();
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();

        // Ajout des marqueurs sur la carte
        for ( int i = 0; i < wifiList.size(); i++) {
            // On place les marqueurs de maniere aleatoire autour du marqueur de localisation de l'utilisateur
            double a = 0.005;
            int n = 0;
            n = randInt(0,10);
            if(n>5) a = -0.005;
            nLatitude[0] = new Random().nextDouble()*a;
            nLongitude[0] = new Random().nextDouble()*a;;
            if (!(wifiList.get(i).capabilities.contains("ESS"))&!(wifiList.get(i).capabilities.contains("WPA2"))&!(wifiList.get(i).capabilities.contains("WPA"))&!(wifiList.get(i).capabilities.contains("WEP"))&!(wifiList.get(i).capabilities.contains("PSK"))&!(wifiList.get(i).capabilities.contains("EAP"))){
                // Permet la modification de la couleur du marqueur
                // Vert pour les reseaux wifi gratuits
                bitmapDescriptor
                        = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN);
            } else {
                // Rouge pour les reseaux wiif payant
                bitmapDescriptor
                        = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED);
            }
            Marker marker = mMap
                    .addMarker(new MarkerOptions()
                            .position(new LatLng(latitude + nLatitude[0], longitude + nLongitude[0]))
                            .title("Wifi network " + Integer.toString(i+1))
                            .icon(bitmapDescriptor));
            markers.add(marker);
        }
        markers.size();
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            // Si l'utilisateur n'est plus la ou que sa fonction de localisation est desactive
            // on le supprime
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }

        // Pour chaque marqueur, on va creer un intent vers NavActivity
        // Afin d'afficher les informations relatives a chaque reseau Wifi
        for (int i = 0; i < wifiList.size(); i++) {
            Marker marker = markers.get(i);
            final int finalI = i;
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                public boolean onMarkerClick(Marker marker) {

                    // Intent vers NavActivity
                    StringBuilder sb = new StringBuilder();
                    final Context context = getApplicationContext();
                    Intent intent = new Intent(context, NavActivity.class);
                    final StringBuilder append = sb.append("\n \tWifi Network " + Integer.toString(finalI + 1) + "\n")
                            .append("SSID:").append(wifiList.get(finalI).SSID).append("\n")
                            .append("BSSID:").append(wifiList.get(finalI).BSSID).append("\n")
                            .append("RSSI:").append(wifiList.get(finalI).level).append("\n")
                            .append("Capabilities:").append("\n");
                    if (wifiList.get(finalI).capabilities.length()==0)
                        sb.append("[NONE]");
                    else sb.append(wifiList.get(finalI).capabilities).append("\n");


                    // Ajout des arguments de l'intent : parametres du reseau wifi
                    intent.putExtra("pointSSID", wifiList.get(finalI).SSID);
                    intent.putExtra("pointBSSID", wifiList.get(finalI).BSSID);
                    intent.putExtra("pointRSSI", "" + wifiList.get(finalI).level);
                    intent.putExtra("pointCapabilities", wifiList.get(finalI).capabilities);
                    intent.putExtra("internalID", Integer.toString(finalI + 1));

                    intent.putExtra("WifiIntent",sb.toString());

                    double a = 0.005;
                    int n = 0;
                    n = randInt(0,10);
                    if(n>5) a = -0.005;
                    nLatitude[0] = new Random().nextDouble()*a;
                    nLongitude[0] = new Random().nextDouble()*a;
                    double MarkerLatitude = latitude + nLatitude[0];
                    double MarkerLongitude = longitude + nLongitude[0];
                    intent.putExtra("MarkerLocation",Double.toString(MarkerLatitude) + "  " + Double.toString(MarkerLongitude));
                    intent.putExtra("CurrentLocation", Double.toString(latitude)+ "  " + Double.toString(longitude));
                    intent.putExtra("PastBatterieLevel", Float.toString(pastbatterielevel));
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    public void scanWifiHandle(View view) {
        // Message signalant à l'utilisateur que l'activite WifiScannerActivity va se lancer
        final Context context = this;
        Toast.makeText(this, "En train de rechercher les reseaux WiFi.....", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, WifiScannerActivity.class);
        startActivity(intent);
    }

    public void helpHandle(View view) {
        // Message d'aide permettant d'expliquer le fonctionnement de l'application a l'utilisateur
        final Context context = this;
        Toast.makeText(context, "Bonjour ! Cliquez sur un marqueur pour afficher ses informations ou choisissez le bouton Scan Wifi pour afficher la liste de tous les access points disponibles.", Toast.LENGTH_LONG).show();
    }

    public void viewFavoritesHandle(View view) {
        // Gestion des favoris
        final Context context = this;
        Intent intent = new Intent(context, FavoritesActivity.class);
        startActivity(intent);
    }

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
}
