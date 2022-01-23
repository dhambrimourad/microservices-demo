package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

  private final ElasticQueryService elasticQueryService;

  public ElasticDocumentController(ElasticQueryService queryService) {
    this.elasticQueryService = queryService;
  }

  @GetMapping("/")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
    LOG.info("Elasticsearch returned {} documents!", response.size());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) {
    ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
    LOG.info("Elasticsearch returned document with id {}", id);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/get-document-by-text")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>>
  getDocumentsByText(@RequestBody @Valid ElasticQueryServiceRequestModel requestModel) {
    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(requestModel.getText());
    LOG.info("Elasticsearch returned {} documents!", response.size());
    return ResponseEntity.ok(response);
  }
}
