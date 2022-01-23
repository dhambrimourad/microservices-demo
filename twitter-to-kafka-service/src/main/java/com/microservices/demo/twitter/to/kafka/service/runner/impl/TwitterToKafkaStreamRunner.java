package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.listener.TwitterToKafkaStatusListener;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.annotation.PreDestroy;
import java.util.Arrays;

@Component
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false", matchIfMissing = true)
public class TwitterToKafkaStreamRunner implements StreamRunner {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaStreamRunner.class);

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
  private final TwitterToKafkaStatusListener twitterToKafkaStatusListener;

  private final TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

  public TwitterToKafkaStreamRunner(
      TwitterToKafkaServiceConfigData config,
      TwitterToKafkaStatusListener listener
  ) {
    this.twitterToKafkaServiceConfigData = config;
    this.twitterToKafkaStatusListener = listener;
  }

  @Override
  public void start() throws TwitterException {
    this.twitterStream.addListener(this.twitterToKafkaStatusListener);
    addFilter();
  }

  @PreDestroy
  public void shutdown(){
    LOG.info("Closing twitter stream!");
    this.twitterStream.shutdown();
  }

  private void addFilter() {
    String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);
    FilterQuery filterQuery = new FilterQuery(keywords);
    this.twitterStream.filter(filterQuery);
    LOG.info("Started filtering twitter stream for keywords {}", Arrays.toString(keywords));
  }

}
