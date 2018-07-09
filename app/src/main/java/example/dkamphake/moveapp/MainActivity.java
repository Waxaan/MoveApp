package example.dkamphake.moveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity{

    private Button btnHistory, btnGameStart;
    private Switch invertSwitch;
    private boolean inverted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnHistory = findViewById(R.id.History);
        btnGameStart = findViewById(R.id.btnChangeActivity);
        invertSwitch = findViewById(R.id.switch2);

        btnHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, replays.class));
            }
        });

        btnGameStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, drawActivity.class);
                intent.putExtra("inverted", false);
                intent.putExtra("replay", 0);
                startActivity(intent);
            }
        });

        invertSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (invertSwitch.isChecked()) {
                    inverted = true;
                } else {
                    inverted = false;
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
}
