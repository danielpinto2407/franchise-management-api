package org.franchise.management.entrypoints.webflux.router;

import org.franchise.management.entrypoints.webflux.handler.FranchiseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.toWebHandler;

class FranchiseRouterTest {

    private FranchiseHandler handler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        handler = mock(FranchiseHandler.class);

        FranchiseRouter router = new FranchiseRouter();
        RouterFunction<ServerResponse> route = router.franchiseRoutes(handler);

        webTestClient = WebTestClient.bindToWebHandler(toWebHandler(route)).build();
    }

    @Test
    @DisplayName("Should route POST /franchises to createFranchise handler")
    void shouldRouteToCreateFranchise() {
        when(handler.createFranchise(any())).thenReturn(ServerResponse.ok().build()); // ✅ sin Mono.just

        webTestClient.post()
                .uri("/franchises")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).createFranchise(any());
    }
}
