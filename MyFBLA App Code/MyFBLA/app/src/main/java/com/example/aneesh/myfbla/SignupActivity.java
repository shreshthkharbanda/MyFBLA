package com.example.aneesh.myfbla;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    EditText signUpFirst;
    EditText signUpLast;
    EditText signUpEmail;
    EditText signUpPassword;
    EditText signUpConfirmPassword;
    Button signUp;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpFirst = findViewById(R.id.signUpFirst);
        signUpLast = findViewById(R.id.signUpLast);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPassword = findViewById(R.id.signUpPassword);
        signUpConfirmPassword = findViewById(R.id.signUpPassword2);
        signUp = findViewById(R.id.signUpButton);

        signUp.setOnClickListener(view -> {

            //  Validating sign up information entered and if it is all valid, register the user into the database.
            /*
            if ((signUpPassword.getText().toString().equals(signUpConfirmPassword.getText().toString()) && !(signUpFirst == null)
                    && !(signUpLast == null) && !(signUpEmail == null) && !(signUpPassword == null)
                    && !(signUpConfirmPassword == null)) &&
                    (Patterns.EMAIL_ADDRESS.matcher(signUpEmail.getText().toString()).matches())) {
                    */
            Toast.makeText(SignupActivity.this, "Starting!", Toast.LENGTH_SHORT).show();
            registerUser(signUpFirst.getText().toString(), signUpLast.getText().toString(),
                    signUpEmail.getText().toString(), signUpPassword.getText().toString(),
                    "http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/signUpLibrary.php");
            Toast.makeText(SignupActivity.this, "Done!", Toast.LENGTH_SHORT).show();
            Log.v("dataSignUp", signUpFirst.getText().toString() + " " + signUpLast.getText().toString() + " " +
                    signUpEmail.getText().toString() + " " + signUpPassword.getText().toString());

            Toast.makeText(getApplicationContext(), "Account Successfully Created!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            /*
            } else {
                try {
                    if (result.equalsIgnoreCase("Failed") || result.equalsIgnoreCase("failure")) {
                        Toast.makeText(getApplicationContext(), "That email already exists!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    if (signUpPassword.getText().toString().equals("") || signUpConfirmPassword.getText().toString().equals("") ||
                            signUpFirst.getText().toString().equals("") || signUpLast.getText().toString().equals("") || signUpEmail.getText().toString().equals("") ||
                            signUpPassword.getText().toString().equals("") || signUpConfirmPassword.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please fill out all forms!", Toast.LENGTH_LONG).show();
                    } else if (!(signUpPassword.getText().toString().equals(signUpConfirmPassword.getText().toString()))) {
                        Toast.makeText(getApplicationContext(), "Please make sure both the passwords match!", Toast.LENGTH_LONG).show();
                    } else if (!(Patterns.EMAIL_ADDRESS.matcher(signUpEmail.getText().toString()).matches())) {
                        Toast.makeText(getApplicationContext(), "Please make sure the email address is valid!", Toast.LENGTH_LONG).show();
                    }
                }
            }
            */
        });
    }

    private void registerUser(final String... args) {
        @SuppressLint("StaticFieldLeak")
        class SignUpAsync extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> signUpPair = new ArrayList<>();
                signUpPair.add(new BasicNameValuePair("userFirstName", args[0]));
                signUpPair.add(new BasicNameValuePair("userLastName", args[1]));
                signUpPair.add(new BasicNameValuePair("userEmail", args[2]));
                signUpPair.add(new BasicNameValuePair("userPassword", args[3]));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(args[args.length - 1]);
                    httpPost.setEntity(new UrlEncodedFormEntity(signUpPair));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "success";
            }
        }
        SignUpAsync signUpAsync = new SignUpAsync();
        signUpAsync.execute();
    }
}
