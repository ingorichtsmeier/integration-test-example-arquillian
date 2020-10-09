package com.camunda.consulting.test_example;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class CreateTweetDelegate implements JavaDelegate {
  private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class.getName());
  
  public TwitterService twitterService;

  @Inject
  public CreateTweetDelegate(TwitterService twitterService) {
    super();
    this.twitterService = twitterService;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String content = (String) execution.getVariable("content");
    LOGGER.info("Publishing tweet: " + content);
    long tweetId = twitterService.publishTweet(content);
    execution.setVariable("tweetId", tweetId);
  }

}
