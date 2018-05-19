package example.dkamphake.moveapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mGyro, mAccel;
    private TextView dataView1, dataView2, dataView3, dataView4, dataView5, dataView6;
    private Button btnChangeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataView1 = findViewById(R.id.textOutput1);
        dataView2 = findViewById(R.id.textOutput2);
        dataView3 = findViewById(R.id.textOutput3);
        dataView4 = findViewById(R.id.textOutput4);
        dataView5 = findViewById(R.id.textOutput5);
        dataView6 = findViewById(R.id.textOutput6);
        btnChangeActivity = findViewById(R.id.btnChangeActivity);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        btnChangeActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, drawActivity.class));
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == mAccel) {
            dataView1.setText("ACCEL X-Achse "+Float.toString(event.values[0]));
            dataView2.setText("ACCEL Y-Achse "+Float.toString(event.values[1]));
            dataView3.setText("ACCEL Z-Achse "+Float.toString(event.values[2]));
        } else if(event.sensor == mGyro) {
            dataView4.setText("GYRO X-Achse "+Float.toString(event.values[0]));
            dataView5.setText("GYRO Y-Achse "+Float.toString(event.values[1]));
            dataView6.setText("GYRO Z-Achse "+Float.toString(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
