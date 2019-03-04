package com.example.aneesh.myfbla;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText logInEdit;
    TextInputEditText logInPasswordEdit;
    Button logInButton;
    boolean validCredentials;
    boolean loggedIn;

    public String TAG_USER_ID = "userId";
    public String TAG_NAME = "userName";
    public String TAG_EMAIL = "userEmail";
    public String TAG_PASS = "userPassword";
    String email = "";
    String password = "";
    String finalName = "";
    String myJSON = "";
    String userName = "Guest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        logInEdit = findViewById(R.id.logInEdit);
        logInPasswordEdit = findViewById(R.id.logInPasswordEdit);
        logInButton = findViewById(R.id.email_sign_in_button);

        emptyCreds();

        logInButton.setOnClickListener(view -> {
            String userEmail = Objects.requireNonNull(logInEdit.getText()).toString();
            String userPass = Objects.requireNonNull(logInPasswordEdit.getText()).toString();

            if (!Objects.equals(userEmail, "") && !Objects.equals(userPass, "")) {
                Log.v("LoginStarted", "Checking... ");
                checkCredentials(userEmail, userPass);
            } else {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public void emptyCreds() {
        logInEdit.setText("");
        logInPasswordEdit.setText("");
    }

    /**
     * This method checks the credentials of the user that is attempting to log in
     *
     * @param email
     * @param password
     */
    public void checkCredentials(String email, String password) {
        new Login().execute(email, password);
    }

    /**
     * This class is used in the checkCredentials method for LogIn and uses AsyncTask of type String, String, String
     */
    @SuppressLint("StaticFieldLeak")
    private class Login extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL dataUrl = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                dataUrl = new URL("http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/checkUsernamePassword.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Malformed URL Exception", Toast.LENGTH_LONG).show();
                return "exception";
            }
            try {
                conn = (HttpURLConnection) dataUrl.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    return (result.toString());
                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Input-Output Exception", Toast.LENGTH_LONG).show();
                    }
                });
                return "IOException";
            } finally {
                conn.disconnect();
            }
        }

        /**
         * After doInBackground, this method is executed and updates the user interface layer with the result
         *
         * @param insideResult
         */
        @Override
        protected void onPostExecute(final String insideResult) {

            LoginActivity.this.runOnUiThread(() -> {
                Handler mHandler = new Handler();
                mHandler.postDelayed(() -> {
                    if (Objects.requireNonNull(logInEdit.getText()).toString().equals("") || Objects.requireNonNull(logInPasswordEdit.getText()).toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Invalid credentials... Please try again", Toast.LENGTH_LONG).show();
                        setContentView(R.layout.login_screen);
                        validCredentials = false;
                        loggedIn = false;
                    } else if (insideResult.toLowerCase().contains("true")) {
                        Toast.makeText(getApplicationContext(), "Logged In Successfully", Toast.LENGTH_LONG).show();
                        validCredentials = true;
                        loggedIn = true;
                        email = logInEdit.getText().toString();
                        password = logInPasswordEdit.getText().toString();


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

                        getName();
                    } else if (insideResult.toLowerCase().contains("false")) {
                        Toast.makeText(getApplicationContext(), "Invalid credentials... Please try again", Toast.LENGTH_LONG).show();
                        setContentView(R.layout.login_screen);
                        validCredentials = false;
                        loggedIn = false;
                    } else if (insideResult.equalsIgnoreCase("exception") || insideResult.equalsIgnoreCase("unsuccessful")) {
                        Toast.makeText(getApplicationContext(), "Oops! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
                        setContentView(R.layout.login_screen);
                        validCredentials = false;
                        loggedIn = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid credentials... Please try again", Toast.LENGTH_LONG).show();
                        setContentView(R.layout.login_screen);
                        validCredentials = false;
                        loggedIn = false;
                    }
                }, 1500);
            });
        }
    }


    /**
     * gets all of the books from the database.
     */
    public String getName() {
        @SuppressLint("StaticFieldLeak")
        class GetAllBooksClass extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String result = "";
                InputStream inputStream = null;
                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/getName.php");
                httppost.setHeader("MyFBLA Users", "MyFBLA Users JSON Values");

                try {
                    HttpResponse httpResonse = httpClient.execute(httppost);
                    HttpEntity httpEntity = httpResonse.getEntity();

                    inputStream = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    result = stringBuilder.toString();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Couldn't retrieve information from database", Toast.LENGTH_SHORT).show();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                finalName = setName();
            }
        }
        GetAllBooksClass g = new GetAllBooksClass();
        g.execute();
        Log.v("Name", "User's name is " + finalName);
        return finalName;
    }

    private String setName() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            JSONArray peoples = jsonObj.getJSONArray("result");

            // loop through each person in the database
            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                // get the basic information about each person
                String id = c.getString(TAG_USER_ID);
                String name = c.getString(TAG_NAME);
                String userEmail = c.getString(TAG_EMAIL);
                String password = c.getString(TAG_PASS);

                // email matches the email entered!
                if (userEmail.equalsIgnoreCase(email)) {
                    userName = name;
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("name", userName);
                    startActivity(intent);
                    return name;
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return userName;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, StartupActivity.class);
        startActivity(intent);
    }
}
