package com.example.pinor.inf8405runner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import static java.lang.Math.min;

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
    private double latitude, longitude;
    private Marker currentLocationMarker;
    private ArrayList<Location> locationList;
    private LocationManager locationManager;
    private Date date1;
    private Date date2;
    private Date date3;
    private Fragment fragment = null;

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
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return;
        }
        locationList = new ArrayList<> ();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);

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
                locationList = new ArrayList<> ();
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
                float Distance = getDistance();
                Log.d("Location distance",Float.toString(Distance));
                Log.d("Location size",Integer.toString(locationList.size()));
                reinitializeTimer();
                customHandler.postDelayed(updateTimerThread, 0);
                fragment = ProgressionFragment.newInstance(locationList);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.drawer_content, fragment);
                ft.commit();
            }
        });

        return RootView;
    }

    public float getDistance() {
        float distance, SumDistance = 0;
        int j = 0;
        for(;j<locationList.size()-1;j++){
            distance = locationList.get(j).distanceTo(locationList.get(j+1));
            SumDistance +=distance;
            Log.d("Location",locationList.get(j).toString());
        }
        return SumDistance;
    }

    public void insertTimeDB() {
        long time = stopTime - startTime - pauseDelay;
        Result result = new Result();
        result.set_distance(getDistance());
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
                    String response = new MongoPostResult().execute(Long.toString(result.get_time()), Float.toString(result.get_distance())).get();
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
            googlemap.setMyLocationEnabled(true);
        }
        googlemap.setTrafficEnabled(true);
        googlemap.setIndoorEnabled(true);
        googlemap.setBuildingsEnabled(true);
        googlemap.getUiSettings().setZoomControlsEnabled(true);
        googlemap.setMinZoomPreference(12);



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
            Toast.makeText(this.getActivity(), Double.toString(locationList.get((locationList.size() - 1)).getLatitude()) + "  "+ Double.toString(locationList.get((locationList.size() - 1)).getLongitude()), Toast.LENGTH_LONG).show();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googlemap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            googlemap.animateCamera(CameraUpdateFactory.zoomBy(10));
        } catch (Exception e) {
            Log.d("Camera","Null pointer exception");
        }

    }


    @Override
    public void onConnectionSuspended (int i) {

    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected (@Nullable Bundle bundle) {

    }


    @Override
    public void onStatusChanged (String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled (String s) {

    }

    @Override
    public void onProviderDisabled (String s) {

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
