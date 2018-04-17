package com.example.pinor.inf8405runner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


import com.example.pinor.inf8405runner.db.DBHandler;
import com.example.pinor.inf8405runner.db.Run;



public class ProgressionFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private ArrayList<Location> locationList = new ArrayList <> () ;
    private ArrayList<LatLng> latLngList = new ArrayList <> () ;
    private Spinner spinner;
    private Button btnShare;
    private Button btnView;


    private OnFragmentInteractionListener mListener;
    private MapView mapView;
    private GoogleMap googleMap;
    private PolylineOptions lineOptions;

    private ArrayList<Run> runs;
    private DBHandler db;
    private int index ;

    public ProgressionFragment() {
        // Required empty public constructor
    }

    public static ProgressionFragment newInstance(String param1, String param2) {
        ProgressionFragment fragment = new ProgressionFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_progression, container, false);


        mapView = (MapView)RootView.findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        spinner = (Spinner) RootView.findViewById(R.id.spinner_journey);
        btnShare = (Button) RootView.findViewById(R.id.button_share);
        btnView = (Button) RootView.findViewById(R.id.button_view);
        try {
            db = new DBHandler(getActivity());
            runs = db.getRunInstance();
            for (int i = 0; i < runs.size(); i++) {
                Log.d("Progression - Run", "Run " + Integer.toString(runs.get(i).get_id()) + " Date " + runs.get(i).get_date().toLocaleString());

                if (spinner.getSelectedItem() == "Itinéraire 1") {
                    index = 0;
                } else if (spinner.getSelectedItem() == "Itinéraire 2") {
                    index = 1;
                } else if (spinner.getSelectedItem() == "Itinéraire 3") {
                    index = 2;
                }
                locationList = runs.get(index).get_Points();

            }

            for (Location location : locationList) {
                latLngList.add(new LatLng(location.getLatitude(), location.getLongitude()));
            }
        } catch(Exception e){
            Log.d("Db","Impossible d'utiliser la db");
        }
        btnShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext() , "Partage de l' "+ String.valueOf(spinner.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                latLngList.add(new LatLng(0,0));
                latLngList.add(new LatLng(1,1));
                latLngList.add(new LatLng(2,2));
                Share_data(latLngList);

            }

        });

        btnView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Affichage de l' " + String.valueOf(spinner.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                OnMapShow(latLngList);
            }

        });
        return RootView;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    public void Share_data( ArrayList<LatLng> latLngList){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, latLngList.toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void OnMapShow (ArrayList<LatLng> latLngList) {
        googleMap.addMarker(new MarkerOptions()
                .position(latLngList.get(0))
                .title("Départ"));
        googleMap.addMarker(new MarkerOptions()
                .position(latLngList.get(latLngList.size() - 1))
                .title("Arrivée"));
        for(int i=1; i< latLngList.size() - 1 ; i++)
            googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .alpha(0.3f)
                    .position(latLngList.get(i)));
        lineOptions = new PolylineOptions();
        lineOptions.addAll(latLngList);
        lineOptions.width(10);
        lineOptions.color(Color.RED);
        googleMap.addPolyline(lineOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngList.get(0)));
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady (GoogleMap Map) {
        this.googleMap = Map;
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
