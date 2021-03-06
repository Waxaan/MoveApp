package example.dkamphake.moveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private Button btnHistory, btnGameStart;
    private Switch invertSwitch;
    private boolean inverted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add the UI-elements to the activity
        setContentView(R.layout.activity_main);
        btnHistory = findViewById(R.id.History);
        btnGameStart = findViewById(R.id.btnChangeActivity);
        invertSwitch = findViewById(R.id.switch2);

        //add listeners to buttons
        btnHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "not yet implemented", Toast.LENGTH_SHORT).show();
            }
        });

        btnGameStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, drawActivity.class);
                intent.putExtra("inverted", inverted);
                intent.putExtra("replay", new ArrayList<>());
                startActivity(intent);
            }
        });

        //changes the movement-direction to normal or inverted on change
        invertSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (invertSwitch.isChecked()) { inverted = true; }
                else { inverted = false; }
            }
        });

    }

    //standard functions
    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
