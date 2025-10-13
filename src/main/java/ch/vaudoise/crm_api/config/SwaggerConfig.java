package ch.vaudoise.crm_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApiSpec() {
        return new OpenAPI()
                .components(
                        new Components()
                                .addSchemas(
                                        "ApiErrorResponse",
                                        new ObjectSchema()
                                                .addProperty("timestamp", new DateTimeSchema().example("2025-10-13T22:00:00Z"))
                                                .addProperty("status", new IntegerSchema().example(404))
                                                .addProperty("error", new StringSchema().example("Not Found"))
                                                .addProperty("message", new StringSchema().example("Client not found"))
                                                .addProperty("path", new StringSchema().example("/api/clients/123"))
                                                .description("Standard error response for failed API requests")
                                )
                );
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            var errorSchemaRef = new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/ApiErrorResponse");

            ApiResponse errorResponse = new ApiResponse()
                    .description("Error response")
                    .content(
                            new Content()
                                    .addMediaType(
                                            "application/json",
                                            new MediaType().schema(errorSchemaRef)
                                    )
                    );

            operation.getResponses()
                    .addApiResponse("400", errorResponse)
                    .addApiResponse("404", errorResponse)
                    .addApiResponse("500", errorResponse);

            return operation;
        };
    }
}
