package com.example.aneesh.myfbla;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the adapter for the myAccount list
 *
 * @Shreshth Kharbanda
 */
public class CustomLogInAdapter extends SimpleAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> arrayList;
    private static final String TAG_BOOK_ID = "bookId";
    private static final String TAG_BOOLEAN_LIKED = "booleanLiked";
    private String liked;
    private InputStream is = null;
    private String result2 = null;

    private InputStream is2 = null;
    private String result3 = null;

    private HttpPost httpPost;
    private HttpClient httpClient;
    private int logInCode;
    private String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_BOOK_NAME = "bookTitle";
    private static final String TAG_USER = "userName";
    private static final String TAG_LIBRARY_ID = "libraryId";
    private static final String TAG_OUT = "dateCheckedOut";
    private static final String TAG_DUE = "dateDue";
    private static final String TAG_CHECKED_OUT_ID = "checkedOutId";
    private static final String TAG_LIKES = "likes";
    private static final String TAG_USER_FINE = "userFines";

    /**
     * sets up the login adapter.
     *
     * @param context
     * @param data
     * @param resource
     * @param from
     * @param to
     */
    CustomLogInAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.arrayList = data;
        LayoutInflater.from(context);
    }

    /**
     * loads and returns the current view for the app.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    /**
     * refreshes the app.
     */
    public void refresh() {
        //manipulate list
        notifyDataSetChanged();
    }

    /**
     * retrieves the books that have been checked out by the user.
     */
    /*
    public void getBooksOut() {
        @SuppressLint("StaticFieldLeak")
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                InputStream inputStream;
                String result;
                String dataUrl = "http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/booksCheckedOut.php";

                try {

                    HttpClient httpclient = new DefaultHttpClient();
                    httpPost = new HttpPost(dataUrl);
                    String json1;

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("user", new LogInFragment().mEmailView.getText().toString());

                    json1 = jsonObject.toString();
                    StringEntity se = new StringEntity(json1);
                    httpPost.setEntity(se);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");

                    HttpResponse httpResponse = httpclient.execute(httpPost);
                    inputStream = httpResponse.getEntity().getContent();
                    result = convertInputStreamToString(inputStream);
                    myJSON = result;
                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }
                httpPost.setHeader("Content-type", "application/json");

                inputStream = null;
                String result2 = null;
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity entity = httpResponse.getEntity();

                    inputStream = entity.getContent();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder stringBuilder = new StringBuilder();

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    result2 = stringBuilder.toString();
                } catch (NullPointerException | IOException npe) {
                    npe.printStackTrace();
                    logInCode = 0;
                } finally {
                    if (inputStream != null) try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logInCode = 0;
                    }
                }

                return result2;
            }

            @Override
            protected void onPostExecute(String result) {
                showBooksCheckedOut(myJSON);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    /**
     * displays all of the checked out books under the my account tab.
     *
     * @param jsonData recieves the jsonData for the checkedout books.
     */
    /*
    public void showBooksCheckedOut(String jsonData) {
        InputStream inputStream = null;
        String result = "";
        String dataUrl = "http://ec2-52-41-161-91.us-west-2.compute.amazonaws.com/booksCheckedOut.php";

        try {
            JSONObject jsonObj = new JSONObject(jsonData);

            JSONArray booksArray = jsonObj.getJSONArray(TAG_RESULTS);
            ArrayList<HashMap<String, String>> booksList = new ArrayList<>();

            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject c = booksArray.getJSONObject(i);

                // get all the book details in String format
                String bookName = c.getString(TAG_BOOK_NAME);
                String bookId = c.getString(TAG_BOOK_ID);
                String userName = c.getString(TAG_USER);
                String libraryIdDatabase = c.getString(TAG_LIBRARY_ID);
                String dateOut = c.getString(TAG_OUT);
                String dateDue = c.getString(TAG_DUE);
                String id = c.getString(TAG_CHECKED_OUT_ID);
                String likes = c.getString(TAG_LIKES);
                String booleanLiked = c.getString(TAG_BOOLEAN_LIKED);
                String userFine = c.getString(TAG_USER_FINE);
                // display the total fines for the yser
                new /*LogInFragment().userFineText.setText("Your current fines are: $" + userFine);
                // change font size and color if the user has any outstanding fines
                if (!userFine.equals("0")) {
                    new LogInFragment().userFineText.setTextColor(Color.RED);
                    new LogInFragment().userFineText.setTextSize(25);
                }


                final HashMap<String, String> persons = new HashMap<>();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date currentTime = Calendar.getInstance().getTime();

                // add all the book details into persons
                persons.put(TAG_BOOK_NAME, bookName);
                persons.put(TAG_BOOK_ID, bookId);
                persons.put(TAG_USER, userName);
                persons.put(TAG_LIBRARY_ID, libraryIdDatabase);
                persons.put(TAG_OUT, dateOut);
                persons.put(TAG_DUE, dateDue);
                persons.put(TAG_CHECKED_OUT_ID, id);
                persons.put(TAG_LIKES, "+" + likes);
                persons.put(TAG_BOOLEAN_LIKED, booleanLiked);
                persons.put(TAG_USER_FINE, userFine);

                // add the book to the booklist
                booksList.add(persons);
            }
            logInAdapter = new CustomLogInAdapter(context, booksList, R.layout.layout_account_list_item,
                    new String[]{TAG_BOOK_NAME, TAG_USER, TAG_OUT, TAG_DUE, TAG_CHECKED_OUT_ID, TAG_BOOK_ID, TAG_LIKES, TAG_BOOLEAN_LIKED},
                    new int[]{R.id.bookName, R.id.userName, R.id.outDate, R.id.dueDate, R.id.checkedOutId, R.id.bookId, R.id.numberOfLikes, R.id.booleanLiked}
            );

            try {
                LogInFragment.listAccount.setAdapter(logInAdapter);
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
                /*
    public String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result3 = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null)
            result3.append(line);

        inputStream.close();
        return result3.toString();

    }
*/

    /**
     * sets the httpClient to the fiven httpClient.
     *
     * @param httpClient recives the httpClient.
     */
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * returns the login code.
     *
     * @return returns the login code.
     */
    public int getLogInCode() {
        return logInCode;
    }

    /**
     * sets the login code to the given code.
     *
     * @param logInCode recieves the login code.
     */
    public void setLogInCode(int logInCode) {
        this.logInCode = logInCode;
    }
}
