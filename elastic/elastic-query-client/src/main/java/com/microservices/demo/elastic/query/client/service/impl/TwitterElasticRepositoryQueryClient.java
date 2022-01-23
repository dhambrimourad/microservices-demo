package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.common.util.CollectionsUtil;
import com.microservices.demo.config.ElasticConfigData;
import com.microservices.demo.config.ElasticQueryConfigData;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.repository.TwitterElasticQueryRepository;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import com.microservices.demo.elastic.query.client.util.ElasticQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticRepositoryQueryClient.class);

  private final TwitterElasticQueryRepository twitterElasticQueryRepository;

  public TwitterElasticRepositoryQueryClient(TwitterElasticQueryRepository repository) {
    this.twitterElasticQueryRepository = repository;
  }

  @Override
  public TwitterIndexModel getIndexModelById(String id) {
    Optional<TwitterIndexModel> searchResult = twitterElasticQueryRepository.findById(id);
    LOG.info("Document with id {} retrieved successfully", searchResult.orElseThrow(() ->
        new ElasticQueryClientException("No document found in elasticsearch with id " + id)).getId());
    return searchResult.get();
  }

  @Override
  public List<TwitterIndexModel> getIndexModelByText(String text) {
    List<TwitterIndexModel> searchResult = twitterElasticQueryRepository.findByText(text);
    LOG.info("{} documents with text {} retrieved successfully", searchResult.size(), text);
    return searchResult;
  }

  @Override
  public List<TwitterIndexModel> getAllIndexModels() {
    List<TwitterIndexModel> searchResult = CollectionsUtil.getInstance().getListFromIterable(twitterElasticQueryRepository.findAll());
    LOG.info("{} documents retrieved successfully", searchResult.size());
    return searchResult;
  }

}
