package ch.vaudoise.crm_api.config;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openApiSpec() {
    return new OpenAPI()
        .info(
            new Info()
                .title("CRM API")
                .version("0.0.1-SNAPSHOT")
                .description("API for managing clients and contracts stored in a backend database"))
        .components(
            new Components()
                .addSchemas(
                    "Decimal128",
                    new NumberSchema()
                        .format("double")
                        .example(1234.56)
                        .description("MongoDB Decimal128 value represented as a decimal number"))
                .addSchemas(
                    "ApiErrorResponse",
                    new ObjectSchema()
                        .addProperty(
                            "timestamp", new DateTimeSchema().example("2025-10-13T22:00:00Z"))
                        .addProperty("status", new IntegerSchema().example(404))
                        .addProperty("error", new StringSchema().example("Not Found"))
                        .addProperty("message", new StringSchema().example("Client not found"))
                        .addProperty("path", new StringSchema().example("/api/clients/123"))
                        .description("Standard error response for failed API requests")));
  }

  @Bean
  public OperationCustomizer operationCustomizer() {
    return (operation, handlerMethod) -> {
      var errorSchemaRef = new Schema<>().$ref("#/components/schemas/ApiErrorResponse");

      ApiResponse errorResponse =
          new ApiResponse()
              .description("Error response")
              .content(
                  new Content()
                      .addMediaType(
                          APPLICATION_JSON_VALUE, new MediaType().schema(errorSchemaRef)));

      operation
          .getResponses()
          .addApiResponse("4xx", errorResponse)
          .addApiResponse("5xx", errorResponse);
      return operation;
    };
  }
}
