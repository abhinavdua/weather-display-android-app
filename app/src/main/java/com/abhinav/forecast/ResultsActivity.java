package com.abhinav.forecast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

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
    String img_url;
    String summary_data="";
    String temp_string="";
    String tunit="";
    JSONObject resp;
    RelativeLayout show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ResultsActivity");


        FacebookSdk.sdkInitialize(getApplicationContext());

        Button details = (Button) findViewById(R.id.details);
        Button map = (Button) findViewById(R.id.map);

        hashMap = (HashMap<String, String>) intent.getSerializableExtra("data_hash");
        get_params = new RequestParams();
        get_params.put("address", hashMap.get("street"));
        get_params.put("city", hashMap.get("city"));
        get_params.put("temp", hashMap.get("degree"));
        get_params.put("state", hashMap.get("state"));

        map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent map = new Intent(getApplicationContext(), MapActivity.class);
                map.putExtra("user_data", hashMap);
                map.putExtra("json_data", resp.toString());
                startActivity(map);
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent details = new Intent(getApplicationContext(), DetailsActivity.class);
                details.putExtra("user_data", hashMap);
                details.putExtra("json_data", resp.toString());
                startActivity(details);
            }
        });

        try{
            getWeatherData();
        }
        catch (JSONException e){
            Toast.makeText(getApplicationContext(), "Could not fetch data from server", Toast.LENGTH_SHORT).show();
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        fb_share = (ImageButton) findViewById(R.id.fb_button);

        fb_share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Current Weather in " + hashMap.get("city")+ ", " + hashMap.get("state"))
                            .setContentDescription(summary_data + ", "+ temp_string + " "+ tunit)
                            .setContentUrl(Uri.parse("http://forecast.io"))
                            .setImageUrl(Uri.parse(img_url))
                            .build();

                    shareDialog.show(linkContent);
                }
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(getApplicationContext(), "Facebook Post Successful", Toast.LENGTH_SHORT).show();
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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getWeatherData() throws JSONException {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Please Wait...");
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        progress.show();
        show = (RelativeLayout) findViewById(R.id.show_details);
//        RelativeLayout hide = (RelativeLayout) findViewById(R.id.hide_details);
//        hide.setVisibility(View.VISIBLE);
        show.setVisibility(View.GONE);
        RestClient.get("", get_params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    resp = response;

                    String wunit="";
                    String dunit="";
                    String vunit="";
                    String punit="";

                    if(hashMap.get("degree").equals("F"))
                    {
                        tunit = "\u00B0F";
                        wunit = "mph";
                        dunit = "\u00B0F";
                        vunit = "mi";
                        punit = "mb";
                    }
                    else if(hashMap.get("degree").equals("C"))
                    {
                        tunit = "\u00B0C";
                        wunit = "m/s";
                        dunit = "\u00B0C";
                        vunit = "km";
                        punit = "hPa";
                    }


                    JSONObject currently = response.getJSONObject("currently");
                    String img_icon = currently.getString("icon");
                    String img="";

                    if(img_icon.equals("clear-day"))
                    {
                        img="clear";
                    }

                    if(img_icon.equals("clear-night"))
                    {
                        img="clear_night";
                    }
                    if(img_icon.equals("rain"))
                    {
                        img="rain";
                    }
                    if(img_icon.equals("snow"))
                    {
                        img="snow";
                    }
                    if(img_icon.equals("sleet"))
                    {
                        img="sleet";
                    }
                    if(img_icon.equals("wind"))
                    {
                        img="wind";
                    }
                    if(img_icon.equals("fog"))
                    {
                        img="fog";
                    }
                    if(img_icon.equals("cloudy"))
                    {
                        img="cloudy";
                    }
                    if(img_icon.equals("partly-cloudy-day"))
                    {
                        img="cloud_day";
                    }
                    if(img_icon.equals("partly-cloudy-night"))
                    {
                        img="cloud_night";
                    }

                    img_url = "http://cs-server.usc.edu:45678/hw/hw8/images/" + img + ".png";
                    new DownloadImageTask((ImageView) findViewById(R.id.icon)).execute(img_url);


                    summary_data = currently.getString("summary");
                    TextView summary = (TextView) findViewById(R.id.summary);
                    summary.setText(summary_data+ " in " + hashMap.get("city") + " ," + " " + hashMap.get("state"));

                    String temp_val = currently.getString("temperature");
                    int temp = (int) Double.parseDouble(temp_val);
                    temp_string = Integer.toString(temp);

                    TextView temperature_tv = (TextView) findViewById(R.id.temperature);
                    TextView temp_unit = (TextView) findViewById(R.id.temp_unit);
                    if(hashMap.get("degree").equals("F")) {
                        temperature_tv.setText(Html.fromHtml(temp_string));
                        temp_unit.setText(Html.fromHtml("\u2109"));
                    }
                    else if(hashMap.get("degree").equals("C"))
                    {
                        temperature_tv.setText(Html.fromHtml(temp_string));
                        temp_unit.setText(Html.fromHtml("\u2103"));
                    }

                    String precipIntensity = currently.getString("precipIntensity");
                    Double precip_val = Double.parseDouble(precipIntensity);
                    String precipdisplay = "";

                    if(precip_val>=0 && precip_val<0.002) {
                        precipdisplay = "None";
                    }
                    if(precip_val>=0.002 && precip_val<0.017) {
                        precipdisplay = "Very Light";
                    }
                    if(precip_val>=0.017 && precip_val<0.1) {
                        precipdisplay = "Light";
                    }
                    if(precip_val>=0.1 && precip_val<0.4) {
                        precipdisplay = "Moderate";
                    }
                    if(precip_val>=0.4) {
                        precipdisplay = "Heavy";
                    }

                    TextView ppt = (TextView)findViewById(R.id.c_precipitation);
                    ppt.setText(precipdisplay);

                    String precipProbability = currently.getString("precipProbability").replaceAll("\\D+","");
                    int rainData = Integer.parseInt(precipProbability);
                    int rainValue = rainData * 100;
                    String rain = Integer.toString(rainValue);


                    String dewPoint = currently.getString("dewPoint");
                    double dewData = Double.parseDouble(dewPoint);
                    DecimalFormat df = new DecimalFormat("#.##");
                    dewData = Double.valueOf(df.format(dewData));
                    int dewdata_int = (int)dewData;
                    String dewDisplay = Integer.toString(dewdata_int);

                    TextView rain_tv = (TextView)findViewById(R.id.c_rain);
                    rain_tv.setText(rain+" %");

                    TextView dew = (TextView)findViewById(R.id.c_dew);
                    dew.setText(dewDisplay + " " + dunit);


                    String humidity = currently.getString("humidity");
                    double humidityData = Double.parseDouble(humidity);
                    double humidityValue = humidityData * 100;
                    int humInt = (int) humidityValue;
                    String humidityDisplay = Integer.toString(humInt);


                    String visibility = currently.getString("visibility");
                    double visibilityData = Double.parseDouble(visibility);
                    NumberFormat formatter = new DecimalFormat("#0.00");

                    String visibilityDisplay = String.format("%.2f", visibilityData);;

                    String timezone = response.getString("timezone");

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    sdf.setTimeZone(TimeZone.getTimeZone(timezone));

                    JSONObject daily = response.getJSONObject("daily");
                    JSONArray dailydata = daily.getJSONArray("data");
                    JSONObject daily_today = dailydata.getJSONObject(0);

                    String sunrisedata = daily_today.getString("sunriseTime");


                    long sunrise = Long.parseLong(sunrisedata);
                    Date sunrise_date = new  Date(sunrise*1000L);
                    String sunriseDisplay = sdf.format(sunrise_date);


                    String sunsetdata = daily_today.getString("sunsetTime");
                    long sunset = Long.parseLong(sunsetdata);
                    Date sunset_date = new  Date(sunset*1000L);
                    String sunsetDisplay = sdf.format(sunset_date);

                    String tempdatamin = daily_today.getString("temperatureMin");
                    int tempmin = (int)Double.parseDouble(tempdatamin);

                    String tempmindisplaydata = Integer.toString(tempmin);


                    String tempdatamax = daily_today.getString("temperatureMax");
                    int tempmax = (int)Double.parseDouble(tempdatamax);

                    String tempmaxdisplaydata = Integer.toString(tempmax);



                    String t = "L: "+tempmindisplaydata+ (char) 0x00B0+" | "+"H: "+tempmaxdisplaydata +(char) 0x00B0;

                    TextView lowhigh = (TextView)findViewById(R.id.lowHigh);
                    lowhigh.setText(t);

                    TextView humid = (TextView)findViewById(R.id.c_humidity);
                    humid.setText(humidityDisplay + " %");

                    TextView visible = (TextView)findViewById(R.id.c_visibility);
                    visible.setText(visibilityDisplay + " " + vunit);

                    TextView sun_rise = (TextView)findViewById(R.id.c_sunrise);
                    sun_rise.setText(sunriseDisplay.replace("a.m.", "AM").replace("p.m.", "PM"));


                    TextView sun_set = (TextView)findViewById(R.id.c_sunset);
                    sun_set.setText(sunsetDisplay.replace("a.m.", "AM").replace("p.m.", "PM"));

                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(), "Could not load JSON data", Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
                show.setVisibility(View.VISIBLE);
            }
                @Override
                public void onFailure(int statusCode, Header[] headers,String responseString, Throwable e) {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Bad JSON response from the server", Toast.LENGTH_LONG).show();
                }
        });
    }

    public void setWeatherData(JSONObject weather){

    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private ProgressDialog dialog = new ProgressDialog(ResultsActivity.this);


        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}





