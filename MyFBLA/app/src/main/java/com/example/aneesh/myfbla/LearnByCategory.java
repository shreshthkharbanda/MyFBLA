package com.example.aneesh.myfbla;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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
import java.util.HashMap;

public class LearnByCategory extends AppCompatActivity {

    final HttpClient[] httpclient = new HttpClient[1];
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_by_category);
        Intent intent = getIntent();

        try {
            category = intent.getStringExtra("category");
        } catch (Exception e) {
            e.printStackTrace();
            category = "A";
        }
        getQuestions(category);

    }

    /*
     * This method is used to retrieve the books that are checked out from the MySQL database and is done through a php code hosted on AWS
     * and this helps make it secure since only our IP addresses can access the php code and the website.
     * Author: Shreshth Kharbanda
     */
    public void getQuestions(final String categoryName) {
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
                String dataUrl = "http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/getQuestions.php";

//              Uses HttpPost to send the email over to the php code as an input to get the books checked out
                try {
                    httpclient[0] = new DefaultHttpClient();
                    httpPost[0] = new HttpPost(dataUrl);
                    String json1;

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("category", categoryName);

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
                showBooksCheckedOut(myJSON[0], categoryName);
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
    public void showBooksCheckedOut(String jsonData, String categoryName) {
//        String user = categoryName;
//        InputStream inputStream = null;
//        String result = "";
        try {
            JSONObject jsonObj = new JSONObject(jsonData);

            JSONArray booksArray = jsonObj.getJSONArray("result");
            ArrayList<HashMap<String, String>> booksList = new ArrayList<>();

//          for loop to get the string values from the json object
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject c = booksArray.getJSONObject(i);

                String questionId = c.getString("questionId");
                String question = c.getString("question");
                String questionCategory = c.getString("questionCategory");
                String correctOption = c.getString("correctOption");
                String option2 = c.getString("option2");
                String option3 = c.getString("option3");
                String option4 = c.getString("option4");

                final HashMap<String, String> persons = new HashMap<>();

                persons.put("questionId", questionId);
                persons.put("question", question);
                persons.put("questionCategory", questionCategory);
                persons.put("correctOption", correctOption);
                persons.put("option2", option2);
                persons.put("option3", option3);
                persons.put("option4", option4);

                booksList.add(persons);
            }
//          Use Custom Log In Adapter to setup the variable logInAdapter
            CustomLogInAdapter logInAdapter = new CustomLogInAdapter(getApplicationContext(), booksList, R.layout.layout_learn_list_item,
                    new String[]{"question", "correctOption"},
                    new int[]{R.id.question, R.id.answer}
            );

//          Set Adapter for books checked out list
            try {
                ListView questions = findViewById(R.id.questionsList);
                questions.setAdapter(logInAdapter);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
