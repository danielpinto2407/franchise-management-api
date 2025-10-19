package org.franchise.management.entrypoints.webflux.router;

import org.franchise.management.entrypoints.webflux.handler.BranchHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.toWebHandler;

class BranchRouterTest {

    private BranchHandler handler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        handler = mock(BranchHandler.class);

        BranchRouter router = new BranchRouter();
        RouterFunction<ServerResponse> route = router.branchRoutes(handler);

        webTestClient = WebTestClient.bindToWebHandler(toWebHandler(route)).build();
    }

    @Test
    @DisplayName("Should route POST /franchises/{franchiseId}/branches to addBranch handler")
    void shouldRouteToAddBranch() {
        when(handler.addBranch(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.post()
                .uri("/franchises/123/branches")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).addBranch(any());
    }

    @Test
    @DisplayName("Should route PUT /branches/{branchId}/name to updateBranchName handler")
    void shouldRouteToUpdateBranchName() {
        when(handler.updateBranchName(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.put()
                .uri("/branches/456/name")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).updateBranchName(any());
    }
}
