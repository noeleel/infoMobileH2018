package com.example.pinor.inf8405runner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pinor.inf8405runner.db.DBHandler;
import com.example.pinor.inf8405runner.db.MongoGetResults;
import com.example.pinor.inf8405runner.db.MongoPostResult;
import com.example.pinor.inf8405runner.db.Result;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class ChronoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private SupportMapFragment mapFragment;

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

    public static final int REQUEST_LOCATION_CODE = 99;


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
        db = new DBHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_chrono, container, false);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {

                private GoogleApiClient client;
                private LocationRequest locationRequest;
                private Location lastlocation;
                private Marker currentLocationmMarker;
                double latitude,longitude;
                private GoogleMap googleMap;

                private final List<LatLng> locations = new ArrayList<LatLng>();

                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);
                    googleMap.getUiSettings().setRotateGesturesEnabled(true);
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        googleMap.setMyLocationEnabled(true);
                    }
                }

                protected synchronized void buildGoogleApiClient() {
                    client = new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onConnected (@Nullable Bundle bundle) {
                            locationRequest = new LocationRequest();
                            locationRequest.setInterval(100);
                            locationRequest.setFastestInterval(1000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


                            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
                            {
                                LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, new LocationListener() {
                                    @Override
                                    public void onLocationChanged (Location location) {
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
                                        locations.add(latLng);
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(latLng);
                                        markerOptions.title("Position actuelle");
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                        googleMap.addMarker(markerOptions);
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 16));
                                        //googleMap.animateCamera(CameraUpdateFactory.zoomBy(10));
                                        if(client != null)
                                        {
                                            // Si l'utilisateur n'est plus la ou que sa fonction de localisation est desactive
                                            // on le supprime
                                            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
                                        }

                                    }
                                });
                            }

                        }

                        @Override
                        public void onConnectionSuspended (int i) {

                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {

                        }
                    }).addApi(LocationServices.API).build();
                    client.connect();
                }

            });

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();


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


    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getActivity().getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions((Activity) getActivity().getApplicationContext(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions((Activity) getActivity().getApplicationContext(),new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else
            return true;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
