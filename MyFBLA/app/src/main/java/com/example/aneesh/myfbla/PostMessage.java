package com.example.aneesh.myfbla;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PostMessage extends AppCompatActivity {

    final HttpClient[] httpclient = new HttpClient[1];
    String category;
    String result;
    InputStream inputStream;
    Button postButton;
    TextInputEditText messageEdit;
    TextInputEditText nameEdit;
    String name;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);

        postButton = findViewById(R.id.postButton);
        messageEdit = findViewById(R.id.messageEdit);
        nameEdit = findViewById(R.id.nameEdit);
        cancel = findViewById(R.id.cancelButton);

        getMessages();

        postButton.setOnClickListener(view -> {
            if (!(Objects.requireNonNull(messageEdit.getText()).toString().isEmpty() || Objects.requireNonNull(nameEdit.getText()).toString().isEmpty())) {
                Log.v("PostButton", "CLICKED! msg: " + Objects.requireNonNull(messageEdit.getText()).toString() + "| author: " + Objects.requireNonNull(nameEdit.getText()).toString());
                postMessage(Objects.requireNonNull(messageEdit.getText()).toString(), nameEdit.getText().toString(), "http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/postMessage.php");
                Intent intent1 = new Intent(PostMessage.this, ForumActivity.class);
                startActivity(intent1);
            } else {
                Toast.makeText(this, "Please fill out both fields!", Toast.LENGTH_LONG).show();
            }
        });

        cancel.setOnClickListener(view -> {
            Intent intent1 = new Intent(PostMessage.this, ForumActivity.class);
            startActivity(intent1);
        });
    }

    /**
     * gets all of the books from the database.
     */
    public void getMessages() {
        @SuppressLint("StaticFieldLeak")
        class GetAllBooksClass extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost("http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/getFblaMessages.php");
                httppost.setHeader("BookDatabase Books", "BookDatabase JSON");

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
                } finally {
                    if (inputStream != null) try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                showBooksCheckedOut(result);
            }
        }
        GetAllBooksClass g = new GetAllBooksClass();
        g.execute();
    }

    public void showBooksCheckedOut(String jsonData) {
        try {
            JSONObject jsonObj = new JSONObject(jsonData);

            JSONArray booksArray = jsonObj.getJSONArray("result");
            ArrayList<HashMap<String, String>> booksList = new ArrayList<>();

            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject c = booksArray.getJSONObject(i);

                String messageText = c.getString("messageText");
                String messageAuthor = c.getString("authorName");

                final HashMap<String, String> persons = new HashMap<>();

                persons.put("messageText", messageText);
                persons.put("authorName", messageAuthor);

                booksList.add(persons);
            }
//          Use Custom Log In Adapter to setup the variable logInAdapter
            CustomLogInAdapter logInAdapter = new CustomLogInAdapter(getApplicationContext(), booksList, R.layout.layout_forum_list_item,
                    new String[]{"messageText", "authorName"},
                    new int[]{R.id.messageText, R.id.authorName}
            );

//          Set Adapter for books checked out list
            try {
                ListView questions = findViewById(R.id.messagesList);
                questions.setAdapter(logInAdapter);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void postMessage(final String... args) {
        @SuppressLint("StaticFieldLeak")
        class PostMessageClass extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                List<NameValuePair> signUpPair = new ArrayList<>();
                signUpPair.add(new BasicNameValuePair("message", args[0]));
                signUpPair.add(new BasicNameValuePair("name", args[1]));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(args[args.length - 1]);
                    httpPost.setEntity(new UrlEncodedFormEntity(signUpPair));
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(getApplicationContext(), "Message Posted!", Toast.LENGTH_SHORT).show();
                return "success";
            }
        }
        PostMessageClass postMessageClass = new PostMessageClass();
        postMessageClass.execute();
    }
}
