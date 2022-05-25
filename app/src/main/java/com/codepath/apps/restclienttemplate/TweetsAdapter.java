package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private static clickReply cR;
    String TAG="TweetsAdapter";
    Context context;
    List<Tweet> tweetList;
    TwitterClient client;

    public interface clickReply
    {
        void onClickReplyReaction(Tweet tweet);
    }

    public TweetsAdapter(Context context, List<Tweet> tweetList,clickReply cR) {
        this.context = context;
        this.tweetList = tweetList;
        this.cR=cR;
        client=TwitterApp.getRestClient(context);
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemTweetBinding binding=ItemTweetBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(tweetList.get(position));
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    //Clear the recyler
    public void clear()
    {
        tweetList.clear();
        notifyDataSetChanged();
    }

    //Add a list of items
    public void addAll(List<Tweet> tweetList)
    {
        this.tweetList.addAll(tweetList);
        Log.d("ADAPTER",tweetList.toString());
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        ItemTweetBinding binding;
        ImageView ivProfileImage;
        ImageView ivTweet;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvRelativeTime;
        TextView tvRetweetCount;
        TextView tvLikedCount;
        ImageButton ibReply;
        ImageButton ibRetweet;
        ImageButton ibFavorite;

        public ViewHolder(@NonNull @NotNull ItemTweetBinding binding_for_tweet) {
            super(binding_for_tweet.getRoot());
            binding=binding_for_tweet;
            ivProfileImage=binding.ivProfileImage;
            tvBody=binding.tvBody;
            tvScreenName=binding.tvScreenName;
            tvName=binding.tvName;
            ivTweet=binding.ivTweet;
            tvRelativeTime=binding.tvRelativeTime;
            tvRetweetCount=binding.tvRetweetCount;
            tvLikedCount=binding.tvLikeCount;
            ibReply=binding.ibReply;
            ibRetweet = binding.ibRetweet;
            ibFavorite=binding.ibFavorite;

        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@"+tweet.user.screenName);
            tvName.setText(tweet.user.name);
            tvRelativeTime.setText(TimeFormatter.getRelativeTimeAgo(tweet.createdAt));
            tvRetweetCount.setText(""+tweet.numRetweets);
            tvLikedCount.setText(""+tweet.numLikes);
            Glide.with(context).load(tweet.user.publicImageUrl).transform(new RoundedCorners(50)).into(ivProfileImage);
            if (tweet.tweet_URL!="none")
            {
                ivTweet.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.tweet_URL).into(ivTweet);
            }
            else
                ivTweet.setVisibility(View.GONE);
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TweetsAdapter.cR.onClickReplyReaction(tweet);
                }
            });
            tvBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context,TweetDetailsActivity.class);
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    context.startActivity(i);
                }
            });
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context,FollowActivity.class);
                    i.putExtra("user", Parcels.wrap(tweet.user));
                    context.startActivity(i);
                }
            });
            if(tweet.liked)
                setLikeColor();
            else
                setNotLikeColor();

            if(tweet.retweeted)
                setRetweetColor();
            else
                setNotRetweetColor();

            binding.ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!tweet.liked)
                    {
                        client.favoriteTweet(String.valueOf(tweet.id) , new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context,"Tweet Liked",Toast.LENGTH_LONG).show();
                                tweet.liked=true;
                                tweet.numLikes+=1;
                                tvLikedCount.setText(""+tweet.numLikes);
                                setLikeColor();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG,"Could not fetch tweet "+throwable.toString());
                                Toast.makeText(context,"Could not like tweet",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        client.unFavoriteTweet(String.valueOf(tweet.id), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context,"Tweet unliked",Toast.LENGTH_LONG).show();
                                tweet.liked=false;
                                tweet.numLikes-=1;
                                tvLikedCount.setText(""+tweet.numLikes);
                                setNotLikeColor();
                            }
                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG,"Could not unlike tweet "+throwable.toString());
                                Toast.makeText(context,"Could not unlike tweet",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            });
            binding.ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!tweet.retweeted)
                    {
                        client.reTweet(String.valueOf(tweet.id), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context,"Tweet retweeted",Toast.LENGTH_LONG).show();
                                tweet.retweeted=true;
                                tweet.numRetweets+=1;
                                tvRetweetCount.setText(""+tweet.numRetweets);
                                setRetweetColor();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context,"Could not be retweeted",Toast.LENGTH_LONG).show();
                                Log.e(TAG,"Could not retweet "+throwable.toString());
                            }
                        });
                    }
                    else
                    {
                        client.unreTweet(String.valueOf(tweet.id), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Toast.makeText(context,"Tweet unretweeted",Toast.LENGTH_LONG).show();
                                tweet.retweeted=false;
                                tweet.numRetweets-=1;
                                tvRetweetCount.setText(""+tweet.numRetweets);
                                setNotRetweetColor();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context,"Could not be unretweeted",Toast.LENGTH_LONG).show();
                                Log.e(TAG,"Could not unretweet "+throwable.toString());
                            }
                        });
                    }

                }
            });
        }

        public void setLikeColor()
        {
            binding.ibFavorite.setColorFilter(context.getResources().getColor(R.color.medium_red));
        }
        public void setNotLikeColor()
        {
            binding.ibFavorite.setColorFilter(context.getResources().getColor(R.color.medium_gray));
        }

        public void setRetweetColor()
        {
            binding.ibRetweet.setColorFilter(context.getResources().getColor(R.color.inline_action_retweet));
        }

        public void setNotRetweetColor()
        {
            binding.ibRetweet.setColorFilter(context.getResources().getColor(R.color.medium_gray));
        }
    }
}
