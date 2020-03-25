package com.example.liam.mobdev_assignment;

import android.database.SQLException;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
//import com.loopj.android.http.*;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainActivity extends AppCompatActivity {

    public static String sensorServerURL = "http://10.0.2.2:8080/PhidgetServer/sensorToDB";
    public TextView sensorValueField;
    public Spinner sensorNameField;
    public Button openBtn;
    public Button sendDataBtn;
    public SeekBar seekSlider;
    public SeekBar seekRotate;
    public TextView sliderValue;
    public TextView rotateValue;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        //fills spinner with possible sensor names
        sensorNameField = (Spinner) findViewById(R.id.ddbName);
        String[] items = new String[]{"slider", "rotate"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        sensorNameField.setAdapter(adapter);
        //declares objects
        sensorValueField = (TextView) findViewById(R.id.txtValue);
        openBtn = (Button) findViewById(R.id.btnOpen);
        sendDataBtn = (Button) findViewById(R.id.btnSend);
        seekSlider = (SeekBar) findViewById(R.id.skSlider);
        seekRotate = (SeekBar) findViewById(R.id.skRotate);
        sliderValue = (TextView) findViewById(R.id.txtSlideValue);
        sliderValue.setText("0");
        rotateValue = (TextView) findViewById(R.id.txtRotateValue);
        rotateValue.setText("0");
        //slider will be initially selected when the app starts up
        //sets sensor value textView to the sliders value
        String valueDB = getSensorData("slider");
        Sensor mySensor = gson.fromJson(valueDB, Sensor.class);
        sensorValueField.setText(mySensor.getValue());

        seekSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            //when seekSlider value is changed, update corresponding textView
            @Override
            public void onProgressChanged(SeekBar seekSlider, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                sliderValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        seekRotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            //when seekRotate value is changed, update corresponding textView
            @Override
            public void onProgressChanged(SeekBar seekRotate, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                rotateValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });

        //sends server a value of 200 for slider as this is a value that is impossible for the sensors, they are scaled by 180
        //when this is read by the eclipse project, the motor will simulate a door lock opening for 3 seconds
        openBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendSensorData("slider","200");
            }
        });

        sendDataBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //get the values of the seek bars from corresponding textViews
                //send the sensors data to the server
                String sliderVal = String.valueOf(sliderValue.getText());
                String rotateVal = String.valueOf(rotateValue.getText());
                sendSensorData("slider",sliderVal);
                sendSensorData("rotate",rotateVal);
                //updates Read Data section of the app after data has been sent
                if (String.valueOf(sensorNameField.getSelectedItem()).equals("slider")){
                    String valueDB = getSensorData("slider");
                    //converts read data from JSON
                    Sensor mySensor = gson.fromJson(valueDB, Sensor.class);
                    sensorValueField.setText(mySensor.getValue());
                }
                else
                {
                    String valueDB = getSensorData("rotate");
                    //converts read data from JSON
                    Sensor mySensor = gson.fromJson(valueDB, Sensor.class);
                    sensorValueField.setText(mySensor.getValue());
                }
            }
        });

        sensorNameField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //when a new sensor is selected from the dropdownbox
            //get data from server using the sensors name and display value in the textView
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (String.valueOf(sensorNameField.getSelectedItem()).equals("slider")){
                    String valueDB = getSensorData("slider");
                    //converts read data from JSON
                    Sensor mySensor = gson.fromJson(valueDB, Sensor.class);
                    sensorValueField.setText(mySensor.getValue());
                }
                else
                {
                    String valueDB = getSensorData("rotate");
                    //converts read data from JSON
                    Sensor mySensor = gson.fromJson(valueDB, Sensor.class);
                    sensorValueField.setText(mySensor.getValue());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public String getSensorData(String sensorname)
    {
        //forms the URL to gather sensor data, usng the sensors name
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = sensorServerURL + "?sensorname="+sensorname+"&getdata=true";
        System.out.println("Sending data to: "+fullURL);
        String line;
        String result = "";
        try {
            url = new URL(fullURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String sendSensorData(String sensorName, String sensorValue)
    {
        //creates the URL to send data, using the sensors name and value
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = sensorServerURL + "?sensorname="+sensorName+"&sensorvalue="+sensorValue;
        System.out.println("Sending data to: "+fullURL);
        String line;
        String result = "";
        try {
            url = new URL(fullURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
