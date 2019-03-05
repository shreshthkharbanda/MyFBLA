package com.example.aneesh.myfbla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    TextView correct;
    TextView total;
    int score;
    Button goHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        correct = findViewById(R.id.correctText);
        total = findViewById(R.id.totalText);
        goHome = findViewById(R.id.homeButton);

        goHome.setOnClickListener(view -> {
            Intent intent = new Intent(ResultsActivity.this, LearnMainMenu.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        try {
            score = intent.getIntExtra("testScore", 0);
        } catch (Exception e) {
            e.printStackTrace();
            score = 0;
        }

        correct.setText(score + " questions were answered correctly out of 10!");
        if(score > 9)
            total.setText("Congrats! You are on your way to becoming an FBLA leader");
        else if(score >= 7)
            total.setText("You are so close! Keep up the good work!");
        else
            total.setText("Nice try! Use the LEARN tab on the home page to study more about FBLA.");
    }
}
