package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.facebook.stetho.inspector.jsonrpc.JsonRpcException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    // Tag used for logging
    public static final String TAG = "TimelineActivity";

    // Declare client instance and recycle view.
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set client instance as a Rest Client
        client = TwitterApp.getRestClient(this);

        // Connect logic and xml parts of recycler view;
        rvTweets = findViewById(R.id.rvTweets);

        // Generate an empty array for the tweets
        tweets = new ArrayList<>();

        // Instance the adapter, pass context and tweet list.
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TweetsAdapter(this, tweets);

        // Set recycler view's adapter as the one we just instanced.
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();

    }

    // Fetch data from Twitter's APIs
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Tweets obtained succesfully" + json.jsonArray.length());
                JSONArray jsonArray = json.jsonArray;
                try {
                    //tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "adapter notified of changes");
                } catch (JSONException e) {
                    Log.e(TAG, "Could not get data from JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    // Override menu creating menu to use our own menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle menuu option being selected, at the moment compose a new tweet
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnCompose){
            // TODO: HANDLE COMPOSE BUTTON PRESSING
            Intent i = new Intent(this, ComposeActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to handle logout button press
    public void onLogoutButton(View view) {
        // Delete login token
        client.clearAccessToken();
        // Return to login activity
        finish();
    }
}