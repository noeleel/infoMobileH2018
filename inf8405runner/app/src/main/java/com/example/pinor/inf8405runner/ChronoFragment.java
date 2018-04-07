package com.example.pinor.inf8405runner;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

public class ChronoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Button startButton;
    private Button stopButton;


    private long startTime = 0L;
    private long pauseTime = 0L;
    private long pauseDelay = 0L;
    private long stopTime = 0L;
    long currentTime = 0L;

    private boolean onPause = false;
    private boolean onStop = true;

    private TextView chronoText;

    private Handler customHandler = new Handler();


    public ChronoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChronoFragment newInstance(String param1, String param2) {
        ChronoFragment fragment = new ChronoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_chrono, container, false);
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
                updateDB();
                reinitializeTimer();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });

        return RootView;
    }

    public void updateDB() {

    }

    public void reinitializeTimer() {
        startTime = 0L;
        pauseTime = 0L;
        pauseDelay = 0L;
        stopTime = 0L;
        currentTime = 0L;
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

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            if (onPause) {
                long chronoValue = pauseTime - startTime - pauseDelay;
                Log.d("Chrono", "cv = " + chronoValue);
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
                Log.d("chrono", "" + pauseDelay + ", deltaTime = " + deltaTime);
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
