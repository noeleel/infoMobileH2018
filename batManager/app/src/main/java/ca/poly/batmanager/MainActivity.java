package ca.poly.batmanager;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.BatteryManager;
import android.widget.TextView;
import android.content.Intent;
import android.content.Context;



public class MainActivity extends AppCompatActivity {
    private TextView battery;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver ()

    {

        public void onReceive (Context context, Intent intent){
        int level = intent.getIntExtra (BatteryManager.EXTRA_LEVEL, 0);
        battery.setText (String.valueOf (level) + "%");
        }
    };


        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        battery = (TextView) this.findViewById (R.id.text);
        this.registerReceiver (this.mBatInfoReceiver,new IntentFilter (Intent.ACTION_BATTERY_CHANGED));
        }

}