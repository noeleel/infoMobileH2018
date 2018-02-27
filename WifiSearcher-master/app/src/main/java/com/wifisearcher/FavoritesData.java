package com.wifisearcher;

import java.util.Vector;

/**
 * Created by Pinor on 2018-02-27.
 */

public class FavoritesData {

    private static final FavoritesData favoritesData = new FavoritesData();
    public static FavoritesData getInstance() {return favoritesData;}

    public void addElement(String BSSID) {
        favoritesBSSID.add(BSSID);
    }

    public void deleteElement(String BSSID) {
        favoritesBSSID.remove(BSSID);
    }

    public boolean checkIfInFavorites(String BSSID) {
        return  favoritesBSSID.contains(BSSID);
    }

    private Vector<String> favoritesBSSID = new Vector<String>();
}
