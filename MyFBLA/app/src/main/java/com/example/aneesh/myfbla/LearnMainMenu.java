package com.example.aneesh.myfbla;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LearnMainMenu extends AppCompatActivity {

    Button cat1;
    Button cat2;
    Button cat3;
    Button cat4;
    Button cat5;
    Button cat6;
    Button cat7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_main_menu);

        cat1 = findViewById(R.id.category1);
        cat2 = findViewById(R.id.category2);
        cat3 = findViewById(R.id.category3);
        cat4 = findViewById(R.id.category4);
        cat5 = findViewById(R.id.category5);
        cat6 = findViewById(R.id.category6);
//        cat7 = findViewById(R.id.category7);

        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LearnMainMenu.this, LearnByCategory.class);
                intent.putExtra("category", "A");
                startActivity(intent);
            }
        });

        cat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LearnMainMenu.this, LearnByCategory.class);
                intent.putExtra("category", "B");
                startActivity(intent);
            }
        });

        cat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LearnMainMenu.this, LearnByCategory.class);
                intent.putExtra("category", "C");
                startActivity(intent);
            }
        });

        cat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LearnMainMenu.this, LearnByCategory.class);
                intent.putExtra("category", "D");
                startActivity(intent);
            }
        });

        cat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LearnMainMenu.this, LearnByCategory.class);
                intent.putExtra("category", "E");
                startActivity(intent);
            }
        });

        cat6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(LearnMainMenu.this, LearnByCategory.class);
                intent.putExtra("category", "F");
                startActivity(intent);
            }
        });
    }
}
