package example.dkamphake.moveapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class drawActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener {

    //onscreen refresh rate
    private final int refreshRate = 1000/60;
    private final int bufferSize = 5;
    private SensorManager mSensorManager;
    private Sensor mGyro;
    private ImageView mView;
    private Bitmap mBitmap = Bitmap.createBitmap(380, 380, Bitmap.Config.ARGB_8888);
    private List<Bitmap> prevMaps;
    private TextView mText, mScore;
    private Button btnStart, btnReset;
    private Spinner dropdown;
    private Timer mTimer = new Timer();
    private Bundle bundle;

    private List<Float> GyroXList, GyroYList, GyroZList;
    private List<PointF> positions;

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
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //register the different UI-elements
        mView = findViewById(R.id.imageView);
        mText = findViewById(R.id.textView);
        mScore = findViewById(R.id.textView2);
        btnStart = findViewById(R.id.btnStart);
        btnReset = findViewById(R.id.btnReset);
        dropdown = findViewById(R.id.spinner);

        bundle = getIntent().getExtras();
        isInverted = Objects.requireNonNull(bundle).getBoolean("inverted");

        //initialize the variables needed to draw
        GyroXList = new ArrayList<>();
        GyroYList = new ArrayList<>();
        GyroZList = new ArrayList<>();

        positions = new ArrayList<>();
        prevMaps = new LinkedList<>();


        //add the different game-modes to the dropdown
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gamemodes_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        //add the buttonListener to start the game
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameIsRunning) {
                    startGame();
                }
                else pauseGame();
            }

        });


        //add the buttonListener to reset the game
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
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

    //interrupts game and saves the progress
    private void pauseGame() {
        gameIsRunning = false;
        btnStart.setText("Weiter");
        mTimer.cancel();
    }


    class drawBitmap extends TimerTask {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //average out the sensory data for the Gyro-sensor; returns [X|Y|Z]
                    float[] gyro = calcAverage3(GyroXList, GyroYList, GyroZList);

                    //calculate the current position and calculate the score based off of it
                    positions.add(Game.getNewPosition(positions.get(positions.size()-1), gyro[1], gyro[0], isInverted));
                    float cur_x = positions.get(positions.size()-1).x;
                    float cur_y = positions.get(positions.size()-1).y;
                    current_score += Game.getScoreV2(cur_x, cur_y, gyro[1], gyro[0], current_state) / refreshRate;

                    String scoreline = "Aktueller Punktestand: " + current_score;
                    mScore.setText(scoreline);

                    //to add the color gradient for the courser-line
                    if(positions.size() <= 100) {
                        if(prevMaps.size() == 0) {
                            mBitmap = Graphics.drawCourserToCanvas(positions, current_state);
                            prevMaps.add(mBitmap);
                        } else {
                            mBitmap = Graphics.drawCourserToCanvas(positions, prevMaps.get(0));
                            prevMaps.add(mBitmap);
                        }
                    } else {
                        List<PointF> last100Points = positions.subList(positions.size()-100, positions.size());
                        mBitmap = Graphics.drawCourserToCanvas(last100Points, prevMaps.get(90));
                        prevMaps.remove(0);
                        prevMaps.add(mBitmap);
                    }

                    //Attach the canvas to the ImageView
                    mView.setImageDrawable(new BitmapDrawable(getResources(), mBitmap));

                    /* used for debug purposes
                    @SuppressLint("DefaultLocale") String output =
                                    "GYRO: X: " + String.format("%.3f", gyro[0]*100) +
                                        "m Y: " + String.format("%.3f", gyro[1]*100) +
                                        "m Z: " + String.format("%.3f", gyro[2]*100) + "m";
                    mText.setText(output);
                    */
                }
            });

        }
    }

    //updates the current state of the game according to the now selected item in the dropdown-menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0 : current_state = state.RECTANGLE;
                break;
            case 1 :  current_state = state.CIRCLE;
                break;
            case 2 :  current_state = state.WSHAPE;
                break;
            default : current_state = state.NOTHING;
                break;
        }
        pauseGame();
        reset();
    }

    //returns the average value of a given list with 3 coordinates
    public float[] calcAverage3(List<Float> X, List<Float> Y, List<Float> Z) {
        float[] retArr = new float[3];

        //if there are less values in X|Y|Z than as intended in bufferSize don't run out of bounds
        for(int i = 0; i < ((X.size() < bufferSize)? X.size() : bufferSize); i++) {
            retArr[0] += X.get(X.size()-i-1) / X.size();
            retArr[1] += Y.get(Y.size()-i-1) / Y.size();
            retArr[2] += Z.get(Z.size()-i-1) / Z.size();
        }
        return retArr;
    }


    public final void reset() {

        gameIsRunning = false;
        current_score = 0;
        positions = new LinkedList<>();
        positions = Game.getStartPosition(current_state);
        mBitmap = Graphics.getMap(current_state);
        mView.setImageBitmap(mBitmap);
        prevMaps = new LinkedList<>();
        prevMaps.add(mBitmap);
        String scoreline = "Aktueller Punktestand: " + current_score;
        mScore.setText(scoreline);
        btnStart.setText("Start");
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //implements the SensorData as a Queue with the maximum size of buffer_size
    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor==mGyro) {
            GyroXList.add(event.values[0]);
            GyroYList.add(event.values[1]);
            GyroZList.add(event.values[2]);
            if(GyroXList.size() > bufferSize) {
                GyroXList.remove(0);
                GyroYList.remove(0);
                GyroZList.remove(0);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {  }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        pauseGame();
    }
}
