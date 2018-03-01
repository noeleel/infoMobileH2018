package com.wifisearcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WifiScannerActivity extends AppCompatActivity {
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Snackbar.make(view, "Share", Snackbar.LENGTH_LONG)
                        .setAction("Sharing the Wifi results", Share()).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //appel au methode getWifiListNearby
        getWifiListNearby(this.getApplicationContext());
    }

    private View.OnClickListener Share ( ) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, sb.toString());

        startActivity(Intent.createChooser(share, "Share link!"));
        return null;
    }

    //configurer la liste des réseaux WiFi
    public void getWifiListNearby ( Context context ) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();
        for (int i=0; i<wifiList.size();i++){
            sb.append("\n \tWifi Network " + Integer.toString(i+1)+"\n")
                    .append("SSID:").append(wifiList.get(i).SSID).append("\n")
                    .append("BSSID:").append(wifiList.get(i).BSSID).append("\n")
                    .append("RSSI:").append(wifiList.get(i).level).append("\n")
                    .append("Capabilities:").append("\n");
            if (wifiList.get(i).capabilities.length()==0)
                sb.append("[NONE]");
            else sb.append(wifiList.get(i).capabilities).append("\n");
        }
        TextView hotspotslist = (TextView)findViewById(R.id.hotspotslist);
        hotspotslist.setText(sb);
        TextView nhotspots = (TextView)findViewById(R.id.nhotspots);
        nhotspots.setText(String.valueOf(wifiList.size()));

    }

}