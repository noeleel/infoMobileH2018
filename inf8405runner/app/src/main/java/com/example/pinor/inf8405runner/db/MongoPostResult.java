package com.example.pinor.inf8405runner.db;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Pinor on 2018-04-08.
 */

public class MongoPostResult extends AsyncTask<String, Void, String> {

    private MongoDBHandler db = new MongoDBHandler();

    protected void onPreExecute() {
    }

    /**
     * Main part of the task executing in background
     * @param strings Parameters used to perform the task.
     * @return The web request response
     */
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(db.getURLPrefix() + "?apiKey=" + db.getApiKey());
            Log.d("MongoDB", "URL: " + url);

            JSONObject data = new JSONObject();
            data.put("time", Long.parseLong(strings[0]));
            data.put("distance", Integer.parseInt(strings[1]));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(11000);
            conn.setConnectTimeout(11000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(data.toString());

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {

                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                return sb.toString();
            }
            else {
                return new String("Error " + responseCode);
            }
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }
}
