package com.example.aneesh.myfbla;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HelpActivity extends AppCompatActivity {

    Button reportBug;
    TextView aboutUsText;
    AlertDialog alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        reportBug = findViewById(R.id.reportBug);
        aboutUsText = findViewById(R.id.aboutText);

        aboutUsText.setText(R.string.about_us_help);

        reportBug.setOnClickListener(view -> {
            Intent intent = new Intent(HelpActivity.this, ReportBugActivity.class);
            startActivity(intent);
        });
    }
}
