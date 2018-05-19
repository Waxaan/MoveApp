package example.dkamphake.moveapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class drawActivity extends AppCompatActivity implements SensorEventListener{

    private final int refreshRate = 50;
    private final int bufferSize = 5;
    private SensorManager mSensorManager;
    private Sensor mGyro, mAccel;
    private ImageView mView;
    private Bitmap mBitmap = Bitmap.createBitmap(380, 380, Bitmap.Config.ARGB_8888);
    private TextView mText;
    private Button mButton;
    private Timer mTimer;
    private Canvas mCanvas;

    public float[] mGyroX,mGyroY,mGyroZ;
    public float[] mAccelX,mAccelY,mAccelZ;

    //TODO implement as RingBuffer

    private int tick = 0;
    private int tock = 0;

    public float x_last = 190, y_last = 190;
    public float x_current = 190, y_current = 190;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        //initialize the Gyroscope and the Accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //register the different display elements
        mView = findViewById(R.id.imageView);
        mText = findViewById(R.id.textView);
        mButton = findViewById(R.id.btnDraw);

        //initialize the variables needed to draw
        mGyroX = new float[5];
        mGyroY = new float[5];
        mGyroZ = new float[5];
        mAccelX = new float[5];
        mAccelY = new float[5];
        mAccelZ = new float[5];


        //add the buttonListener to reset the state
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        //add a new timer to redraw the bitmap based on the refresh Rate
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new drawBitmap(), 0, refreshRate);
    }

    class drawBitmap extends TimerTask {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //update the sensorData based on the average of the last 3 values
                    float[] accel = calcAverage3(mAccelX, mAccelY, mAccelZ);
                    float[] gyro = calcAverage3(mGyroX, mGyroY, mGyroZ);
                    x_current -= gyro[1]*18; //vertical
                    y_current -= gyro[0]*10; //horizontal

                    if(x_current < 0) x_current = 0;
                    if(x_current > 379) x_current = 379;
                    if(y_current < 0) y_current = 0;
                    if(y_current > 379) y_current = 379;

                    //Create a new image bitmap and attach a brand new canvas to it
                    Canvas tempCanvas = new Canvas(mBitmap);

                    //Draw the image bitmap into the canvas
                    tempCanvas.drawBitmap(mBitmap, 0, 0, null);

                    Paint p = new Paint();
                    p.setColor(Color.GREEN);

                    //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
                    tempCanvas.drawLine(x_last,y_last, x_current, y_current, p);

                    //Attach the canvas to the ImageView
                    mView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));

                    @SuppressLint("DefaultLocale") String output =
                            "ACCEL: X: " + String.format("%.3f", accel[0]) + " Y: " + String.format("%.3f", accel[1]) + " Z: " + String.format("%.3f", accel[2]) + "\n"+
                            "GYRO: X: " +  String.format("%.3f", gyro[0]) + "m Y: " + String.format("%.3f", gyro[1])+ "m Z: "  + String.format("%.3f", gyro[2])  + "m";
                    mText.setText(output);

                    x_last = x_current;
                    y_last = y_current;

                }
            });

        }
    }

    //returns the average value of a given array wth 3 coordinates
    public float[] calcAverage3(float[] X, float[] Y, float[] Z) {
        float[] retArr = new float[3];

        for(int i = 0; i < bufferSize; i++) {
            retArr[0] += X[i]/bufferSize;
            retArr[1] += Y[i]/bufferSize;
            retArr[2] += Z[i]/bufferSize;
        }
        return retArr;
    }

    private float[] fitToCanvas(float[] arr) {
        for(int i = 0; i < 3; i++) {
            if(arr[i] < 0)  arr[i] = 0;
            if(arr[i] > 379)  arr[i] = 379;
        }
        return arr;
    }


    public final void reset() {
        mBitmap = Bitmap.createBitmap(380, 380, Bitmap.Config.ARGB_8888);
        mView.setImageBitmap(mBitmap);

        x_last = 190;
        y_last = 190;
        x_current = 190;
        y_current = 190;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //update the sensor data
    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor==mAccel) {
            mAccelX[tock] = event.values[0];
            mAccelY[tock] = event.values[1];
            mAccelZ[tock] = event.values[2];
            tock++;
            if (tock == bufferSize-1) tock = 0;
        } else if(event.sensor==mGyro) {
            mGyroX[0] = event.values[0];
            mGyroY[0] = event.values[1];
            mGyroZ[0] = event.values[2];
            tick++;
            if (tick == bufferSize-1) tick = 0;
        }
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
