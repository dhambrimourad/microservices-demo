package com.microservices.demo.reactive.elastic.query.web.client.api;

import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.microservices.demo.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.microservices.demo.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@Controller
public class QueryController {

  private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

  private final ElasticQueryWebClient elasticQueryWebClient;

  public QueryController(ElasticQueryWebClient webClient) {
    this.elasticQueryWebClient = webClient;
  }

  @GetMapping("")
  public String index() {
    return "index";
  }

  @GetMapping("/error")
  public String error() {
    return "error";
  }

  @GetMapping("/home")
  public String home(Model model) {
    model.addAttribute("elasticQueryWebClientRequestModel",
        ElasticQueryWebClientRequestModel.builder().build());
    return "home";
  }

  @PostMapping("/query-by-text")
  public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {
    Flux<ElasticQueryWebClientResponseModel> responseModels = elasticQueryWebClient.getDataByText(requestModel);
    responseModels = responseModels.log();
    IReactiveDataDriverContextVariable reactiveData = new ReactiveDataDriverContextVariable(responseModels, 1);
    model.addAttribute("elasticQueryWebClientResponseModels", reactiveData);
    model.addAttribute("searchText", requestModel.getText());
    model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
    LOG.info("Returning from reactive client controller for text {}", requestModel.getText());
    return "home";
  }

  @GetMapping("/all")
  public String queryAll(Model model) {
    Flux<ElasticQueryWebClientResponseModel> responseModels = elasticQueryWebClient.getAllData();
    responseModels = responseModels.log();
    IReactiveDataDriverContextVariable reactiveData = new ReactiveDataDriverContextVariable(responseModels, 1);
    model.addAttribute("elasticQueryWebClientResponseModels", reactiveData);
    model.addAttribute("searchText", "");
    model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
    LOG.info("Returning from reactive client controller for all data");
    return "home";
  }

}
