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
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private int clock = 0;
    private int flags = 4;
    private String mode = "picker";
    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cell_tvs = new ArrayList<TextView>();
        // Method (3): add four dynamically created cells with LayoutInflater
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<=9; i++) {
            for (int j=0; j<=7; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GREEN);
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
            int randomIndex = generator.nextInt(cell_tvs.size());
            cell_tvs.get(randomIndex).setText("bomb");
        }
        runTimer();
        updateFlagCount();
        final ImageButton img = (ImageButton) findViewById(R.id.button);
        img.setBackgroundResource(R.drawable.picker);


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
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.parseColor("lime"));
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

   /* public int minesweeperBFS(TextView tv){
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;


    }*/


}