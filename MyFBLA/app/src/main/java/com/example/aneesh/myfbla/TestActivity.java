package com.example.aneesh.myfbla;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TestActivity extends AppCompatActivity {

    final HttpClient[] httpclient = new HttpClient[1];
    RadioButton option1;
    RadioButton option2;
    RadioButton option3;
    RadioButton option4;
    TextView questionText;
    Button submitButton;
    ProgressBar progressBar;
    TextView progressText;
    TextView timeText;
    public int totalQuestions = 1;
    public int score = 0;
    public boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        questionText = findViewById(R.id.questionText);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        timeText = findViewById(R.id.timeText);
        submitButton = findViewById(R.id.submitButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        getSingleQuestion(String.valueOf(new Random().nextInt(41) + 1));

        SharedPreferences getSavedUsername = getApplication().getSharedPreferences("Questions", MODE_PRIVATE);
        int totalQuestions = getSavedUsername.getInt("questions", 1);
        progressText.setText("Question " + totalQuestions + "/10");

        getSavedUsername = getApplication().getSharedPreferences("Score", MODE_PRIVATE);
        score = getSavedUsername.getInt("score", 0);
        progressBar.setProgress(score * 10);

        new CountDownTimer(16 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeText.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (!finished) {
                    SharedPreferences getSavedUsername = getApplication().getSharedPreferences("Score", MODE_PRIVATE);
                    int score = getSavedUsername.getInt("score", 0);

                    progressBar.setProgress(score * 10);
                    (new Handler()).postDelayed(TestActivity.this::reloadQuestion, 2500);
                }
//                Intent intent = new Intent(TestActivity.this, TestActivity.class);
//                startActivity(intent);
            }
        }.start();
    }

    /*
     * This method is used to retrieve the books that are checked out from the MySQL database and is done through a php code hosted on AWS
     * and this helps make it secure since only our IP addresses can access the php code and the website.
     * Author: Shreshth Kharbanda
     */
    public void getSingleQuestion(final String questionId) {
        final String[] myJSON = {""};
        final HttpPost[] httpPost = new HttpPost[1];

//      Suppress StaticFieldLeaks
        @SuppressLint("StaticFieldLeak")

//      AsyncTask Class of Type String, Void, String
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

//              Defining local variables and initializing the url of the php code
                InputStream inputStream;
                String result;
                String dataUrl = "http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/getTestQuestions.php";

//              Uses HttpPost to send the email over to the php code as an input to get the books checked out
                try {
                    httpclient[0] = new DefaultHttpClient();
                    httpPost[0] = new HttpPost(dataUrl);
                    String json1;

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("questionId", questionId);

                    json1 = jsonObject.toString();
                    StringEntity se = new StringEntity(json1);
                    httpPost[0].setEntity(se);
                    httpPost[0].setHeader("Accept", "application/json");
                    httpPost[0].setHeader("Content-type", "application/json");

//                  Retrieves the result and what is returned from the php code and stores the string in MyJSON
                    HttpResponse httpResponse = httpclient[0].execute(httpPost[0]);
                    inputStream = httpResponse.getEntity().getContent();
                    result = convertInputStreamToString(inputStream);
                    myJSON[0] = result;
                } catch (Exception e) {
//                  Exception and logged in debug portion of logcat
                    Log.d("InputStream", e.getLocalizedMessage());
                }
//              Set header for the HttpPost process
                httpPost[0].setHeader("Content-type", "application/json");

                inputStream = null;
                String result2 = null;

//              Retrieves the data from the php code to get all the information about the books checked out by the user
                try {
                    HttpResponse httpResponse = httpclient[0].execute(httpPost[0]);
                    HttpEntity entity = httpResponse.getEntity();

                    inputStream = entity.getContent();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    result2 = stringBuilder.toString();
                } catch (NullPointerException npe) {
                    npe.printStackTrace();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Input-Output Exception", Toast.LENGTH_LONG).show();
                        }
                    });

                } finally {
                    if (inputStream != null) try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }

                return result2;
            }

            //  Calls the method showBooksCheckedOut and gives an input of the JSON data
            @Override
            protected void onPostExecute(String result) {
                showBooksCheckedOut(myJSON[0]);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    /**
     * This method shows the data gotten and populates the listview with the data
     *
     * @param jsonData
     * @return
     */
    public void showBooksCheckedOut(String jsonData) {
        try {
            JSONObject jsonObj = new JSONObject(jsonData);

            JSONArray booksArray = jsonObj.getJSONArray("result");
            ArrayList<HashMap<String, String>> booksList = new ArrayList<>();

//          for loop to get the string values from the json object
            for (int i = 0; i < booksArray.length(); i++) {
                Log.v("Book Loop", booksArray.getJSONObject(i).toString());
                JSONObject c = booksArray.getJSONObject(i);

                String questionId = c.getString("questionId");
                String question = c.getString("question");
                String questionCategory = c.getString("questionCategory");
                String correctOption = c.getString("correctOption");
                String option2Text = c.getString("option2");
                String option3Text = c.getString("option3");
                String option4Text = c.getString("option4");

                final HashMap<String, String> persons = new HashMap<>();

                persons.put("questionId", questionId);
                persons.put("question", question);
                persons.put("questionCategory", questionCategory);
                persons.put("correctOption", correctOption);
                persons.put("option2", option2Text);
                persons.put("option3", option3Text);
                persons.put("option4", option4Text);

                booksList.add(persons);
            }

            String option2String = "";
            String option3String = "";
            String option4String = "";
            String correctOptionString = "";
            List<String> answersList = new ArrayList<>();

            for (HashMap<String, String> map : booksList) {
                final boolean[] clicked = {false};
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    clicked[0] = false;
                    if (!entry.getKey().toLowerCase().contains("category")) {
                        if (entry.getKey().toLowerCase().contains("question")) {
                            questionText.setText(entry.getValue());
                        }
                        if (entry.getKey().equalsIgnoreCase("correctOption")) {
                            correctOptionString = entry.getValue();
                        }
                        if (entry.getKey().toLowerCase().contains("option2")) {
                            option2String = entry.getValue();
                        }
                        if (entry.getKey().toLowerCase().contains("option3")) {
                            option3String = entry.getValue();
                        }
                        if (entry.getKey().toLowerCase().contains("option4")) {
                            option4String = entry.getValue();
                        }
                    }
                }
                answersList.add(option2String);
                answersList.add(option3String);
                answersList.add(option4String);
                answersList.add(correctOptionString);
                Collections.shuffle(answersList);

                option1.setText(answersList.get(0));
                option2.setText(answersList.get(1));
                option3.setText(answersList.get(2));
                option4.setText(answersList.get(3));

                final String finalCorrectOptionString = correctOptionString;
                final String finalCorrectOptionString1 = correctOptionString;
                final String finalCorrectOptionString2 = correctOptionString;
                submitButton.setOnClickListener(view -> {
                    finished = true;
                    clicked[0] = true;

                    SharedPreferences usernamePreferences;
                    SharedPreferences.Editor usernameEditor;
                    SharedPreferences getSavedUsername = getApplication().getSharedPreferences("Score", MODE_PRIVATE);
                    int score = getSavedUsername.getInt("score", 0);


                    String checkedOption = "";
                    if (option1.isChecked())
                        checkedOption = option1.getText().toString();
                    if (option2.isChecked())
                        checkedOption = option2.getText().toString();
                    if (option3.isChecked())
                        checkedOption = option3.getText().toString();
                    if (option4.isChecked())
                        checkedOption = option4.getText().toString();

                    if (checkedOption.equalsIgnoreCase(finalCorrectOptionString1)) {
                        Toast.makeText(TestActivity.this, "Correct!", Toast.LENGTH_SHORT).show();

                        usernamePreferences = getApplication().getSharedPreferences("Score", MODE_PRIVATE);
                        usernameEditor = usernamePreferences.edit();
                        usernameEditor.putInt("score", ++score);
                        usernameEditor.apply();
                    } else {
                        if (option1.getText().toString().equalsIgnoreCase(finalCorrectOptionString))
                            option1.setBackgroundColor(Color.GREEN);
                        if (option2.getText().toString().equalsIgnoreCase(finalCorrectOptionString))
                            option2.setBackgroundColor(Color.GREEN);
                        if (option3.getText().toString().equalsIgnoreCase(finalCorrectOptionString))
                            option3.setBackgroundColor(Color.GREEN);
                        if (option4.getText().toString().equalsIgnoreCase(finalCorrectOptionString))
                            option4.setBackgroundColor(Color.GREEN);
                        Toast.makeText(TestActivity.this, "Incorrect. Correct Answer: " + finalCorrectOptionString2, Toast.LENGTH_LONG).show();
                    }
                    progressBar.setProgress(score * 10);
                    (new Handler()).postDelayed(TestActivity.this::reloadQuestion, 2500);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void reloadQuestion() {
        SharedPreferences getSavedUsername = getApplication().getSharedPreferences("Questions", MODE_PRIVATE);
        totalQuestions = getSavedUsername.getInt("questions", 1);
        if (totalQuestions >= 10) {
            Intent intent = new Intent(TestActivity.this, ResultsActivity.class);
            intent.putExtra("testScore", score);
            startActivity(intent);
        } else {
            SharedPreferences usernamePreferences;
            SharedPreferences.Editor usernameEditor;
            usernamePreferences = getApplication().getSharedPreferences("Questions", MODE_PRIVATE);
            usernameEditor = usernamePreferences.edit();
            totalQuestions++;
            usernameEditor.putInt("questions", totalQuestions);
            usernameEditor.apply();

            Intent intent = new Intent(TestActivity.this, TestActivity.class);
            startActivity(intent);
        }
    }


    /**
     * converts the inputStream into a String and returns the String format of the
     * inputStream.
     *
     * @param inputStream the input stream that needs to be converted into a String.
     * @return returns the String format of the inputStream.
     * @throws IOException
     */
    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result3 = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result3.append(line);

        inputStream.close();
        return result3.toString();

    }
}
