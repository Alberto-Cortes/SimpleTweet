package com.codepath.apps.restclienttemplate.models;

import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.stetho.inspector.jsonrpc.JsonRpcException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

// Model used to build each tweet.
@Parcel
public class Tweet {

    // Variables needed to work with a tweet.
    public String body;
    public String createdAt;
    public User user;
    public String imageUrl = "";

    // Empty constructor required by Parcel
    public Tweet() {
    }

    // Get and set data from a single object of the JSON response.
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        // Try to get image URL from request, catch exception if not available
        try {
            JSONObject media = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0);
            tweet.imageUrl = media.getString("media_url_https");
            Log.i("TweetModel", "Fetched image url correctly " + tweet.imageUrl);
        } catch (JSONException e){
            Log.e("TweetModel", "Error getting images");
        }


        // This calls the other model defined, user.
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        return  tweet;
    }

    // Generates a list of tweets by traversing the array on the JSON response.
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
