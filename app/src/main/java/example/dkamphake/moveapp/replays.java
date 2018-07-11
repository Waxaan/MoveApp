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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class replays extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private int replay_clicked = 0;
    private Button btnPlayReplay;
    private Spinner replayDropdown;
    private LinkedList<Point> positions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_replays);

        replayDropdown = (Spinner) findViewById(R.id.replayDropdown);
        btnPlayReplay = (Button) findViewById(R.id.btnPlayReplay);

        String[] arraySpinner = fileList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        replayDropdown.setAdapter(adapter);


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
        FileInputStream file = null;
        try {
            file = openFileInput("replay_"+position);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String data = null;
        try { //might only read one Byte at a time, idk gotta test it
            data = new String(String.valueOf(file.read()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] xypoints = data.split(",");
        positions = new LinkedList<Point>();
        for(int i=1; i < xypoints.length; i +=2){
            Point np = new Point(Integer.parseInt(xypoints[i-1]), Integer.parseInt(xypoints[i]));
            positions.add(np);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
