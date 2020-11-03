package com.example.bc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.bc.bt_menu.btt;

public class configure_param extends AppCompatActivity {

    private static EditText loops, time;
    private Button confirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_param);

        confirm = findViewById(R.id.cnf);
        loops = findViewById(R.id.loo);
        time = findViewById(R.id.time);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   JSONObject data_request = new JSONObject();
                try {
                    data_request.put("data_request", "false");
                    data_request.put("config", "true");
                    data_request.put("loops", loops.getText().toString());
                    data_request.put("delay", time.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                String msg = makeMsg();
                if(msg == "") return;

                btt.write(msg.getBytes());
                //status = false;

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
                                finish();
                            }
                        });

                    }
                }).start();


            }
        });
    }

    public String makeMsg(){
        JSONObject data_request = new JSONObject();
        int l = Integer.parseInt(loops.getText().toString());
        int t = Integer.parseInt(time.getText().toString());

        if(l > 0 && l <= 100 && t >= 0 && t < 10000){

        try {
            data_request.put("data_request", "false");
            data_request.put("config", "true");
            data_request.put("loops", loops.getText().toString());
            data_request.put("delay", time.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return (data_request + "$");
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "Počet meraní musí byť v rozsahu 1 - 100 \nRozdiely medzi meraniami musia byť v rozsahu 1 - 9999", Toast.LENGTH_LONG);
            toast.show();
            return "";
        }

    }

    public static void setValues(String t, String i){
        loops.setText(i);
        time.setText(t);
    }
}
