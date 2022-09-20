package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

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
    private ArrayList<Integer> rightmost = new ArrayList<>();
    private ArrayList<Integer> leftmost = new ArrayList<>();
    private ArrayList<Integer> foundCells = new ArrayList<>();


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
                tv.setTextColor(00000000);
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
            cell_tvs.get(randomIndex).setTextColor(00000000);
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
                //YOU LOSE
                for(int i = 0; i < bombs.size(); i++){
                    bombs.get(i).setBackgroundResource(R.drawable.bomb_30x30);
                    tv.setTextColor(00000000);
                }
                //GO TO LOSING PAGE
                Intent intent = new Intent(this, LosingPage.class);
                intent.putExtra("com.example.gridlayout.CLOCK", Integer.toString(clock));
                startActivity(intent);
            }
            else{
                if(foundCells.size() == 76){
                    //GO TO WINNING PAGE
                    Intent intent = new Intent(this, WinningPage.class);
                    intent.putExtra("com.example.gridlayout.CLOCK", Integer.toString(clock));
                    startActivity(intent);
                }
                BFS(tv);
            }
        }
        if(mode == "flag"){
            if(tv.getText() == "flagged"){
                tv.setBackgroundColor(Color.GREEN);
                tv.setText("notFlagged");
                tv.setTextColor(00000000);
                flags++;
            }
            else {
                if(flags != 0) {
                    tv.setTextColor(Color.RED);
                    tv.setText("flagged");
                    tv.setTextColor(00000000);
                    tv.setBackgroundResource(R.drawable.flag_1_30x20);
                    flags--;
                }
            }
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
        for(int i = 7; i < 81; i+=8) {rightmost.add(i);}
        for(int i = 0; i < 80; i+=8) {leftmost.add(i);}
        for(int i = 0; i < bombs.size(); i++){
            ArrayList<TextView> adjList = adjCells(bombs.get(i));
            for(int j = 0; j < adjList.size(); j++){
                Integer idx = findIndexOfCellTextView(adjList.get(j));
                cell_tvs.get(idx).setText(dist(idx));
            }
        }
    }

    private String dist(int x){
        if(cell_tvs.get(x).getText() == "1") return "2";
        else if(cell_tvs.get(x).getText() == "2") return "3";
        else return "1";
    }

    private ArrayList<TextView> adjCells(TextView tv){
        int idx = findIndexOfCellTextView(tv);
        ArrayList<TextView> res = new ArrayList<>();
        // { r+1, c }
        if((idx+8) < 80) { // check to make sure its not past the bottom row
            res.add(cell_tvs.get(idx+8));
        }
        // { r-1, c }
        if((idx - 8) >= 0){ // check to make sure its not above the top row
            res.add(cell_tvs.get(idx-8));
        }
        // { r, c+1 }
        if(!rightmost.contains(idx)){ //making sure its not past the rightmost column
            res.add(cell_tvs.get(idx+1));
        }
        // { r, c-1 }
        if(!leftmost.contains(idx)){ //making sure its not before the leftmost column
            res.add(cell_tvs.get(idx-1));
        }
        // { r+1, c+1 }
        if(idx < 72 && !rightmost.contains(idx)){ // not along rightmost column && not along bottom row
            res.add(cell_tvs.get(idx+9));
        }
        // { r-1, c+1 }
        if(idx > 7 && !rightmost.contains(idx) ){ // not along rightmost column && not along top row
            res.add(cell_tvs.get(idx-7));
        }
        // { r-1, c-1 }
        if(idx > 7 && !leftmost.contains(idx) ){ // not along leftmost column && not along top row
            res.add(cell_tvs.get(idx-9));
        }
        // { r+1, c-1 }
        if(idx < 72 && !leftmost.contains(idx)){ // not along leftmost column && not along bottom row
            res.add(cell_tvs.get(idx+7));
        }
        return res;
    }

    private void BFS(TextView tv){

        Queue<TextView> q = new LinkedList<>();
        ArrayList<TextView> turnGray = new ArrayList<>();
        q.add(tv);
        while(!q.isEmpty()){
            TextView temp = q.poll();
            turnGray.add(temp);
            if(!foundCells.contains(findIndexOfCellTextView(temp))) {
                foundCells.add(findIndexOfCellTextView(temp));
            }
            if(temp.getText() != "1" && temp.getText() != "2" && temp.getText() != "3" && temp.getText() != "bomb"){
                temp.setText("found");

                ArrayList<TextView> adjCellsList = adjCells(temp);
                for(int i = 0; i < adjCellsList.size(); i++){
                    turnGray.add(adjCellsList.get(i));
                    if (adjCellsList.get(i).getText() != "1" && adjCellsList.get(i).getText()
                            != "2" && adjCellsList.get(i).getText() != "3" && adjCellsList.get(i).getText() != "bomb" && adjCellsList.get(i).getText() != "found") {
                        q.add(adjCellsList.get(i));
                        adjCellsList.get(i).setText("found");
                    }
                    if(!foundCells.contains(findIndexOfCellTextView(adjCellsList.get(i)))) {
                        foundCells.add(findIndexOfCellTextView(adjCellsList.get(i)));
                    }
                }
            }
        }

        for(int i = 0; i < turnGray.size(); i++){
            if(turnGray.get(i).getText() != "bomb") {
                turnGray.get(i).setBackgroundColor(Color.parseColor("gray"));
                turnGray.get(i).setTextColor(Color.BLACK);
            }
            if(turnGray.get(i).getText() != "1" && turnGray.get(i).getText() != "2" && turnGray.get(i).getText() != "3") {
                turnGray.get(i).setTextColor(Color.parseColor("gray"));
            }
        }
    }
}