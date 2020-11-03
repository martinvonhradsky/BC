package com.example.bc;


import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

//https://create.arduino.cc/projecthub/azoreanduino/simple-bluetooth-lamp-controller-using-android-and-arduino-aa2253

public class BTConnectionThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    static final int RESPONSE_MESSAGE = 10;
    private Handler uih;


    BTConnectionThread(BluetoothSocket socket, Handler uih) throws IOException {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.uih = uih;
        Log.i("[-Connection Thread-]", "Creating Thread");
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        try {
            mmOutStream.flush();
        } catch (IOException e) {
            return;
        }
        Log.i("[-Connection Thread-]","IO's obtained");
    }

    public void run(){

        BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));
        Log.i("[-Connection Thread-]", "Innit thread");
        while(true){
            try {
                String resp = br.readLine();
                Message msg = new Message();
                msg.what = RESPONSE_MESSAGE;
                Log.i("[-Connection Thread-]", "Recieving data");
                msg.obj = resp;
                uih.sendMessage(msg);
            } catch (IOException e) {
                break;
            }
        }
        Log.i("[-Connection Thread-]", "While loop terminated");

    }


    void write(byte[] bytes){
        try{
            Log.i("[-Connection Thread-]", "Writing bytes");
            mmOutStream.write(bytes);

        }catch(IOException ignored){}
    }

    void cancel(){
        try{
            mmSocket.close();
        }catch(IOException ignored){}
    }

}
