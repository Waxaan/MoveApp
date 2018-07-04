package example.dkamphake.moveapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


//grundmotivation
//implementierungsdetails
//kleine präsentation
//16
public class drawActivity extends AppCompatActivity implements SensorEventListener{

    private final int refreshRate = 1000/60;
    private final int bufferSize = 5;
    private SensorManager mSensorManager;
    private Sensor mGyro, mAccel;
    private Sensor mRot;
    private ImageView mView;
    private Bitmap mBitmap = Bitmap.createBitmap(380, 380, Bitmap.Config.ARGB_8888);
    private List<Bitmap> prevMaps;
    private TextView mText, mScore;
    private Button btnStart, btnCircle, btnRect, btnReset;
    private Timer mTimer;

    private List<Float> GyroXList, GyroYList, GyroZList;
    private List<Float> AccelXList, AccelYList, AccelZList;

    private List<Point> positions;

    private boolean gameIsRunning = false;
    private boolean isInverted = false;

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
        mScore = findViewById(R.id.textView2);
        btnStart = findViewById(R.id.btnStart);
        btnRect = findViewById(R.id.btnRect);
        btnCircle = findViewById(R.id.btnCircle);
        btnReset = findViewById(R.id.btnReset);


        //initialize the variables needed to draw
        AccelXList = new ArrayList<Float>();
        AccelYList = new ArrayList<Float>();
        AccelZList = new ArrayList<Float>();
        GyroXList = new ArrayList<Float>();
        GyroYList = new ArrayList<Float>();
        GyroZList = new ArrayList<Float>();

        positions = new ArrayList<Point>();
        prevMaps = new LinkedList<Bitmap>();

        //add the buttonListener to start the game
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameIsRunning) startGame();
                else stopGame();
            }

        });

        //add the buttonListener to set the gamemode to Rectangle
        btnRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_state = state.RECTANGLE;
                reset();
            }
        });

        //add the buttonListener to set the gamemode to Circle
        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_state = state.CIRCLE;
                reset();
            }
        });

        //add the buttonListener to reset the game
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
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
                    float[] accel = calcAverage3(AccelXList, AccelYList, AccelZList);
                    float[] gyro = calcAverage3(GyroXList, GyroYList, GyroZList);

                    positions.add(Game.getNewPosition(positions.get(positions.size()), gyro[1], gyro[0], isInverted));
                    //if switch is inverted SUBTRACT the change //TODO
                /*if(switchInvert.isChecked()) {
                    x_current = fitToCanvas(x_current + gyro[1]*8); //vertical
                    y_current = fitToCanvas(y_current + gyro[0]*8); //horizontal
                } else { */
                    //x_current = fitToCanvas(x_current - gyro[1]*8); //vertical
                    //y_current = fitToCanvas(y_current - gyro[0]*8); //horizontal
                    //}
                    int cur_x = positions.get(positions.size()).x;
                    int cur_y = positions.get(positions.size()).y;
                    current_score += 10 *Game.getScoreV2(cur_x, cur_y, gyro[1], gyro[0], current_state);

                    if(positions.size() <= 100) {
                        mBitmap = Graphics.drawCourserToCanvas(positions, current_state);
                        prevMaps.add(mBitmap);
                    } else {
                        List<Point> last100Points = positions.subList(positions.size()-100, positions.size());
                        mBitmap = Graphics.drawCourserToCanvas(last100Points, prevMaps.get(90));
                        prevMaps.remove(0);
                        prevMaps.add(mBitmap);
                    }

                    //Attach the canvas to the ImageView
                    mView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));

                    @SuppressLint("DefaultLocale") String output =
                            "Accel: X: " + String.format("%.3f", accel[0]) + " Y: " + String.format("%.3f", accel[1]) + " Z: " + String.format("%.3f", accel[2]) + "\n"+
                                    "GYRO: X: " +  String.format("%.3f", gyro[0]) + "m Y: " + String.format("%.3f", gyro[1])+ "m Z: "  + String.format("%.3f", gyro[2])  + "m";
                    String scoreline = "Aktueller Punktestand: " + current_score;
                    mScore.setText(scoreline);
                    mText.setText(output);
                }
            });

        }
    }

    //returns the average value of a given list with 3 coordinates
    public float[] calcAverage3(List<Float> X, List<Float> Y, List<Float> Z) {
        float[] retArr = new float[3];

        for(int i = 0; i < X.size(); i++) retArr[0] += X.get(i) / X.size();
        for(int i = 0; i < Y.size(); i++) retArr[1] += Y.get(i) / Y.size();
        for(int i = 0; i < Z.size(); i++) retArr[2] += Z.get(i) / Z.size();

        return retArr;
    }



    public final void reset() {

        positions = new LinkedList<Point>();
        positions.add(new Point(190, 40));
        positions.add(new Point(190, 40));

        current_score = 0;

        switch (current_state) {
            case RECTANGLE:
                mBitmap = Graphics.getMap(state.RECTANGLE);
                mView.setImageBitmap(mBitmap);
                break;
            case CIRCLE:
                mBitmap = Graphics.getMap(state.CIRCLE);
                mView.setImageBitmap(mBitmap);
                break;
            case NOTHING:
            default:
                mBitmap = Graphics.getMap(state.NOTHING);
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
            //TODO
            AccelXList.add(event.values[0]);
            //if(AccelXList.size() > bufferSize) AccelXList.remove(0);
            AccelYList.add(event.values[1]);
            //if(AccelYList.size() > bufferSize) AccelYList.remove(0);
            AccelZList.add(event.values[2]);
            //if(AccelZList.size() > bufferSize) AccelZList.remove(0);

        } else if(event.sensor==mGyro) {
            GyroXList.add(event.values[0]);
            //if(GyroXList.size() > bufferSize) GyroXList.remove(0);
            AccelYList.add(event.values[1]);
            //if(AccelYList.size() > bufferSize) GyroYList.remove(0);
            GyroZList.add(event.values[2]);
            //if(GyroZList.size() > bufferSize) GyroZList.remove(0);
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
