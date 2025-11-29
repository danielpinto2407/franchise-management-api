package org.franchise.management.infrastructure.entrypoints.webflux.router;

import org.franchise.management.infrastructure.entrypoints.webflux.dto.FranchiseRequestDTO;
import org.franchise.management.infrastructure.entrypoints.webflux.handler.FranchiseHandler;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class FranchiseRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/franchises",
            method = RequestMethod.POST,
            beanClass = FranchiseHandler.class,
            beanMethod = "createFranchise",
            operation = @Operation(
                operationId = "createFranchise",
                summary = "Crear una franquicia",
                description = "Crea una nueva franquicia en el sistema",
                tags = { "Franchises" },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        schema = @Schema(implementation = FranchiseRequestDTO.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Franquicia creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
                }
            )
        )
    })
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return RouterFunctions.route(POST("/franchises"), handler::createFranchise);
    }
}
