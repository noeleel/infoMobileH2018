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
        this.button_scan=(Button)findViewById(R.id.button1);
        this.button_scan.setOnClickListener(this);
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
