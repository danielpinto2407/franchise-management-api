package org.franchise.management.entrypoints.webflux.router;

import org.franchise.management.entrypoints.webflux.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return RouterFunctions
                .route(POST("/franchises/{franchiseId}/branches/{branchId}/products"), handler::addProduct)
                .andRoute(DELETE("/franchises/{franchiseId}/branches/{branchId}/products/{productId}"),
                        handler::deleteProduct)
                .andRoute(PUT("/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock"),
                        handler::updateStock)
                .andRoute(GET("/franchises/{franchiseId}/max-stock"), handler::getMaxStockProducts);
    }
}
