package com.example.aneesh.myfbla;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ReportBugActivity extends AppCompatActivity {

    EditText editText;
    Button reportBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_bug);

        editText = findViewById(R.id.explainBug);
        reportBug = findViewById(R.id.submitBugButton);

        reportBug.setOnClickListener(view -> {


            Toast.makeText(getApplicationContext(), "You're bug has been received. Thank you!", Toast.LENGTH_LONG).show();
            editText.setText("");
            Intent intent = new Intent(ReportBugActivity.this, HomeActivity.class);
            startActivity(intent);
            /*
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("4045106272", null, editText.getText().toString(), null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }*/
        });
    }
}

