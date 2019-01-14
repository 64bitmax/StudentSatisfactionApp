package com.oxfordbrookes.max.studentsatisfactionapp.utils;

public class TweetSentiment {
    private String tweet;
    private int sentiment;


    public TweetSentiment(String tweet, int sentiment) {
        this.tweet = tweet;
        this.sentiment = sentiment;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
