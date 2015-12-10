package com.abhinav.forecast;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ResultsActivity extends AppCompatActivity {

    private HashMap<String, String> hashMap;
    private RequestParams get_params;
    private Button facebook;
    private Button loginButton;
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    private String id;
    ImageButton fb_share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        fb_share = (ImageButton) findViewById(R.id.fb_button);

        fb_share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Current Weather in Los Angeles, CA")
                            .setContentDescription(
                                    "Clear, 56 F")
                            .setContentUrl(Uri.parse("http://forecast.io"))
                            .setImageUrl(Uri.parse("http://cs-server.usc.edu:45678/hw/hw8/images/cloud_day.png"))
                            .build();

                    shareDialog.show(linkContent);
                }
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.v("RAGDFADF", result.toString());
                        Toast.makeText(getApplicationContext(), "Posted share successfully", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), "Post Cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });

//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Bundle params = new Bundle();
//                params.putString("link", "http://forecast.io");
//                params.putString("name", "Current weather in Los Angeles, CA");
//                params.putString("caption", "via WeatherApp");
//                params.putString("description", "forecast.io");
//                params.putString("picture", "http://cs-server.usc.edu:45678/hw/hw8/images/clear.png");
//                /* make the API call */
//                new GraphRequest(
//                        loginResult.getAccessToken(),
//                        "/me/feed",
//                        params,
//                        HttpMethod.POST,
//                        new GraphRequest.Callback() {
//                            public void onCompleted(GraphResponse response) {
//                                /* handle the result */
//                                Toast.makeText(getApplicationContext(), "Facebook Post Successful", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                ).executeAsync();
//
//                //Log.v("Facebook get user id", "Encountered error with user details endpoint");
//
//            }
//
//            @Override
//            public void onCancel() {
//                Toast.makeText(getApplicationContext(), "Could not login to facebook", Toast.LENGTH_SHORT).show();
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Toast.makeText(getApplicationContext(), "Encountered an error", Toast.LENGTH_SHORT).show();
//            }
//        });

        hashMap = (HashMap<String, String>) intent.getSerializableExtra("data_hash");
        get_params = new RequestParams();
        get_params.put("address", hashMap.get("street"));
        get_params.put("city", hashMap.get("city"));
        get_params.put("temp", "C");
        get_params.put("state", "CA");



        try{
            getWeatherData();
        }
        catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Could not fetch data from server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getWeatherData() throws JSONException {
        RestClient.get("", get_params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String img_icon = (response.getJSONObject("currently")).getString("icon");
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(), "Could not load JSON data", Toast.LENGTH_SHORT).show();
                }
            }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                }
        });
    }

    public void setWeatherData(JSONObject weather){

    }
}



