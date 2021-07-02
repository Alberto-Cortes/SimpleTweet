package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.facebook.stetho.inspector.jsonrpc.JsonRpcException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.OnTweetInteractionListener {

    // Tag used for logging
    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    public static final String EXTRA_TWEET = "extra_tweet";

    // Declare client instance and recycle view.
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;

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
        adapter = new TweetsAdapter(this, tweets, this);

        // Set recycler view's adapter as the one we just instanced.
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
            }
        });

        swipeContainer.setColorSchemeResources(
                R.color.twitter_blue_50
        );
    }

    // Fetch data from Twitter's APIs
    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Show progress bar as an operation is in progress
                miActionProgressItem.setVisible(true);
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.clear();
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                    // Hide progress bar as operation is finished
                    miActionProgressItem.setVisible(false);
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

    // Handle menu option being selected, at the moment compose a new tweet
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnCompose){
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }
    // Method to check results of composing activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Check if the activity we came back from is the Compose Activity and everything is OK
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // Get data from the tweet just published
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // Add new tweet to array
            tweets.add(0, tweet);
            // Update the adapter for the recycle view
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Method to handle logout button press
    public void onLogoutButton(View view) {
        // Delete login token
        client.clearAccessToken();
        // Return to login activity
        finish();
    }

    // Prepare options menu so it connects the progress bar with its logic
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onReplyClicked(int position) {
        // call the API

    }

    @Override
    public void onLikeClicked(final int position) {
        // Extract a tweet from the tweets array
        final Tweet tweet = tweets.get(position);

        // Use the API calling method depending on whether a tweet has a like or not
        client.likeInteraction(tweet.liked, tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Update the tweet liked attribute, the tweets array and the adapter.
                tweet.liked = !tweet.liked;
                tweets.set(position, tweet);
                adapter.notifyItemChanged(position);
            }

            // Toast to notify user if there was any error
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRetweetClicked(final int position) {
        // Extract a tweet from the tweets array
        final Tweet tweet = tweets.get(position);

        // Use the API calling method depending on whether a tweet has been retweeted or not
        client.retweetInteraction(tweet.retweeted, tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                tweet.retweeted = !tweet.retweeted;
                tweets.set(position, tweet);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(TimelineActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("Beto", response, throwable);
            }
        });
    }
    // Handle tap on tweet body to go to tweet detail
    @Override
    public void onTweetClicked(int position) {
        // Declare and define intent from this activity to detail view.
        Intent i = new Intent(this, TweetDetailActivity.class);

        // Extract the clicked tweet from the array
        Tweet tweet = tweets.get(position);

        // Wrap tweet with Parcels to send it over the other activity
        i.putExtra(EXTRA_TWEET, Parcels.wrap(tweet));

        // Launch the new activity
        startActivity(i);
    }
}