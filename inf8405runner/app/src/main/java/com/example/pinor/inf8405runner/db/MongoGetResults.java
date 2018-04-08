package com.example.pinor.inf8405runner.db;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pinor on 2018-04-08.
 */

public class MongoGetResults extends AsyncTask<String, Void, String> {

    private MongoDBHandler db = new MongoDBHandler();

    protected void onPreExecute() {}

    /**
     * Main part of the task executing in background
     * @param strings Parameters used to perform the task.
     * @return The web request response
     */
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(db.getURLPrefix() + "?apiKey=" + db.getApiKey());
            Log.d("MongoDB", "URL: " + url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(2000);
            conn.setConnectTimeout(2000);
            conn.setRequestMethod("GET");

            int responseCode=conn.getResponseCode();
            Log.d("MongoDB","GET Results response: " + responseCode);
            if (responseCode == 200) {

                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                Log.d("MongoDB","GET Results data returned: " + sb.toString());
                return sb.toString();
            }
            else {
                conn.disconnect();
                return new String("Error " + responseCode);
            }
        }
        catch(Exception e)
        {
            return new String("Exception: " + e.getMessage());
        }
    }
}
