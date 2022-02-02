package com.microservices.demo.reactive.elastic.query.web.client.service.impl;

import com.microservices.demo.config.ElasticQueryWebClientConfigData;
import com.microservices.demo.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

  private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

  private final WebClient.Builder webClientBuilder;
  private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

  public TwitterElasticQueryWebClient(
      @Qualifier("webClientBuilder") WebClient.Builder clientBuilder,
      ElasticQueryWebClientConfigData configData
  ) {
    this.webClientBuilder = clientBuilder;
    this.elasticQueryWebClientConfigData = configData;
  }

  @Override
  public Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
    LOG.info("Querying by text {}", requestModel.getText());
    return getWebClient(requestModel)
        .bodyToFlux(ElasticQueryWebClientResponseModel.class);
  }

  @Override
  public Flux<ElasticQueryWebClientResponseModel> getAllData() {
    LOG.info("Querying all data");
    return webClientBuilder
        .build()
        .get()
        .uri("/")
        .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQuery().getAccept()))
        .retrieve()
        .bodyToFlux(ElasticQueryWebClientResponseModel.class);
  }

  private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
    return webClientBuilder
        .build()
        .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQuery().getMethod()))
        .uri(elasticQueryWebClientConfigData.getQuery().getUri())
        .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQuery().getAccept()))
        .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
        .retrieve()
        .onStatus(
            httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
            clientResponse -> Mono.just(new BadCredentialsException("Not authenticated!")))
        .onStatus(
            HttpStatus::is4xxClientError,
            cr -> Mono.just(new ElasticQueryWebClientException(cr.statusCode().getReasonPhrase())))
        .onStatus(
            HttpStatus::is5xxServerError,
            cr -> Mono.just(new Exception(cr.statusCode().getReasonPhrase())));
  }

  private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
    return new ParameterizedTypeReference<>() {
    };
  }

}
