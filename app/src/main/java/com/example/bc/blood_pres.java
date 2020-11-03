package com.example.bc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class blood_pres extends AppCompatActivity {
    TextView sys, dia, pul, time;
    EditText display;
    Button out;
    String[][] arr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pres);

        out = findViewById(R.id.exportBP);
        display = findViewById(R.id.view);
        sys = findViewById(R.id.systolic);
        dia = findViewById(R.id.diastolic);
        pul = findViewById(R.id.pulse);
        time = findViewById(R.id.timestamp);

        String data = getIntent().getStringExtra("BLOOD_PRESSURE_DATA");
        if(data == null){
            time.setText("Žiadne dáta neboli načítané");
        }else{
            arr = parseData(data);
            time.setText(arr[1][0] + " " +arr[1][1]);;
            sys.setText(arr[1][2]);
            dia.setText(arr[1][3]);
            pul.setText(arr[1][4]);
        }

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arr != null)
                    display.setText(parseToJSON(arr));
            }
        });

    }
    private String[][] parseData(String data){                                                      // BP export -> String[][]
        data = data.replace("\"", "");
        String[] lines = data.split("\n");
        int line_count = lines.length;
        String[][] arr = new String[line_count][5];

        int j = 0;
        for(String l : lines){
            String[] fields = l.split(",");
            for(int i = 0; i < 5; i++){
                arr[j][i] = fields[i];
            }
            j++;
        }
        return arr;
    }

    private String parseToJSON(String[][] data){

        JSONObject bp_data = new JSONObject();
        try {
            bp_data.put("date", data[2][0]);
            bp_data.put("time", data[2][1]);
            bp_data.put("sys", data[2][2]);
            bp_data.put("dia", data[2][3]);
            bp_data.put("pulse", data[2][4]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bp_data.toString();

    }

}
