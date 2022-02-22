package com.microservices.demo.elastic.query.service.api;

import com.microservices.demo.elastic.query.service.business.ElasticQueryService;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.microservices.demo.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.microservices.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {
  private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

  private final ElasticQueryService elasticQueryService;

  @Value("${server.port}")
  private String port;

  public ElasticDocumentController(ElasticQueryService queryService) {
    this.elasticQueryService = queryService;
  }

  @PostAuthorize("hasPermission(returnObject, 'READ')")
  @Operation(summary = "Get all elastic documents.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful response.", content = {
          @Content(mediaType = "application/vnd.api.v1+json",
              schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
          )
      }),
      @ApiResponse(responseCode = "400", description = "Not found."),
      @ApiResponse(responseCode = "500", description = "Internal server error.")
  })
  @GetMapping("/")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments() {
    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
    LOG.info("Elasticsearch returned {} documents!", response.size());
    return ResponseEntity.ok(response);
  }

  @PreAuthorize("hasPermission(#id, 'ElasticQueryServiceResponseModel','READ')")
  @Operation(summary = "Get elastic document by id.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful response.", content = {
          @Content(mediaType = "application/vnd.api.v1+json",
              schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
          )
      }),
      @ApiResponse(responseCode = "400", description = "Not found."),
      @ApiResponse(responseCode = "500", description = "Internal server error.")
  })
  @GetMapping("/{id}")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) {
    ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
    LOG.info("Elasticsearch returned document with id {}", id);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Get elastic document by id.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful response.", content = {
          @Content(mediaType = "application/vnd.api.v2+json",
              schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class)
          )
      }),
      @ApiResponse(responseCode = "400", description = "Not found."),
      @ApiResponse(responseCode = "500", description = "Internal server error.")
  })
  @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
  public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable @NotEmpty String id) {
    ElasticQueryServiceResponseModel response = elasticQueryService.getDocumentById(id);
    LOG.info("Elasticsearch returned document with id {}", id);
    ElasticQueryServiceResponseModelV2 responseModelV2 = getV2Model(response);
    return ResponseEntity.ok(responseModelV2);
  }

  @PreAuthorize("hasRole('APP_USER_ROLE') || hasRole('APP_SUPER_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
  @PostAuthorize("hasPermission(returnObject, 'READ')")
  @Operation(summary = "Get elastic document by text.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful response.", content = {
          @Content(mediaType = "application/vnd.api.v1+json",
              schema = @Schema(implementation = ElasticQueryServiceResponseModel.class)
          )
      }),
      @ApiResponse(responseCode = "400", description = "Not found."),
      @ApiResponse(responseCode = "500", description = "Internal server error.")
  })
  @PostMapping("/get-document-by-text")
  public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>>
  getDocumentsByText(@RequestBody @Valid ElasticQueryServiceRequestModel requestModel) {
    List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(requestModel.getText());
    LOG.info("Elasticsearch returned {} documents on port {}!", response.size(), port);
    return ResponseEntity.ok(response);
  }

  private ElasticQueryServiceResponseModelV2 getV2Model(ElasticQueryServiceResponseModel responseModel) {
    ElasticQueryServiceResponseModelV2 responseModelV2 = ElasticQueryServiceResponseModelV2.builder()
        .id(Long.parseLong(responseModel.getId()))
        .userId(responseModel.getUserId())
        .text(responseModel.getText())
        .textV2("Version 2 text")
        .build();
    responseModelV2.add(responseModel.getLinks());
    return responseModelV2;
  }

}
