package com.example.pinor.inf8405runner;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.net.TrafficStats;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BatteryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BatteryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BatteryFragment extends Fragment {

    //declaration de variables
    TextView tvUse_bat;
    TextView tvHealth;
    TextView tvTemp;

    TextView tvTX;
    TextView tvRX;

    TextView tvPression;

    int i=1;
    int initLevel=0;

    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


// TODO: Rename and change types and number of parameters

    public static BatteryFragment newInstance(String param1, String param2) {
        BatteryFragment fragment = new BatteryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();
        // verification si le dispositif supporte TrafficsStats
        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity ());
            alert.setTitle("Erreur!");
            alert.setMessage("Votre dispositif ne supporte pas traffic stat monitoring.");
            alert.show();
        }
        else {
            //appel du runnable. Mise à jour du taffic par seconde
            mHandler.postDelayed(mRunnable, 1000);

        }



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    //creation du Runnable
    private final Runnable mRunnable = new Runnable() {
        public void run() {

            long rxBytes = (TrafficStats.getTotalRxBytes() - mStartRX)/1024;
            tvRX.setText(Long.toString(rxBytes)+" KBytes");
            long txBytes = (TrafficStats.getTotalTxBytes() - mStartTX)/1024;
            tvTX.setText(Long.toString(txBytes)+" KBytes");
            mHandler.postDelayed(mRunnable, 1000);
        }

    };


    // definir le broadcast Receiver pour recevoir info battery
    private BroadcastReceiver mBatInfoReceiver;

    {
        mBatInfoReceiver = new BroadcastReceiver ()

        {

            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra (BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra (BatteryManager.EXTRA_SCALE, -1);
                int health = intent.getIntExtra (BatteryManager.EXTRA_HEALTH, 0);


                boolean present = intent.getBooleanExtra (BatteryManager.EXTRA_PRESENT, false);

                String etatBatt = "inconnu";

                if (present) {

                    //Santé de la batterie

                    int healthLbl = -1;

                    switch (health) {
                        case BatteryManager.BATTERY_HEALTH_COLD:
                            healthLbl = 7;
                            etatBatt = "Froide";
                            break;

                        case BatteryManager.BATTERY_HEALTH_DEAD:
                            healthLbl = 4;
                            etatBatt = "Morte";
                            break;

                        case BatteryManager.BATTERY_HEALTH_GOOD:
                            healthLbl = 2;
                            etatBatt = "Bonne";
                            break;

                        case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                            healthLbl = 5;
                            etatBatt = "surtension";
                            break;

                        case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                            healthLbl = 3;
                            etatBatt = "surchauffé";
                            break;

                        case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                            healthLbl = 6;
                            etatBatt = "Problème Inconnu";
                            break;

                        case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        default:
                            break;
                    }

                    if (healthLbl != -1) {
                        tvHealth.setText (String.valueOf (etatBatt));

                    }


                    // Utilisation de la batterie en pourcentage ...

                     if (level != -1 && scale != -1) {

                        // batteryPct =  (startLevel-(level / (float) scale));

                         int startLevel=level+i;  // variable  global i, pour pouvoir differentier la valeur initiale

                         if (startLevel > level) {
                             initLevel=level;  //variable global initLevel, stocke la valeur initiale de level
                             --i;
                         }

                        tvUse_bat.setText (String.valueOf (initLevel-level) + "%");

                    }

                    //Temperature de la batterie lors de l'utilisation de l'application
                    int temperature = intent.getIntExtra (BatteryManager.EXTRA_TEMPERATURE, 0);

                    if (temperature > 0) {
                        float temp = ((float) temperature) / 10f;
                        tvTemp.setText (temp + "°C");

                    }

                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder (getActivity ());
                    alert.setTitle ("Erreur!");
                    alert.setMessage ("Aucune batterie installé.");
                    alert.show ();

                }

            }


        };
    }


    //Affichage
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view1 = inflater.inflate(R.layout.fragment_battery, container, false);

        //Affichage batterie
        tvUse_bat = (TextView) view1.findViewById (R.id.blevel);
        tvHealth = (TextView)view1.findViewById(R.id.bhealth);
        tvTemp = (TextView)view1.findViewById(R.id.btemp);
        getActivity ().registerReceiver (this.mBatInfoReceiver,new IntentFilter (Intent.ACTION_BATTERY_CHANGED));
        //getActivity ().registerReceiver(BI, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Affichage utilisation de la bande passante
        tvRX = (TextView)view1.findViewById(R.id.tvRX);
        tvTX = (TextView)view1.findViewById(R.id.tvTX);

        // Affichage de la pression
        tvPression = (TextView)view1.findViewById(R.id.pression_tv);
        tvPression.setText(String.format("%.1f", PressureSingleton.getInstance().getPressure()) + " KPa");

        //returner inflater.inflate(R.layout.fragment_battery, container, false);
        return view1;

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}






