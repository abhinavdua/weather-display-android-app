package com.abhinav.forecast;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class DetailsActivity extends AppCompatActivity {


    private HashMap<String, String> UserDataMap;
    View v1;
    View v2;
    String JsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DetailsActivity");

        Intent intent = getIntent();
        UserDataMap = (HashMap<String, String>) intent.getSerializableExtra("user_data");
        JsonData = intent.getStringExtra("json_data");

        TextView det = (TextView) findViewById(R.id.details);
        det.setText("More details for " + UserDataMap.get("city") + ", " + UserDataMap.get("state"));

        try {
            String tunit="";
            if(UserDataMap.get("degree").equals("F"))
            {
                tunit = "\u00B0F";
                TextView tv = (TextView)findViewById(R.id.tempHeader);
                tv.setText("Temp("+ (char) 0x00B0 + "F)");
            }
            else if(UserDataMap.get("degree").equals("C"))
            {
                tunit = "\u00B0C";
                TextView tv = (TextView)findViewById(R.id.tempHeader);
                tv.setText("Temp("+ (char) 0x00B0 + "C)");
            }

            JSONObject obj = new JSONObject(JsonData);
            JSONObject hourly_data = obj.getJSONObject("hourly");
            JSONArray data = hourly_data.getJSONArray("data");

            String timezone = obj.getString("timezone");

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(TimeZone.getTimeZone(timezone));

            String time_arr[] = new String[49];
            String icon_arr[] = new String[49];
            String temp_arr[] = new  String[49];

            for(int i=1;i<49;i++) {
                JSONObject data_details = data.getJSONObject(i);
                String time_det = data_details.getString("time");
                long time = Long.parseLong(time_det);
                Date time_date = new Date(time * 1000L);
                String timeDisplay = sdf.format(time_date);
                time_arr[i] = timeDisplay;


                String img = "";
                String img_icon = data_details.getString("icon");
                if (img_icon.equals("clear-day")) {
                    img = "clear";
                }

                if (img_icon.equals("clear-night")) {
                    img = "clear_night";
                }
                if (img_icon.equals("rain")) {
                    img = "rain";
                }
                if (img_icon.equals("snow")) {
                    img = "snow";
                }
                if (img_icon.equals("sleet")) {
                    img = "sleet";
                }
                if (img_icon.equals("wind")) {
                    img = "wind";
                }
                if (img_icon.equals("fog")) {
                    img = "fog";
                }
                if (img_icon.equals("cloudy")) {
                    img = "cloudy";
                }
                if (img_icon.equals("partly-cloudy-day")) {
                    img = "cloud_day";
                }
                if (img_icon.equals("partly-cloudy-night")) {
                    img = "cloud_night";
                }

                icon_arr[i] = img;

                String tempdata = data_details.getString("temperature");
                int temp = (int) Double.parseDouble(tempdata);

                String tempdisplay = Integer.toString(temp);
                temp_arr[i] = tempdisplay;

            }

            for (int j=0;j<48;j++) {

                String imageid = "i"+j;
                int resID = getResources().getIdentifier(imageid, "id", getPackageName());
                String img_url = "http://cs-server.usc.edu:45678/hw/hw8/images/" + icon_arr[j+1] + ".png";
                new DownloadImageTask((ImageView) findViewById(resID)).execute(img_url);
                //ImageView iv = (ImageView)findViewById(resID);
                //int resimage = getResources().getIdentifier(icon_arr[j], "drawable", getPackageName());
                //iv.setImageResource(resimage);

                String timeid = "t"+j;
                int resID_time = getResources().getIdentifier(timeid, "id", getPackageName());
                TextView time_tv = (TextView)findViewById(resID_time);
                time_tv.setText(time_arr[j+1].replace("a.m.", "AM").replace("p.m.", "PM"));


                String tempid = "temp"+j;
                int resID_temp = getResources().getIdentifier(tempid, "id", getPackageName());
                TextView temp_tv = (TextView)findViewById(resID_temp);
                temp_tv.setText(temp_arr[j+1]);
            }

            JSONObject daily = obj.getJSONObject("daily");

            JSONArray daily_data = daily.getJSONArray("data");

            String day_disp[] = new String[8];
            String day_icon_disp[] = new String[8];
            String temp_min[] = new String[8];
            String temp_max[] = new String[8];
            String rep_temp[] = new String[8];


            for (int i=1;i<=7;i++)
            {
                JSONObject daily_details = daily_data.getJSONObject(i);
                String daily_time = daily_details.getString("time");
                long days_time = Long.parseLong(daily_time);

                Date time_date = new  Date(days_time*1000L);
                sdf.applyPattern("EEEE, MMM dd");
                String Mydate = sdf.format(time_date);
                System.out.println(Mydate);
                day_disp[i]=Mydate;

                String img_icon = daily_details.getString("icon");
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

                day_icon_disp[i]=img;

                String tempdatamin = daily_details.getString("temperatureMin");
                int tempmin = (int)Double.parseDouble(tempdatamin);

                String tempmindisplaydata = Integer.toString(tempmin);
                temp_min[i]=tempmindisplaydata;

                String tempdatamax = daily_details.getString("temperatureMax");
                int tempmax = (int)Double.parseDouble(tempdatamax);

                String tempmaxdisplaydata = Integer.toString(tempmax);
                temp_max[i]=tempmaxdisplaydata;

                String t = "Min: "+tempmindisplaydata+tunit+" | "+"Max: "+tempmaxdisplaydata+tunit;

                rep_temp[i]=t;
            }

            for(int i=0;i<7;i++)
            {
                String dayid = "day_"+i;
                int resID_day = getResources().getIdentifier(dayid, "id", getPackageName());
                TextView day_temp = (TextView)findViewById(resID_day);
                day_temp.setText(day_disp[i+1]);


                String tempid = "temp_"+i;
                int resID_temp = getResources().getIdentifier(tempid, "id", getPackageName());
                TextView temp = (TextView)findViewById(resID_temp);
                temp.setText(rep_temp[i+1]);

                String imageid = "img_"+i;
                int resID = getResources().getIdentifier(imageid, "id", getPackageName());
                String img_url = "http://cs-server.usc.edu:45678/hw/hw8/images/" + day_icon_disp[i+1] + ".png";
                new DownloadImageTask((ImageView) findViewById(resID)).execute(img_url);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button sevenDays = (Button) findViewById(R.id.next7days);
        sevenDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout tab24 = (RelativeLayout) findViewById(R.id.tab24);
                tab24.setVisibility(View.GONE);
                RelativeLayout tab7 = (RelativeLayout) findViewById(R.id.tab7days);
                tab7.setVisibility(View.VISIBLE);

                Button days = (Button) findViewById(R.id.next7days);
                days.setBackgroundResource(R.drawable.buttonformat);
                Button hours = (Button) findViewById(R.id.next24hours);
                hours.setBackgroundResource(R.drawable.nobuttonformat);


            }
        });

        Button b1 = (Button) findViewById(R.id.next24hours);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout r1 = (RelativeLayout)findViewById(R.id.tab7days);
                r1.setVisibility(View.GONE);
                RelativeLayout r2 = (RelativeLayout)findViewById(R.id.tab24);
                r2.setVisibility(View.VISIBLE);
                Button daysBtn= (Button)findViewById(R.id.next7days);
                daysBtn.setBackgroundResource(R.drawable.nobuttonformat);
                Button hoursBtn= (Button)findViewById(R.id.next24hours);
                hoursBtn.setBackgroundResource(R.drawable.buttonformat);


            }
        });

        v1 = findViewById(R.id.tab24);
        v2 = findViewById(R.id.tab7days);

        ImageButton plus = (ImageButton) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TableRow layout1 = (TableRow) findViewById(R.id.plusrow);
                layout1.setVisibility(View.GONE);
                for (int i = 25; i <= 48; i++) {
                    String row = "row" + i;
                    int resID_row = getResources().getIdentifier(row, "id", getPackageName());
                    TableRow layout2 = (TableRow) findViewById(resID_row);
                    layout2.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        private ProgressDialog dialog = new ProgressDialog(DetailsActivity.this);

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
