package cz.pazzi.inventura6k;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.pazzi.inventura6k.comunication.ServerListener;

/**
 * Created by pavel on 21.08.16.
 */

public class ServerGateway extends AsyncTask<Void,Void,Void> {

    protected String sUrl;
    protected ServerListener listener;
    protected JsonElement result = null;
    protected String error = "";

    public ServerGateway(String url, ServerListener listener) {
        this.sUrl = url;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection connection = null;
        Log.d(getClass().getSimpleName(), "json start downloading...");
        try {
            connection = GetConnection();
            int responseCode = connection.getResponseCode();
            Log.d(getClass().getSimpleName(), "ResponseCode = " + responseCode);

            if(responseCode >= 200 && responseCode <= 299) {
                JsonParser jp = new JsonParser(); //from gson
                String response = GetResponse(connection.getInputStream());
                Log.d(getClass().getSimpleName(), response);
                result = jp.parse(response);
            } else {
                Log.e(this.getClass().toString(), "responseCode = " + responseCode);
                error = GetResponse(connection.getErrorStream());
                Log.e(getClass().getSimpleName(), "errorStream: " + error);
            }

        } catch (Exception e) {
            error = e.toString();
            e.printStackTrace();
            Log.e(getClass().getSimpleName(), "json downloading error: " + error);
        }
        if(connection != null) {
            connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(error.isEmpty()) {
            listener.OnServerOK(result);
        } else {
            listener.OnServerError(error);
        }
    }

    protected HttpURLConnection GetConnection() throws IOException {
        URL url = new URL(sUrl);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        return connection;
    }

    protected final static String GetResponse(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer response = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
}

