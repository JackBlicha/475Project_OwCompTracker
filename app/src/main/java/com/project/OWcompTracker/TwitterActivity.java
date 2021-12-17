package com.project.OWcompTracker;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod; //make textviews scrollable
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TwitterActivity extends AppCompatActivity {
    TextView body;
    TextView body2;
    Button refreshButton;
    String result;
    String stringURL;
    Boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);

        refreshButton = findViewById(R.id.twitter_refresh_button);
        body = findViewById(R.id.twitter_body);
        body2 = findViewById(R.id.twitter_body2);

        stringURL = "https://api.twitter.com/2/tweets/search/recent?query=from:overwatchleague";
        result = getString(R.string.twitter_body_default);
        firstRun = true;

        // show default text for tweet placeholders
        body.setText(result);
        body2.setText(result);


        //make textviews scrollable
        body.setMovementMethod(new ScrollingMovementMethod());
        body2.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void refreshTweetClick(View view) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        URL url = new URL(stringURL);
                        result = getResponseFromHttpUrl(url);
                    } catch (IOException e) {
                        Log.e("Twitter", "refreshTweetClick: error caught: failed to execute curl command");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


        try {
            JSONObject translatedToJson = toJSON(result);
            JSONArray myJsonArray = translatedToJson.getJSONArray("data");

            result = myJsonArray.getJSONObject(0).getString("text");
            body.setText(result);

            result = myJsonArray.getJSONObject(1).getString("text");
            body2.setText(result);

        } catch (JSONException e){
            body.setText(R.string.JSONException);
            body2.setText(R.string.JSONException);
            if(firstRun){
                firstRun = false;
                refreshTweetClick(view);
            }
        }

    }

    public JSONObject toJSON(String input) throws JSONException {
        return new JSONObject(input);
    }


    // My twitter api curl request as a static function
    //source/help: https://stackoverflow.com/questions/46803286/run-curl-from-java-android
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        //my twitter api curl request:
        //curl --request GET "https://api.twitter.com/2/tweets/search/recent?query=from:overwatchleague" --header "Authorization: Bearer AAAAAAAAAAAAAAAAAAAAAFdlWgEAAAAAnvenQl9WsiX%2FmwWJSRAIAE%2Fugp8%3D9zZnW4GUQgxsxZ1t9tMjqqtWpBytFLngMJ3KSIkMHXN3dwsiur"
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Content-Type","application/json");
        connection.addRequestProperty("Authorization","Bearer AAAAAAAAAAAAAAAAAAAAAFdlWgEAAAAAnvenQl9WsiX%2FmwWJSRAIAE%2Fugp8%3D9zZnW4GUQgxsxZ1t9tMjqqtWpBytFLngMJ3KSIkMHXN3dwsiur");

        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

}

