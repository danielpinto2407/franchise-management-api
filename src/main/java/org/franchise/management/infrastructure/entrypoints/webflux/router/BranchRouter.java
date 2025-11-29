package org.franchise.management.infrastructure.entrypoints.webflux.router;

import org.franchise.management.infrastructure.entrypoints.webflux.dto.BranchRequestDTO;
import org.franchise.management.infrastructure.entrypoints.webflux.handler.BranchHandler;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class BranchRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/franchises/{franchiseId}/branches",
            method = RequestMethod.POST,
            beanClass = BranchHandler.class,
            beanMethod = "addBranch",
            operation = @Operation(
                operationId = "addBranch",
                summary = "Crear branch",
                description = "Agrega un nuevo branch a una franquicia",
                tags = { "Branches" },
                parameters = {
                    @Parameter(
                        name = "franchiseId",
                        in = ParameterIn.PATH,
                        required = true,
                        description = "ID de la franquicia"
                    )
                },
                requestBody = @RequestBody(
                    description = "Datos del branch",
                    required = true,
                    content = @Content(
                        schema = @Schema(implementation = BranchRequestDTO.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Branch creado con éxito"
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Datos inválidos"
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Franquicia no encontrada"
                    )
                }
            )
        ),
        @RouterOperation(
            path = "/branches/{branchId}/name",
            method = RequestMethod.PUT,
            beanClass = BranchHandler.class,
            beanMethod = "updateBranchName",
            operation = @Operation(
                operationId = "updateBranchName",
                summary = "Actualizar nombre del branch",
                description = "Actualiza el nombre de un branch existente",
                tags = { "Branches" },
                parameters = {
                    @Parameter(
                        name = "branchId",
                        in = ParameterIn.PATH,
                        required = true,
                        description = "ID del branch"
                    )
                },
                requestBody = @RequestBody(
                    description = "Nuevo nombre del branch",
                    required = true,
                    content = @Content(
                        schema = @Schema(implementation = BranchRequestDTO.class)
                    )
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200",
                        description = "Nombre actualizado con éxito"
                    ),
                    @ApiResponse(
                        responseCode = "400",
                        description = "Datos inválidos"
                    ),
                    @ApiResponse(
                        responseCode = "404",
                        description = "Branch no encontrado"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return RouterFunctions
            .route(POST("/franchises/{franchiseId}/branches")
                .and(accept(MediaType.APPLICATION_JSON)), handler::addBranch)
            .andRoute(PUT("/branches/{branchId}/name")
                .and(accept(MediaType.APPLICATION_JSON)), handler::updateBranchName);
    }
}