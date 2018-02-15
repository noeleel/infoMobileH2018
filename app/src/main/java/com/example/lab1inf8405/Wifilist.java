package com.example.lab1inf8405;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Melchor on 2018-02-04.
 */

public class Wifilist extends Activity {
    private Element[] nets;
    private WifiManager manWifi;
    private List<ScanResult> wifiList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.manWifi = (WifiManager)
                getApplicationContext().getSystemService (Context.WIFI_SERVICE);
        this.manWifi.startScan ();
        this.wifiList = this.manWifi.getScanResults ();
        this.nets = new Element[wifiList.size ()];
        for (int i = 0; i < wifiList.size (); i++) {
            String item = wifiList.get (i).toString ();
            String[] vector_item = item.split (",");
            String item_essid = vector_item[0];
            String item_capabilities = vector_item[2];
            String ssid = item_essid.split (":")[1];
            String security = item_capabilities.split (":")[1];
            nets[i] = new Element (ssid, security);

        }
        setContentView (R.layout.wifilist);
        AdapterElements adapter = new AdapterElements (this);
        ListView netList = (ListView) findViewById (R.id.listView1);
        netList.setAdapter (adapter);

    }

    class AdapterElements extends ArrayAdapter<Object> {
        Activity context;

        public AdapterElements(Activity context) {
            super (context, R.layout.elementsitems, nets);
            this.context = context;

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater ();
            View item = inflater.inflate (R.layout.elementsitems, null);

            TextView lblTitle = (TextView) item.findViewById (R.id.strssid);
            lblTitle.setText (nets[position].getTitle ());

            TextView lblSubTitle = (TextView) item.findViewById (R.id.strsecurity);
            lblSubTitle.setText (nets[position].getSubtitle ());
            return (item);
        }

    }




}