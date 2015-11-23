package com.abhinav.forecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ResultsActivity extends AppCompatActivity {

    private HashMap<String, String> hashMap;
    private RequestParams get_params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        hashMap = (HashMap<String, String>) intent.getSerializableExtra("data_hash");

        //Toast.makeText(getApplicationContext(), hashMap.get("street"), Toast.LENGTH_SHORT).show();
        get_params = new RequestParams();
        get_params.put("address", hashMap.get("street"));
        get_params.put("city", hashMap.get("city"));
        get_params.put("state", "CA");
        get_params.put("degree", "si");
        try{
            getWeatherData();
        }
        catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Could not fetch data from server", Toast.LENGTH_SHORT).show();
        }
    }

    public void getWeatherData() throws JSONException {
        RestClient.get("", get_params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                setWeatherData(response);

            }

        });
    }

    public void setWeatherData(JSONObject weather){
        try{
            System.out.print(weather.getString("timezone"));
        }
        catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Could not parse JSON data", Toast.LENGTH_SHORT).show();
        }
    }
}



