package org.franchise.management.entrypoints.webflux.router;

import org.franchise.management.entrypoints.webflux.handler.BranchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;

@Configuration
public class BranchRouter {

    @Bean
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return RouterFunctions.route(POST("/franchises/{franchiseId}/branches"), handler::addBranch)
                .andRoute(PUT("/branches/{branchId}/name"), handler::updateBranchName);
    }
}
