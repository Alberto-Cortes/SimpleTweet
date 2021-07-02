package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TWEET = "extra_tweet";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    // Declare logic counterpart of visual elements
    ImageView ivDetailProfile;
    TextView tvDetailHandle;
    TextView tvDetailBody;
    ImageView ivDetailImage;
    TextView tvDetailTimestamp;
    TextView tvDetailUsername;
    ImageButton ibDetailLike;
    ImageButton ibDetailReply;
    ImageButton ibDetailRetweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        // Connect logic and visual elements
        ivDetailImage = findViewById(R.id.ivDetailTweetImage);
        ivDetailProfile = findViewById(R.id.ivDetailProfileImage);
        tvDetailBody = findViewById(R.id.tvDetailBody);
        tvDetailHandle = findViewById(R.id.tvDetailHandleName);
        tvDetailTimestamp = findViewById(R.id.tvDetailTimestamp);
        tvDetailUsername = findViewById(R.id.tvDetailUserName);
        ibDetailLike = findViewById(R.id.ibDetailTweetLike);
        ibDetailReply = findViewById(R.id.ibDetailTweetReply);
        ibDetailRetweet = findViewById(R.id.ibDetailTweetRetweet);

        // Unwrap passed tweet
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_TWEET));

        // Use data from the tweet to set content
        tvDetailBody.setText(tweet.body);
        tvDetailHandle.setText(tweet.user.name);
        tvDetailTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));
        tvDetailUsername.setText("@"+tweet.user.screenName);
        Glide.with(this)
                .load(tweet.user.publicImageUrl)
                .circleCrop()
                .into(ivDetailProfile);

        if (!tweet.imageUrl.isEmpty()){
            ivDetailImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(tweet.imageUrl)
                    .transform(new RoundedCorners(45))
                    .into(ivDetailImage);
        } else {
            ivDetailImage.setVisibility(View.GONE);
        }
        if (tweet.liked){
            ibDetailLike.setImageResource(R.drawable.ic_vector_heart);
        } else {
            ibDetailLike.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
        if (tweet.retweeted) {
            ibDetailRetweet.setImageResource(R.drawable.ic_vector_retweet);
        } else {
            ibDetailRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }

    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.e("TweetsAdapter", "getRelativeTimeAgo failed", e);
        }

        return "";
    }
}