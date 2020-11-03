package com.example.bc;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button bt_button;
    private Button temp_button;
    private Button end_con_button;
    private Button blood_pressure;

    private String BPdata = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bt_button = findViewById(R.id.BT_button);
        end_con_button = findViewById(R.id.end_connection);
        temp_button = findViewById(R.id.Temp_button);
        blood_pressure = findViewById(R.id.blood_pres);

        end_con_button.setEnabled(false);
        temp_button.setEnabled(false);
        blood_pressure.setEnabled(false);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                try {
                    handleSendMultipleText(intent); // Handle text being sent
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        blood_pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBPMenu(v);
            }
        });

        end_con_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endConnection();

            }
        });

        bt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtMenu(v);
            }
        });

        temp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTempMenu(v);
            }
        });
    }

    public void startBtMenu (View v){
        Intent intent = new Intent(MainActivity.this, bt_menu.class);
        startActivityForResult(intent, 2);
    }

    public void startBPMenu(View v){
        Intent intent = new Intent(MainActivity.this, blood_pres.class);
        intent.putExtra("BLOOD_PRESSURE_DATA", BPdata);
        startActivity(intent);

    }

    public void endConnection(){
        temp_menu.end_connection();
        bt_button.setEnabled(true);
        temp_button.setEnabled(false);
        end_con_button.setEnabled(false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2)
            if (data != null && data.getBooleanExtra("result", false)) {
                temp_button.setEnabled(true);
                bt_button.setEnabled(false);
                end_con_button.setEnabled(true);
            }
    }

    public void startTempMenu(View v){
        startActivity(new Intent(MainActivity.this, temp_menu.class));
    }

    void handleSendMultipleText(Intent intent) throws IOException {
        ArrayList<Uri> textUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (textUris != null) {
            for (Uri uri : textUris) {
                InputStream stream = getContentResolver().openInputStream(uri);
                assert stream != null;
                BufferedReader r = new BufferedReader(new InputStreamReader(stream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }

                BPdata += total.toString();
            }
            blood_pressure.setEnabled(true);
        }
    }
}
