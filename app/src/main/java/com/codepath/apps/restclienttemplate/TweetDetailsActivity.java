package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    public static final String TAG="TweetDetailsActivity";
    Tweet tweet;
    ActivityTweetDetailsBinding binding;
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tweet= Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        binding.tvName.setText(tweet.user.name);
        binding.tvScreenName.setText(tweet.user.screenName);
        binding.tvBody.setText(tweet.body);
        binding.tvDate.setText(tweet.createdAt);
        Glide.with(this).load(tweet.tweet_URL).into(binding.ivTweet);
        Glide.with(this).load(tweet.user.publicImageUrl).into(binding.ivProfileImage);

        client=TwitterApp.getRestClient(this);

        binding.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.favoriteTweet(String.valueOf(tweet.id) , new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Toast.makeText(TweetDetailsActivity.this,"Tweet Liked",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG,"Could not fetch tweet "+throwable.toString());
                        Toast.makeText(TweetDetailsActivity.this,"Could not like tweet",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        binding.ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.reTweet(String.valueOf(tweet.id), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Toast.makeText(TweetDetailsActivity.this,"Tweet retweeted",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Toast.makeText(TweetDetailsActivity.this,"Could not be retweeted",Toast.LENGTH_LONG).show();
                        Log.e(TAG,"Could not retweet "+throwable.toString());
                    }
                });
            }
        });


    }
}