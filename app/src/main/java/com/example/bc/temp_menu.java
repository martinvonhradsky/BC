package com.example.bc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.bc.bt_menu.btt;
import static com.example.bc.bt_menu.mmSocket;

public class temp_menu extends AppCompatActivity {

    private Button data,conf, export;
    private static TextView response, ambient, json;
    public long time;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_menu);
        sendMsg("clear channel");

        conf = findViewById(R.id.config);
        data = findViewById(R.id.getData);
        ambient = findViewById(R.id.Ambient);
        response = findViewById(R.id.response);

        export = findViewById(R.id.exportTEM);
        json = findViewById(R.id.viewJson);
        export.setEnabled(false);

        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //otvorenie okna konfiguracie parametrov
                startActivity(new Intent(temp_menu.this, configure_param.class));
                sendMsg(makeRequest(true, true));

            }
        });

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMsg(makeRequest(true, false));

                export.setEnabled(true);

                }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = "";
                temp = ambient.getText().toString();                                                // nacitanie hodnoty z labelu
                float amb = Float.parseFloat(temp.substring(0, temp.length()-2));                   // uprava na float

                temp = response.getText().toString();
                float obj = Float.parseFloat(temp.substring(0, temp.length()-2));

                JSONObject export_temperature = new JSONObject();
                try {
                    export_temperature.put("temperature", obj);
                    export_temperature.put("room_temperature", amb);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                json.setText(export_temperature.toString());
            }
        });

    }

   public static void end_connection(){
       JSONObject data_request = new JSONObject();
       try {
           data_request.put("data_request", "false");
           data_request.put("config", "false");
       } catch (JSONException e) {
           e.printStackTrace();
       }

       String msg = (data_request + "$");
       btt.write(msg.getBytes());
       btt.cancel();
   }


    public static void printOutput(String amb, String obj){
            response.setText(obj +  " °C");
            ambient.setText(amb +  " °C");
    }


    public void sendMsg(String txt){
        if (mmSocket.isConnected() && btt != null) {

            String msg = (txt + "$");
            btt.write(msg.getBytes());
            //disable the button and wait for 4 seconds to enable it again
            //data.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2700);
                    } catch (InterruptedException e) {
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            data.setEnabled(true);
                        }
                    });

                }
            }).start();
        } else
            Toast.makeText(temp_menu.this, "Unable to connect", Toast.LENGTH_LONG).show();
    }
    public String makeRequest(boolean d, boolean c){
        JSONObject data_request = new JSONObject();
        try {
            data_request.put("data_request", d);
            data_request.put("config", c);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data_request.toString();
    }



}

