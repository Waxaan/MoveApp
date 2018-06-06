package example.dkamphake.moveapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
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
import java.util.ArrayList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class drawActivity extends AppCompatActivity implements SensorEventListener{

    private final int refreshRate = 1000/60;
    private final int bufferSize = 5;
    private SensorManager mSensorManager;
    private Sensor mGyro, mAccel;
    private Sensor mRot;
    private ImageView mView;
    private Bitmap mBitmap = Bitmap.createBitmap(380, 380, Bitmap.Config.ARGB_8888);
    private TextView mText;
    private Button btnStart, btnCircle, btnRect, btnReset;
    private Timer mTimer;

    private float[] mGyroX,mGyroY,mGyroZ;
    private float[] mAccelX,mAccelY,mAccelZ;
    private float[] rotX,rotY,rotZ, rotA;

    //TODO implement as Queue
    private List<Integer> GyroXList, GyroYList, GyroZList;
    private List<Integer> AccelXList, AccelYList, AccelZList;
    private List<Integer> RotXList, RotYList, RotZList, RotSkalarList;

    private List<Point> position;

    private int tick = 0;
    private int tock = 0;

    private boolean gameIsRunning = false;

    private float x_last = 190, y_last = 190;
    private float x_current = 190, y_current = 190;

    private int current_score = 0;

    private state current_state = state.RECTANGLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        //initialize the Gyroscope and the Accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRot = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //register the different display elements
        mView = findViewById(R.id.imageView);
        mText = findViewById(R.id.textView);
        btnStart = findViewById(R.id.btnStart);
        btnRect = findViewById(R.id.btnRect);
        btnCircle = findViewById(R.id.btnCircle);
        btnReset = findViewById(R.id.btnReset);

        //initialize the variables needed to draw
        mGyroX = new float[5];
        mGyroY = new float[5];
        mGyroZ = new float[5];
        mAccelX = new float[5];
        mAccelY = new float[5];
        mAccelZ = new float[5];
        rotX = new float[5];
        rotY= new float[5];
        rotZ = new float[5];
        rotA = new float[5];

        position = new ArrayList<Point>();


        //add the buttonListener to start the game
        btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!gameIsRunning) startGame();
                else stopGame();
            }

        });

        //add the buttonListener to set the gamemode to Rectangle
        btnRect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                current_state = state.RECTANGLE;
                reset();
            }
        });

        //add the buttonListener to set the gamemode to Circle
        btnCircle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                current_state = state.CIRCLE;
                reset();
            }
        });

        //add the buttonListener to reset the game
        btnReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { reset(); }
        });

    }

    private void startGame() {
        gameIsRunning = true;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new drawBitmap(), 0, refreshRate);
        btnStart.setText("Stop");
    }

    private void stopGame() {
        gameIsRunning = false;
        btnStart.setText("Start");
        mTimer.cancel();
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

                x_current = fitToCanvas(x_current - gyro[1]*8); //vertical
                y_current = fitToCanvas(y_current - gyro[0]*8); //horizontal

                current_score += Game.getScore(x_current, y_current, gyro[1], gyro[0], current_state);

                position.add(new Point((int)x_current, (int)y_current));
                if(position.size() >= 200) {
                    List<Point> last200Points = position.subList(position.size()-200, position.size());
                    mBitmap = Drawing.drawPointsToCanvas(last200Points, current_state, mBitmap);
                } else {
                    mBitmap = Drawing.drawPointsToCanvas(position, current_state);
                }

                //Attach the canvas to the ImageView
                mView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));

                @SuppressLint("DefaultLocale") String output =
                        "Aktueller Punktestand: " + current_score + "\nAccel: X: " + String.format("%.3f", accel[0]) + " Y: " + String.format("%.3f", accel[1]) + " Z: " + String.format("%.3f", accel[2]) + "\n"+
                        "ROTATION: X: " + String.format("%.3f", rotX[0]) + " Y: " + String.format("%.3f", rotY[0]) + " Z: " + String.format("%.3f", rotZ[0]) + " Scalar: "+String.format("%.3f", rotA[0])+ "\n"+
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

    private float fitToCanvas(float val) {
        return (val < 0)? 0 : (val > 379)? 379 : val; //if in canvas
    }


    public final void reset() {

        position = new ArrayList<Point>();

        x_last = 190;
        y_last = 40;
        x_current = 190;
        y_current = 40;

        current_score = 0;

        switch (current_state) {
            case RECTANGLE:
                mBitmap = Drawing.getMap(state.RECTANGLE);
                mView.setImageBitmap(mBitmap);
                break;
            case CIRCLE:
                mBitmap = Drawing.getMap(state.CIRCLE);
                mView.setImageBitmap(mBitmap);
                break;
            case NOTHING:
            default:
                mBitmap = Drawing.getMap(state.NOTHING);
                mView.setImageBitmap(mBitmap);
                break;
        }
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
            mGyroX[tick] = event.values[0];
            mGyroY[tick] = event.values[1];
            mGyroZ[tick] = event.values[2];
            tick++;
            if (tick == bufferSize-1) tick = 0;
        } else if(event.sensor==mRot) {
            rotX[0] = event.values[0];
            rotY[0] = event.values[1];
            rotZ[0] = event.values[2];
            rotA[0] = event.values[3];
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mRot, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
