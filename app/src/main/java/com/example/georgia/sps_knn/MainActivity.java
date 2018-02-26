package com.example.georgia.sps_knn;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private SensorManager mySensorManager;
    private SensorEventListener mySensorEventListener;
    private Sensor accelerometer;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private float aX = 0, aY=0, aZ=0;
    public String TAG="com.example.georgia.sps_knn";
    private String ssid;
    private  int rssi;
    private  long localTime;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    public  long now,future;
    public boolean moving;

    TextView txt1;
    ImageView img1,img2,img3,img4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Line to keep screen on permanently
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Log.i(TAG,"App started");
        txt1=(TextView)findViewById(R.id.Activity);

    }


    public void WhichRoom(View view){
        Log.i(TAG,"WhichRoom?");
        // Set the wifi manager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // get the wifi info.
        wifiInfo = wifiManager.getConnectionInfo();
        // update the text.
        ssid=wifiInfo.getSSID();
        rssi=wifiInfo.getRssi();
        localTime=System.currentTimeMillis();
        Log.i(TAG,Integer.toString(rssi));
    }


    public void WhichActivity(View view){
        Log.i(TAG,"WhichActivity?");
        now=System.currentTimeMillis();
        moving=false;
        future=now+10*1000L;
        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        mySensorEventListener=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Log.i(TAG, "Sensor event realised");
                // get the the x,y,z values of the accelerometer
                aX = event.values[0];
                aY = event.values[1];
                aZ = event.values[2];
                Log.i(TAG,Float.toString(aX)+","+Float.toString(aY)+","+Float.toString(aZ));
                mAccelLast = mAccelCurrent;
                mAccelCurrent = Math.sqrt(aX*aX + aY*aY + aZ*aZ);
                double delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;

                if(mAccel > 0.8){
                    moving=true;
                }
                if(System.currentTimeMillis()> future) {
                    mySensorManager.unregisterListener(mySensorEventListener);
                    if(moving){
                        txt1.setText("Moving");
                    }
                    else{
                        txt1.setText("Still");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        // if the default accelerometer exists
        if (mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // set accelerometer
            accelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mySensorManager.registerListener(mySensorEventListener,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        mySensorManager.unregisterListener(mySensorEventListener);
        super.onStop();
    }
}

