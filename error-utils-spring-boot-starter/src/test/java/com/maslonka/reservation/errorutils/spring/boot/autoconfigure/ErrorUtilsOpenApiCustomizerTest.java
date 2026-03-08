package com.maslonka.reservation.errorutils.spring.boot.autoconfigure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorUtilsOpenApiCustomizerTest {

    private final ErrorUtilsOpenApiCustomizer customizer = new ErrorUtilsOpenApiCustomizer();

    @Test
    @DisplayName("Should add shared schemas and default response references when customizing operations")
    void shouldAddSharedSchemasAndDefaultResponseReferencesWhenCustomizingOperations() {
        Operation operation = new Operation();
        OpenAPI openApi = new OpenAPI().paths(new Paths().addPathItem("/orders", new PathItem().get(operation)));

        customizer.customise(openApi);

        assertThat(openApi.getComponents()).isNotNull();
        assertThat(openApi.getComponents().getSchemas()).containsKeys("ApiError", "FieldViolation");
        assertThat(openApi.getComponents().getResponses()).containsKeys("Error400", "Error401", "Error403", "Error404", "Error409", "Error422", "Error500");
        assertThat(openApi.getComponents().getResponses().get("Error400").getContent().get("application/json").getSchema().get$ref()).isEqualTo(
                "#/components/schemas/ApiError");
        assertThat(openApi.getComponents().getSchemas().get("ApiError").getProperties()).containsKeys("timestamp", "violations", "metadata");
        assertThat(operation.getResponses()).containsKeys("400", "401", "403", "404", "409", "422", "500");
        assertThat(operation.getResponses().get("422").get$ref()).isEqualTo("#/components/responses/Error422");
    }

    @Test
    @DisplayName("Should preserve existing schemas and explicit operation responses when customizing OpenAPI twice")
    void shouldPreserveExistingSchemasAndExplicitOperationResponsesWhenCustomizingOpenApiTwice() {
        Schema<?> existingApiError = new ObjectSchema().description("custom api error");
        ApiResponse existingError400 = new ApiResponse().description("custom bad request component");
        ApiResponse explicitOperation400 = new ApiResponse().description("operation specific bad request");

        Operation operation = new Operation().responses(new ApiResponses().addApiResponse("400", explicitOperation400));
        OpenAPI openApi = new OpenAPI().components(new Components().addSchemas("ApiError", existingApiError).addResponses("Error400", existingError400))
                .paths(new Paths().addPathItem("/orders", new PathItem().post(operation)));

        customizer.customise(openApi);
        customizer.customise(openApi);

        assertThat(openApi.getComponents().getSchemas().get("ApiError")).isSameAs(existingApiError);
        assertThat(openApi.getComponents().getSchemas()).containsKey("FieldViolation");
        assertThat(openApi.getComponents().getResponses().get("Error400")).isSameAs(existingError400);
        assertThat(operation.getResponses().get("400")).isSameAs(explicitOperation400);
        assertThat(operation.getResponses().get("401").get$ref()).isEqualTo("#/components/responses/Error401");
    }
}
