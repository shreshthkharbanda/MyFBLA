package com.example.aneesh.myfbla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    Button reportBug;
    TextView aboutUsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        reportBug = findViewById(R.id.reportBug);
        aboutUsText = findViewById(R.id.aboutText);

        aboutUsText.setText(R.string.about_us_help);
    }
}
