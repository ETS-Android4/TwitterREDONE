package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {
    // @Embedded notation flattens the properties of the User object into the object, preserving encapsulation.
    @Embedded
    User user;

    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweets=new ArrayList<>();
        for(int i=0; i<tweetWithUsers.size();i++)
        {
            Tweet tweet=tweetWithUsers.get(i).tweet;
            //Remember how we did @Ignore for the tweet, that's why we need to manually pass in the User object from the list
            tweet.user=tweetWithUsers.get(i).user;
            tweets.add(tweet);
        }

        return tweets;
    }
}
