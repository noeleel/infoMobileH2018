package com.example.pinor.inf8405runner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pinor.inf8405runner.db.DBHandler;
import com.example.pinor.inf8405runner.db.MongoGetResults;
import com.example.pinor.inf8405runner.db.MongoPostResult;
import com.example.pinor.inf8405runner.db.Result;
import com.example.pinor.inf8405runner.db.Run;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChronoFragment extends Fragment implements OnMapReadyCallback,  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private OnFragmentInteractionListener mListener;

    private DBHandler db;

    private TextView chronoText;
    private Button startButton;
    private Button stopButton;

    private long startTime = 0L;
    private long pauseTime = 0L;
    private long pauseDelay = 0L;
    private long stopTime = 0L;
    long currentTime = 0L;

    private boolean onPause = false;
    private boolean onStop = true;

    private Handler customHandler = new Handler();

    MapView mapView;
    GoogleMap googlemap;
    public static final int REQUEST_LOCATION_CODE = 99;
    private LocationRequest locationRequest;
    private GoogleApiClient client;
    private double latitude, longitude;
    private Marker currentLocationMarker;
    private ArrayList<Location> locationList = new ArrayList<> ();

    public ChronoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChronoFragment newInstance() {
        return new ChronoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        db = new DBHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_chrono, container, false);

        mapView = (MapView)RootView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        chronoText = (TextView) RootView.findViewById(R.id.time_tv);

        startButton = (Button) RootView.findViewById(R.id.start_b);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (startTime == 0) {
                    startTime = SystemClock.uptimeMillis();
                    onStop = false;
                    Log.d("Chrono", "Started");
                } else if (!onPause) {
                    pauseTime = SystemClock.uptimeMillis();
                    onStop = false;
                    onPause = true;
                    Log.d("Chrono", "On pause");
                } else {
                    currentTime = SystemClock.uptimeMillis();
                    pauseDelay += currentTime - pauseTime;
                    onPause = false;
                    Log.d("Chrono", "Restarted");
                }
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });

        stopButton = (Button) RootView.findViewById(R.id.stop_b);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopTime = SystemClock.uptimeMillis();
                onPause = false;
                onStop = true;
                Log.d("Chrono", "Stopped");
                insertTimeDB();
                reinitializeTimer();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });

        return RootView;
    }

    public void insertTimeDB() {
        long time = stopTime - startTime - pauseDelay;
        Result result = new Result();
        result.set_distance(20);
        result.set_time(time);
        db.insertResult(result);
        sendToMongo(result);
        Log.d("Chrono", "Insert value: " + time);
    }

    private void sendToMongo(final Result result) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    String response = new MongoPostResult().execute(Long.toString(result.get_time()), Integer.toString(result.get_distance())).get();
                    Log.d("Chrono", response);
                } catch (Exception e) {}
            }
        };
        thread.start();
    }

    public void reinitializeTimer() {
        startTime = 0L;
        pauseTime = 0L;
        pauseDelay = 0L;
        stopTime = 0L;
        currentTime = 0L;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if (onPause) {
                long chronoValue = pauseTime - startTime - pauseDelay;
                int secs = (int) (chronoValue / 1000);
                int hours = secs / 60 / 60;
                int mins = secs / 60 - hours * 60;
                secs = secs % 60;

                chronoText.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
                startButton.setText("Redémarrer");
            } else if (onStop) {
                chronoText.setText("00:00:00");
                startButton.setText("Démarrer");
            } else {
                long deltaTime = SystemClock.uptimeMillis() - startTime - pauseDelay;
                int secs = (int) (deltaTime / 1000);
                int hours = secs / 60 / 60;
                int mins = secs / 60 - hours * 60;
                secs = secs % 60;

                chronoText.setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
                startButton.setText("Pause");
            }
            customHandler.postDelayed(this, 0);
        }
    };


    /************************************ Default override ************************************/


    @Override
    public void onMapReady(GoogleMap map)
    {
        this.googlemap = map;
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            googlemap.setMyLocationEnabled(true);
        }
        googlemap.setTrafficEnabled(true);
        googlemap.setIndoorEnabled(true);
        googlemap.setBuildingsEnabled(true);
        googlemap.getUiSettings().setZoomControlsEnabled(true);
        googlemap.setMinZoomPreference(12);



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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected (@Nullable Bundle bundle) {

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }



    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this.getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this.getActivity(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
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
                    if(ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            buildGoogleApiClient();
                        }
                        googlemap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this.getActivity(),"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onConnectionSuspended (int i) {

    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged (Location location) {
        // Permet la mise a jour de la position de l'utilisateur
        // lorsque celui-ci se deplace

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
        Log.d("lat = ",""+latitude);
        locationList.add(location);
        try {
            int index = 0;

            ArrayList<Run> runs = db.getRunInstance();
            for (int i = 0; i < runs.size(); i++) {
                if (runs.get(i).get_Points() == null) {
                    index = i;
                    break;
                } else {
                    index = 0;
                }
            }
            Run run = new Run();


            run.set_id(index);
            run.set_date(Calendar.getInstance().getTime());
            run.set_Points(locationList);
            db.insertRun(run);

            Toast.makeText(this.getActivity(),"Nouvel ajout dans la db" , Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Log.d("Db", "Impossible d'utiliser la db");
        }


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googlemap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        googlemap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            // Si l'utilisateur n'est plus la ou que sa fonction de localisation est desactive
            // on le supprime
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this.getContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
