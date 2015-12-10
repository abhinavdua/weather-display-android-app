package com.abhinav.forecast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RadioButton degree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        final EditText street_text = (EditText) findViewById(R.id.street_et);
        final EditText city_text = (EditText) findViewById(R.id.city_et);
        final TextView validation_text = (TextView) findViewById(R.id.validation_tv);
        final Spinner state_spinner = (Spinner) findViewById(R.id.spinner);
        final RadioGroup radio_group = (RadioGroup) findViewById(R.id.radioGroup);


        Button search = (Button) findViewById(R.id.search);
        Button about  = (Button) findViewById(R.id.about);
        Button clear = (Button) findViewById(R.id.clear);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                int radio_id = radio_group.getCheckedRadioButtonId();
                degree = (RadioButton) findViewById(radio_id);
                String degree_val = degree.getText().toString();
                String street = street_text.getText().toString();
                String city = city_text.getText().toString();
                String state = state_spinner.getSelectedItem().toString();

                Intent results = new Intent(getApplicationContext(), ResultsActivity.class);
                if (street.trim().contentEquals("")) {
                    validation_text.setText("Please enter a Street Address.");
                }
                else if (city.trim().contentEquals("")) {
                    validation_text.setText("Please enter the City.");
                }
                else {
                    HashMap<String, String> weather_data = new HashMap<String, String>();
                    weather_data.put("street", street);
                    weather_data.put("city", city);
                    weather_data.put("state", state);
                    weather_data.put("degree", degree_val);

                    results.putExtra("data_hash", weather_data);
                    startActivity(results);
                }
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent about_act = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(about_act);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                street_text.getText().clear();
                city_text.getText().clear();
                radio_group.clearCheck();
                radio_group.check(R.id.radioButton);
                state_spinner.setSelection(0);
                validation_text.setText("");
            }
        });
    }
}
