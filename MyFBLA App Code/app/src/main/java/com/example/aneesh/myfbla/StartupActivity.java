package com.example.aneesh.myfbla;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * A login screen that offers login via email/password.
 */
public class StartupActivity extends AppCompatActivity {

    // UI references.
    private Button loginButton;
    private Button signupButton;
    private Button guestButton;
    public boolean isGuest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        // Set up the login form.
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signUpButton);
        guestButton = findViewById(R.id.guestButton);
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(StartupActivity.this, SignupActivity.class);
            startActivity(intent);
        });
        guestButton.setOnClickListener(view -> {
//                new LoginActivity().loggedIn = false;

            SharedPreferences usernamePreferences;
            SharedPreferences.Editor usernameEditor;
            usernamePreferences = getApplication().getSharedPreferences("Score", MODE_PRIVATE);
            usernameEditor = usernamePreferences.edit();
            usernameEditor.putInt("score", 0);
            usernameEditor.apply();


            usernamePreferences = getApplication().getSharedPreferences("Questions", MODE_PRIVATE);
            usernameEditor = usernamePreferences.edit();
            usernameEditor.putInt("questions", 1);
            usernameEditor.apply();


            Intent intent = new Intent(StartupActivity.this, HomeActivity.class);
            intent.putExtra("name", "GuestCode10230213284");
            startActivity(intent);
        });
    }
}