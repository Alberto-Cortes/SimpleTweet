package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.Viewholder> {

    // Interface to listen to clicks on the tweets
    public interface OnTweetInteractionListener{
        void onReplyClicked(int position);
        void onLikeClicked(int position);
        void onRetweetClicked(int position);
        void onTweetClicked(int position);
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    // Member variables needed to adapt.
    Context context;
    List<Tweet> tweets;

    OnTweetInteractionListener tweetInteractionListener;

    // Constructor for the class
    public TweetsAdapter(Context context,
                         List<Tweet> tweets,
                         OnTweetInteractionListener listener) {
        this.context = context;
        this.tweets = tweets;

        this.tweetInteractionListener = listener;
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

    @NotNull
    @Override
    // For each displayable row, inflate an Item Layout.
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Viewholder holder, int position) {
        // Get the data from the tweet and pass it to the binder.
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Holder class for the Reclycler View.
    public class Viewholder extends RecyclerView.ViewHolder {

        // Declare the variables needed for the tweet.
        ImageView ivProfileImage;
        ImageView ivTweetImage;
        TextView tvBody;
        TextView tvHandleName;
        TextView tvUserName;
        TextView tvTimestamp;
        ImageButton ibTweetReply;
        ImageButton ibTweetRetweet;
        ImageButton ibTweetLike;

        // Connect visual with logic elements.
        public Viewholder(@NotNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivDetailProfileImage);
            tvBody = itemView.findViewById(R.id.tvDetailBody);
            tvHandleName = itemView.findViewById(R.id.tvDetailHandleName);
            tvTimestamp = itemView.findViewById(R.id.tvDetailTimestamp);
            ivTweetImage = itemView.findViewById(R.id.ivDetailTweetImage);
            tvUserName = itemView.findViewById(R.id.tvDetailUserName);

            ibTweetLike = itemView.findViewById(R.id.ibDetailTweetLike);
            ibTweetReply = itemView.findViewById(R.id.ibDetailTweetLike);
            ibTweetRetweet = itemView.findViewById(R.id.ibDetailTweetRetweet);
        }

        // Bind data to row of the Recycle View.
        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvHandleName.setText(tweet.user.name);
            tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));
            tvUserName.setText("@" + tweet.user.screenName);
            Glide.with(context)
                    .load(tweet.user.publicImageUrl)
                    .circleCrop()
                    .into(ivProfileImage);

            // Check if tweet has image URL, if it has display it, if it does not, remove Image View
            if (!tweet.imageUrl.isEmpty()){
                ivTweetImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.imageUrl)
                        .transform(new RoundedCorners(45))
                        .into(ivTweetImage);
            } else {
                ivTweetImage.setVisibility(View.GONE);
            }
            if (tweet.liked){
                ibTweetLike.setImageResource(R.drawable.ic_vector_heart);
            } else {
                ibTweetLike.setImageResource(R.drawable.ic_vector_heart_stroke);
            }
            if (tweet.retweeted) {
                ibTweetRetweet.setImageResource(R.drawable.ic_vector_retweet);
            } else {
                ibTweetRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            // Methods for each image button on the tweets. Each method calls the implementation done on the TimelineActivity
            ibTweetReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetInteractionListener.onReplyClicked(getAdapterPosition());
                }
            });
            ibTweetRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetInteractionListener.onRetweetClicked(getAdapterPosition());
                }
            });
            ibTweetLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetInteractionListener.onLikeClicked(getAdapterPosition());
                }
            });

            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetInteractionListener.onTweetClicked(getAdapterPosition());
                }
            });
        }
    }
}
