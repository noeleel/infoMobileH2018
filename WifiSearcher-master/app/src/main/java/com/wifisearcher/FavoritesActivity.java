package com.wifisearcher;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Vector;

public class FavoritesActivity extends AppCompatActivity {

    private ListView favoritesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFav);
        setSupportActionBar(toolbar);

        // Allow to return to main activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add all favorites to the view
        favoritesListView = (ListView) findViewById(R.id.favorites_list_view);
        Vector<String>  favoritesList = loadFavoritesList();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, favoritesList);
        favoritesListView.setAdapter(adapter);
    }

    private Vector<String> loadFavoritesList() {
        Vector<String> favoritesList = new Vector<String>();
        for (int i = 0; i < FavoritesData.getInstance().getNbFavorites(); i++)
            favoritesList.add("BSSID: " + FavoritesData.getInstance().getElementByIndex(i));

        return favoritesList;
    }
}
