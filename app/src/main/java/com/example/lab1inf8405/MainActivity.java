package com.example.lab1inf8405;
/**
 * Created by Melchor on 2018-02-04.
 */
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    private Button button_scan;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.button_scan = (Button) findViewById(R.id.button1);
        this.button_scan.setOnClickListener(this);
        View.OnClickListener handler = new View.OnClickListener() {
            public void onClick (View v) {
                switch (v.getId()) {

                    case R.id.buttonShareTextUrl:
                        shareTextUrl();
                        break;
                }
            }
        };
        findViewById(R.id.buttonShareTextUrl).setOnClickListener(handler);
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Partage de reseaux wifi");
        share.putExtra(Intent.EXTRA_TEXT, "Partage BSSID, SSID, Securite Authentification");

        startActivity(Intent.createChooser(share, "Share link!"));
        }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.button1):
                Toast.makeText(this, "en train de rechercher reseau WIFI.....", Toast.LENGTH_LONG).show();
                Intent wifiList=new Intent(this, Wifilist.class);
                startActivity(wifiList);

        }

    }
}
