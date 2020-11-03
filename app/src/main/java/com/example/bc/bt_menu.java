package com.example.bc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

// https://create.arduino.cc/projecthub/azoreanduino/simple-bluetooth-lamp-controller-using-android-and-arduino-aa2253

public class bt_menu extends AppCompatActivity {
    //BT connection definitions
    public static String MODULE_MAC = "00:18:E4:0C:68:C0";
    public final static int REQUEST_ENABLE_BT = 1;
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bta;
    BluetoothDevice mmDevice;
    static BTConnectionThread btt;
    static BluetoothSocket mmSocket;

    //GUI definitions
    private Button  connect;
    private EditText uuid, mac;

    public Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_menu);

        connect = findViewById(R.id.connect);
        uuid = findViewById(R.id.uuid);
        mac = findViewById(R.id.mac);


        mac.setText(MODULE_MAC);
        uuid.setText(MY_UUID.toString());

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect.setEnabled(false);

                bta = getDefaultAdapter();
                MODULE_MAC = mac.getText().toString();
                MY_UUID = UUID.fromString(uuid.getText().toString());
                if(!bta.isEnabled()){
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
                }else{
                    try {
                        initiateBluetoothProcess();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
            try {
                initiateBluetoothProcess();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initiateBluetoothProcess() throws IOException {

        if(bta.isEnabled()) {
            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            mmDevice = bta.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[-BLUETOOTH-]", "Connected to: " + mmDevice.getName());
            } catch (IOException e) {

                    Toast toast = Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_LONG);
                    toast.show();
                    Log.i("[-BLUETOOTH-]", "unable to connect");
                    mmSocket.close();
                    //connect.setEnabled(true);
                    return;
            }

        }
        Log.i("[-BLUETOOTH-]", "Creating Handler");
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == BTConnectionThread.RESPONSE_MESSAGE){

                    String txt = (String)msg.obj;

                    processMsg(txt);

                }
            }
        };

        Log.i("[-BLUETOOTH-]", "Thread running succesfully");
        btt = new BTConnectionThread(mmSocket,mHandler);
        btt.start();


        //bluetooth ON notification to MainActivity
        Intent intent=new Intent();
        intent.putExtra("result",true);
        setResult(2,intent);
        //finishing activity
        finish();
    }




    public void processMsg(String txt){                                                             // process recieved messages

        JSONObject json;

        try {
            json = new JSONObject(txt);
            if (!json.getBoolean("data")) {
                configure_param.setValues(json.get("time").toString(), json.get("loops").toString());
            }else
                temp_menu.printOutput(json.get("tempAmbient").toString(), json.get("tempObject").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
