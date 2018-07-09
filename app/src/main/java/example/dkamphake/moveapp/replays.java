package example.dkamphake.moveapp;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.LinkedList;

public class replays extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private int replay_clicked = 0;
    private Button btnPlayReplay;
    private Spinner dropdown;
    private LinkedList<Point> positions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replays);

        dropdown = (Spinner) findViewById(R.id.replayDropdown);
        btnPlayReplay = (Button) findViewById(R.id.btnPlayReplay);

        String[] arraySpinner = new String[] { "1", "2", "3", "4", "5" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);


        btnPlayReplay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(replays.this, drawActivity.class);
                intent.putExtra("inverted", false);
                intent.putExtra("replay", positions);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
