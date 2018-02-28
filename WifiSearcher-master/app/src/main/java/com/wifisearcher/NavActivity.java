package com.wifisearcher;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

    /*

    La classe NavActivity permet l'affichage des informations relatives au reseau wifi
    selectionne lorsque l'utilisateur choisit un des marqueurs sur la carte Google Maps.

     */
public class NavActivity extends AppCompatActivity {

    private String WifiKey;

    private String pointTitle;
    private String pointSSID;
    private String pointBSSID;
    private String pointRSSI;
    private String pointCapabilities;

    private Toast favoritesUpdateMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected (@NonNull MenuItem item) {
            // Generation automatique de l'activite NavActivity par Android Studio
            /*
                Cette activite possede trois boutons:
                    Le bouton share permettant de partager les informations du reseau wifi à d'autres applications
                    Le bouton favori permettant d'ajouter le BSSID de ce reseau dans la liste des reseaux favoris
                    Le bouton directions permettant a l'utilisateur de savoir comment acceder a ce reseau wifi
                    (redirection vers l'application Google Maps)
             */
            switch (item.getItemId()) {
                case R.id.navigation_share:
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    share.putExtra(Intent.EXTRA_SUBJECT, "None");
                    share.putExtra(Intent.EXTRA_TEXT, WifiKey.toString());

                    startActivity(Intent.createChooser(share, "Super access point!"));
                    Toast.makeText(getApplicationContext(), "Partage en cours", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_favorite:
                    handleFavoriteButton();
                    return true;
                case R.id.navigation_directions:
                    Intent myIntent = getIntent();
                    String Markerlocation = myIntent.getStringExtra("MarkerLocation");
                    String Currentlocation = myIntent.getStringExtra("CurrentLocation");
                    double sourceLatitude = Double.parseDouble(Currentlocation.split("  ")[0]);
                    double sourceLongitude = Double.parseDouble(Currentlocation.split("  ")[1]);
                    double destinationLatitude = Double.parseDouble(Markerlocation.split("  ")[0]);
                    double destinationLongitude = Double.parseDouble(Markerlocation.split("  ")[1]);
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", sourceLatitude, sourceLongitude, "Current Location", destinationLatitude, destinationLongitude, "Marker location");
                    Intent directions = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    directions.setPackage("com.google.android.apps.maps");
                    startActivity(directions);
                    Toast.makeText(getApplicationContext(), "Directions", Toast.LENGTH_LONG).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        // Get All necessary UI Textview
        TextView pointTitleTV = (TextView) findViewById(R.id.network);
        TextView pointSSIDTV = (TextView) findViewById(R.id.SSID_content);
        TextView pointBSSIDTV = (TextView) findViewById(R.id.BSSID_content);
        TextView pointRSSITV = (TextView) findViewById(R.id.RSSI_content);
        TextView pointCapabilitiesTV = (TextView) findViewById(R.id.capabilities_content);
        TextView batteryUsageTV = (TextView) findViewById(R.id.battery_usage);

        //  Recuperation des arguments de l'Intent
        Intent myIntent = getIntent();

        pointSSID = myIntent.getStringExtra("pointSSID");
        pointBSSID = myIntent.getStringExtra("pointBSSID");
        pointRSSI = myIntent.getStringExtra("pointRSSI");
        pointCapabilities = myIntent.getStringExtra("pointCapabilities");
        pointTitle = "Réseau No." + myIntent.getStringExtra("internalID");

        WifiKey = myIntent.getStringExtra("WifiIntent");

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);

        // Compute battery utilisation percent
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;

        float currentbatterielevel =  (batteryPct*100);
        float pastbatterielevel = Float.parseFloat(myIntent.getStringExtra("PastBatterieLevel"));

        String Batterie_info = "Niveau de la batterie au commencement de l'application : "
                + Float.toString(pastbatterielevel) + "%"
                + "\n\nNiveau de la batterie apres lancement de l'application : "
                + Float.toString(currentbatterielevel) + "%"
                + "\n\nL'application a utilisée " + Float.toString(currentbatterielevel - pastbatterielevel) + " % de votre batterie.";

        // Affichage des informations du reseau Wifi et du pourcentage de batterie consomme
        // pour le scan des reseaux wifi
        pointTitleTV.setText(pointTitle);
        pointSSIDTV.setText(pointSSID);
        pointBSSIDTV.setText(pointBSSID);
        pointRSSITV.setText(pointRSSI);
        pointCapabilitiesTV.setText(pointCapabilities);
        batteryUsageTV.setText(Batterie_info);
       // Wifiinfo.setText(WifiKey + Batterie_info);
        
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void handleFavoriteButton() {
        // Delete previous toast if needed
        if(favoritesUpdateMessage != null)
            favoritesUpdateMessage.cancel();

        // Add or delete from favorites
        if (FavoritesData.getInstance().checkIfInFavorites(pointBSSID)) {
            FavoritesData.getInstance().deleteElement(pointBSSID);
            favoritesUpdateMessage = Toast.makeText(getApplicationContext(), "Supprimé des favoris", Toast.LENGTH_LONG);
        } else {
            FavoritesData.getInstance().addElement(pointBSSID);
            favoritesUpdateMessage = Toast.makeText(getApplicationContext(), "Ajouté aux favoris", Toast.LENGTH_LONG);
        }

        favoritesUpdateMessage.show();
    }

}
