package com.microservices.demo.elastic.index.client.service.impl;

import com.microservices.demo.elastic.index.client.repository.TwitterElasticIndexRepository;
import com.microservices.demo.elastic.index.client.service.ElasticIndexClient;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticRepositoryIndexClient.class);

  private final TwitterElasticIndexRepository twitterElasticIndexRepository;

  public TwitterElasticRepositoryIndexClient(TwitterElasticIndexRepository repository) {
    this.twitterElasticIndexRepository = repository;
  }

  @Override
  public List<String> save(List<TwitterIndexModel> documents) {
    List<TwitterIndexModel> repositoryResponse = (List<TwitterIndexModel>) twitterElasticIndexRepository.saveAll(documents);
    List<String> documentIds = repositoryResponse.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
    LOG.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(), documentIds);
    return documentIds;
  }

}
