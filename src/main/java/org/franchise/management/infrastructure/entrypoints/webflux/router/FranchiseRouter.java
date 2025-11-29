package org.franchise.management.infrastructure.entrypoints.webflux.router;

import org.franchise.management.infrastructure.entrypoints.webflux.handler.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return RouterFunctions.route(POST("/franchises"), handler::createFranchise);
    }
}