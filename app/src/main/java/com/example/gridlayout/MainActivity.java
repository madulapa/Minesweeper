package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private int clock = 0;
    private int flags = 4;
    private String mode = "picker";
    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private ArrayList<TextView> bombs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cell_tvs = new ArrayList<TextView>();
        bombs = new ArrayList<TextView>();
        // Method (3): add four dynamically created cells with LayoutInflater
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                tv.setText(String.valueOf(i)+String.valueOf(j));
                //tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);


                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);

            }
        }
        for(int i = 0; i < 4; i++) {
            Random generator = new Random();
            int randomIndex = generator.nextInt(cell_tvs.size()-1);
            bombs.add(cell_tvs.get(randomIndex));
            cell_tvs.get(randomIndex).setText("bomb");
            cell_tvs.get(randomIndex).setBackgroundColor(Color.BLACK);
        }
        runTimer();
        updateFlagCount();
        final ImageButton img = (ImageButton) findViewById(R.id.button);
        img.setBackgroundResource(R.drawable.picker);
        setValues();


    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;

        if (mode == "picker") {
            if(tv.getText() == "bomb"){
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.parseColor("black"));
            }
            else if(tv.getText() != "flagged") {
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.parseColor("white"));
                //run the BFS


            }
        }
        if(mode == "flag" && flags > 0 && tv.getText() != "flagged"){

            tv.setTextColor(Color.RED);
            tv.setText("flagged");
            tv.setTextColor(00000000);
            tv.setBackgroundResource(R.drawable.flag_1_30x20);
            flags--;
            updateFlagCount();
        }
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.textView);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {

                String time = String.format("%d", clock);
                timeView.setText(time);
                clock++;

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void updateFlagCount() {
        final TextView timeView = (TextView) findViewById(R.id.flagCnt);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                String flagcnt = String.format("%d", flags);
                timeView.setText(flagcnt);

            }
        });
    }

    public void onClickMode(View view){
        final ImageButton img = (ImageButton) findViewById(R.id.button);
        if(mode == "flag"){
            mode = "picker";
            img.setBackgroundResource(R.drawable.picker);
        }
        else {
            mode = "flag";
            img.setBackgroundResource(R.drawable.flag);
        }
    }

    private void setValues(){
        ArrayList<Integer> rightmost = new ArrayList<>();
        for(int i = 7; i < 81; i+=8) {rightmost.add(i);}
        ArrayList<Integer> leftmost = new ArrayList<>();
        for(int i = 0; i < 80; i+=8) {rightmost.add(i);}

        for(int i = 0; i < bombs.size(); i++){
            int idx = findIndexOfCellTextView(bombs.get(i));
            System.out.println(idx);
            // { r+1, c }
            if((idx+8) < 80) { // check to make sure its not past the bottom row
                cell_tvs.get(idx + 8).setText(dist(idx + 8));
            }
            // { r-1, c }
            if((idx - 8) >= 0){ // check to make sure its not above the top row
                cell_tvs.get(idx - 8).setText(dist(idx - 8));
            }
            // { r, c+1 }
            if(!rightmost.contains(idx)){ //making sure its not past the rightmost column
                cell_tvs.get(idx + 1).setText(dist(idx + 1));
            }
            // { r, c-1 }
            if(!leftmost.contains(idx)){ //making sure its not before the leftmost column
                cell_tvs.get(idx - 1).setText(dist(idx - 1));
            }
            // { r+1, c+1 }
            if(idx < 72 && !rightmost.contains(idx)){ // not along rightmost column && not along bottom row
                cell_tvs.get(idx + 9).setText(dist(idx + 9));
            }
            // { r-1, c+1 }
            if(idx > 7 && !rightmost.contains(idx) ){ // not along rightmost column && not along top row
                cell_tvs.get(idx - 7).setText(dist(idx - 7));
            }
            // { r-1, c-1 }
            if(idx > 7 && !leftmost.contains(idx) ){ // not along leftmost column && not along top row
                cell_tvs.get(idx - 9).setText(dist(idx - 9));
            }
            // { r+1, c-1 }
            if(idx < 72 && !leftmost.contains(idx)){ // not along leftmost column && not along bottom row
                cell_tvs.get(idx + 7).setText(dist(idx + 7));
            }
        }
    }

    private String dist(int x){
        if(cell_tvs.get(x).getText() == "1") return "2";
        else if(cell_tvs.get(x).getText() == "2") return "3";
        else return "1";
    }


}