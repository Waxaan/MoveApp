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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class replays extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private int replay_clicked = 0;
    private Button btnPlayReplay;
    private Spinner replayDropdown;
    private LinkedList<Point> positions;
    private TextView countPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_replays);

        replayDropdown = (Spinner) findViewById(R.id.replayDropdown);
        btnPlayReplay = (Button) findViewById(R.id.btnPlayReplay);
        countPoints = findViewById(R.id.textView5);

        String[] arraySpinner = fileList();//{"test1", "test3", "test2"};

        Spinner spinner = (Spinner)findViewById(R.id.replayDropdown);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        for(int i = 0; i < arraySpinner.length; i++) {
            spinnerAdapter.add(arraySpinner[i]);
        }
        spinnerAdapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(this);


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
            file = openFileInput("replay_0");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int data;
        String out = "";
        try { //might only read one Byte at a time, idk gotta test it
            data = file.read();
            while(data != -1) {
                out += String.valueOf(data);
                data = file.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String outstr = "";

        //convert Strings like "495748" to Integer like "190"
        String[] xypoints = out.split("44");
        int[] outArr = new int[xypoints.length];

        for(int i = 0; i < xypoints.length; i++) {
            int cur_number = 0;
            for(int j = 1; j < xypoints[i].length(); j+=2) {
                cur_number *= 10;
                char b_number = xypoints[i].charAt(j-1);
                char s_number = xypoints[i].charAt(j);
                int raw_number = Character.getNumericValue(b_number)*10+Character.getNumericValue(s_number);

                cur_number += (raw_number-48);
            }
            outArr[i] = cur_number;
        }

        for(int i=1; i < outArr.length; i +=2){
            outstr += "x: " + outArr[i-1] + " y:" + outArr[i] +"\t";
        }
        countPoints.setText(outstr);

        /*

        positions = new LinkedList<Point>();
            //Point np = new Point(Integer.parseInt(xypoints[i-1]), Integer.parseInt(xypoints[i]));
            //positions.add(np);
          */
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
