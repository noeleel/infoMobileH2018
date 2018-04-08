package com.example.pinor.inf8405runner;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pinor.inf8405runner.db.DBHandler;
import com.example.pinor.inf8405runner.db.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PerformanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PerformanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerformanceFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private DBHandler db;

    List<Result> results;

    private List<TextView> timesText;
    private List<TextView> lengthsText;

    public PerformanceFragment() {}

    public static PerformanceFragment newInstance() {
        PerformanceFragment fragment = new PerformanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View RootView = inflater.inflate(R.layout.fragment_performance, container, false);

        timesText = new ArrayList<TextView>(5);
        timesText.add((TextView) RootView.findViewById(R.id.time1_tv));
        timesText.add((TextView) RootView.findViewById(R.id.time2_tv));
        timesText.add((TextView) RootView.findViewById(R.id.time3_tv));
        timesText.add((TextView) RootView.findViewById(R.id.time4_tv));
        timesText.add((TextView) RootView.findViewById(R.id.time5_tv));

        lengthsText = new ArrayList<TextView>(5);
        lengthsText.add((TextView) RootView.findViewById(R.id.length1_tv));
        lengthsText.add((TextView) RootView.findViewById(R.id.length2_tv));
        lengthsText.add((TextView) RootView.findViewById(R.id.length3_tv));
        lengthsText.add((TextView) RootView.findViewById(R.id.length4_tv));
        lengthsText.add((TextView) RootView.findViewById(R.id.length5_tv));

        loadDBResults();

        return RootView;
    }

    private void loadDBResults() {
        results = db.getAllResults();
        Collections.sort(results, Result.ResultComparator);

        for (int i = 0; i < timesText.size(); i++) {
            if (results.size() > i) {
                int secs = (int) (results.get(i).get_time() / 1000);
                int hours = secs / 60 / 60;
                int mins = secs / 60 - hours * 60;
                timesText.get(i).setText(String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs));
            }
        }

        for (int i = 0; i < lengthsText.size(); i++) {
            if (results.size() > i) {
                lengthsText.get(i).setText("" + results.get(i).get_distance());
            }
        }
    }


    /************************************ Default override ************************************/


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
