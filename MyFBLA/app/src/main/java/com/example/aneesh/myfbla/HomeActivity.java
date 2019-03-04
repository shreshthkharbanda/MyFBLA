package com.example.aneesh.myfbla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by shreshthkharbanda for MyFBLA.
 */
public class HomeActivity extends AppCompatActivity {

    TextView welcomeText;
    Button testButton;
    Button learnButton;
    Button blogButton;
    Button helpButton;
    Button logOutButton;
    String name;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        welcomeText = findViewById(R.id.welcome);
        testButton = findViewById(R.id.test);
        learnButton = findViewById(R.id.learn);
        blogButton = findViewById(R.id.blog);
        helpButton = findViewById(R.id.help);
        logOutButton = findViewById(R.id.logOutButton);

        intent = getIntent();
        try {
            name = intent.getStringExtra("name");
        } catch (Exception e) {
            e.printStackTrace();
            name = "";
        }
        if (name.equalsIgnoreCase("Guestcode10230213284")) {
            testButton.setAlpha(.5f);
            blogButton.setAlpha(.5f);
            welcomeText.setText("Welcome Guest!");
        } else {
            welcomeText.setText(String.format("Welcome %s!", name.substring(0, 1).toUpperCase() + name.substring(1, name.indexOf(" ")).toLowerCase()));
        }

        blogButton.setOnClickListener(view -> {
            intent = new Intent(HomeActivity.this, ForumActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        });

        learnButton.setOnClickListener(view -> {
            intent = new Intent(HomeActivity.this, LearnMainMenu.class);
            startActivity(intent);
        });

        testButton.setOnClickListener(view -> {
            if (name.equals("Guestcode10230213284")) {
                Toast.makeText(this, "Please login/sign up to access this feature.", Toast.LENGTH_SHORT).show();
            } else {
                intent = new Intent(HomeActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        helpButton.setOnClickListener(view -> {
            intent = new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(intent);
        });


        intent = getIntent();
        name = intent.getStringExtra("name");
        Log.v("ReceivedNameLearn", "NAME IS: " + name);
        try {
            try {
                name = String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1, name.indexOf(" ")).toLowerCase();
            } catch (IndexOutOfBoundsException ioobe) {
                name = String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1).toLowerCase();
            } finally {
            }
        } catch (IndexOutOfBoundsException ignored) {
            ignored.printStackTrace();
        }

        logOutButton.setOnClickListener(view -> {
            intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
