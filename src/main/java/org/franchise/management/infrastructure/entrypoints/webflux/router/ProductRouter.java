package org.franchise.management.infrastructure.entrypoints.webflux.router;

import org.franchise.management.infrastructure.entrypoints.webflux.dto.ProductRequestDTO;
import org.franchise.management.infrastructure.entrypoints.webflux.dto.UpdateStockRequestDTO;
import org.franchise.management.infrastructure.entrypoints.webflux.handler.ProductHandler;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Configuration
public class ProductRouter {

    @Bean
    @RouterOperations({

        // --------------------------------------------------------
        // POST /branches/{branchId}/products : Crear Producto
        // --------------------------------------------------------
        @RouterOperation(
            path = "/branches/{branchId}/products",
            method = RequestMethod.POST,
            beanClass = ProductHandler.class,
            beanMethod = "addProduct",
            operation = @Operation(
                operationId = "addProduct",
                summary = "Agregar producto a una sucursal",
                tags = { "Products" },
                parameters = {
                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true)
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Producto agregado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
                }
            )
        ),

        // --------------------------------------------------------
        // DELETE /branches/{branchId}/products/{productId} : Eliminar Producto
        // --------------------------------------------------------
        @RouterOperation(
            path = "/branches/{branchId}/products/{productId}",
            method = RequestMethod.DELETE,
            beanClass = ProductHandler.class,
            beanMethod = "deleteProduct",
            operation = @Operation(
                operationId = "deleteProduct",
                summary = "Eliminar producto",
                tags = { "Products" },
                parameters = {
                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true),
                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "Producto eliminado"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
                }
            )
        ),

        // --------------------------------------------------------
        // PUT /products/{productId}/stock : Actualizar Stock
        // --------------------------------------------------------
        @RouterOperation(
            path = "/products/{productId}/stock",
            method = RequestMethod.PUT,
            beanClass = ProductHandler.class,
            beanMethod = "updateStock",
            operation = @Operation(
                operationId = "updateStock",
                summary = "Actualizar stock de un producto",
                tags = { "Products" },
                parameters = {
                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateStockRequestDTO.class))
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Stock actualizado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
                }
            )
        ),

        // --------------------------------------------------------
        // GET /franchises/{franchiseId}/products/max-stock : Productos con mayor stock
        // --------------------------------------------------------
        @RouterOperation(
            path = "/franchises/{franchiseId}/products/max-stock",
            method = RequestMethod.GET,
            beanClass = ProductHandler.class,
            beanMethod = "getMaxStockProducts",
            operation = @Operation(
                operationId = "getMaxStockProducts",
                summary = "Obtener productos con mayor stock en una franquicia",
                tags = { "Products" },
                parameters = {
                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true)
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de productos con mayor stock")
                }
            )
        ),

        // --------------------------------------------------------
        // PUT /products/{productId}/name : Actualizar nombre
        // --------------------------------------------------------
        @RouterOperation(
            path = "/products/{productId}/name",
            method = RequestMethod.PUT,
            beanClass = ProductHandler.class,
            beanMethod = "updateProductName",
            operation = @Operation(
                operationId = "updateProductName",
                summary = "Actualizar nombre del producto",
                tags = { "Products" },
                parameters = {
                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true)
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Nombre actualizado"),
                    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
                }
            )
        )
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return RouterFunctions
            .route(POST("/branches/{branchId}/products"), handler::addProduct)
            .andRoute(DELETE("/branches/{branchId}/products/{productId}"), handler::deleteProduct)
            .andRoute(PUT("/products/{productId}/stock"), handler::updateStock)
            .andRoute(GET("/franchises/{franchiseId}/products/max-stock"), handler::getMaxStockProducts)
            .andRoute(PUT("/products/{productId}/name"), handler::updateProductName);
    }
}
