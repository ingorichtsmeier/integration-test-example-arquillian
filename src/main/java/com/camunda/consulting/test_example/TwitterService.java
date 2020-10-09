package com.camunda.consulting.test_example;

import javax.inject.Named;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

@Named
public class TwitterService {
  
  public long publishTweet(String content) throws TwitterException {
    AccessToken accessToken = new AccessToken("220324559-jet1dkzhSOeDWdaclI48z5txJRFLCnLOK45qStvo", "B28Ze8VDucBdiE38aVQqTxOyPc7eHunxBVv7XgGim4say");
    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer("lRhS80iIXXQtm6LM03awjvrvk", "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS");
    twitter.setOAuthAccessToken(accessToken);
    Status status = twitter.updateStatus(content);
    return status.getId();
  }

}
