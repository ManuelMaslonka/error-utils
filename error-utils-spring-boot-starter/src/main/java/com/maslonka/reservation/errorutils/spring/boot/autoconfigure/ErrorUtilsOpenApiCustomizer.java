package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.Map;

/**
 * Contributes reusable error schemas and standard responses to an OpenAPI model.
 *
 * <p>The customizer adds shared {@code ApiError} and {@code FieldViolation} schemas and attaches
 * common error responses to operations that do not define them explicitly.</p>
 */
public class ErrorUtilsOpenApiCustomizer implements OpenApiCustomizer {

    private static final String API_ERROR_SCHEMA = "ApiError";
    private static final String FIELD_VIOLATION_SCHEMA = "FieldViolation";

    private static final String ERROR_400 = "Error400";
    private static final String ERROR_401 = "Error401";
    private static final String ERROR_403 = "Error403";
    private static final String ERROR_404 = "Error404";
    private static final String ERROR_409 = "Error409";
    private static final String ERROR_422 = "Error422";
    private static final String ERROR_500 = "Error500";

    /**
     * Creates the OpenAPI customizer.
     */
    public ErrorUtilsOpenApiCustomizer() {
    }

    /**
     * Updates the supplied OpenAPI document with shared error schemas and responses.
     *
     * @param openApi OpenAPI document to customize
     */
    @Override
    public void customise(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }

        ensureSchemas(components);
        ensureCommonErrorResponses(components);
        attachDefaultResponses(openApi);
    }

    private void ensureSchemas(Components components) {
        Map<String, Schema> schemas = components.getSchemas();
        if (schemas == null) {
            schemas = new java.util.LinkedHashMap<>();
            components.setSchemas(schemas);
        }

        schemas.putIfAbsent(FIELD_VIOLATION_SCHEMA, fieldViolationSchema());
        schemas.putIfAbsent(API_ERROR_SCHEMA, apiErrorSchema());
    }

    private Schema<?> fieldViolationSchema() {
        return new ObjectSchema()
            .addProperty("field", new StringSchema())
            .addProperty("rejectedValue", new ObjectSchema().nullable(true))
            .addProperty("message", new StringSchema())
            .addProperty("code", new StringSchema());
    }

    private Schema<?> apiErrorSchema() {
        return new ObjectSchema()
            .addProperty("timestamp", new StringSchema().format("date-time"))
            .addProperty("status", new IntegerSchema())
            .addProperty("error", new StringSchema())
            .addProperty("code", new StringSchema())
            .addProperty("message", new StringSchema())
            .addProperty("path", new StringSchema())
            .addProperty("correlationId", new StringSchema())
            .addProperty("traceId", new StringSchema())
            .addProperty("violations", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/" + FIELD_VIOLATION_SCHEMA)))
            .addProperty("metadata", new MapSchema().additionalProperties(new ObjectSchema()));
    }

    private void ensureCommonErrorResponses(Components components) {
        Map<String, ApiResponse> responses = components.getResponses();
        if (responses == null) {
            responses = new java.util.LinkedHashMap<>();
            components.setResponses(responses);
        }

        responses.putIfAbsent(ERROR_400, buildResponse("Bad Request"));
        responses.putIfAbsent(ERROR_401, buildResponse("Unauthorized"));
        responses.putIfAbsent(ERROR_403, buildResponse("Forbidden"));
        responses.putIfAbsent(ERROR_404, buildResponse("Not Found"));
        responses.putIfAbsent(ERROR_409, buildResponse("Conflict"));
        responses.putIfAbsent(ERROR_422, buildResponse("Unprocessable Entity"));
        responses.putIfAbsent(ERROR_500, buildResponse("Internal Server Error"));
    }

    private ApiResponse buildResponse(String description) {
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType("application/json",
                new io.swagger.v3.oas.models.media.MediaType()
                    .schema(new Schema<>().$ref("#/components/schemas/" + API_ERROR_SCHEMA))
            ));
    }

    private void attachDefaultResponses(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }

        openApi.getPaths().values().forEach(pathItem -> {
            for (Operation operation : pathItem.readOperations()) {
                ApiResponses responses = operation.getResponses();
                if (responses == null) {
                    responses = new ApiResponses();
                    operation.setResponses(responses);
                }

                addIfMissing(responses, "400", ERROR_400);
                addIfMissing(responses, "401", ERROR_401);
                addIfMissing(responses, "403", ERROR_403);
                addIfMissing(responses, "404", ERROR_404);
                addIfMissing(responses, "409", ERROR_409);
                addIfMissing(responses, "422", ERROR_422);
                addIfMissing(responses, "500", ERROR_500);
            }
        });
    }

    private void addIfMissing(ApiResponses responses, String statusCode, String componentName) {
        if (!responses.containsKey(statusCode)) {
            responses.addApiResponse(statusCode, new ApiResponse().$ref("#/components/responses/" + componentName));
        }
    }
}
