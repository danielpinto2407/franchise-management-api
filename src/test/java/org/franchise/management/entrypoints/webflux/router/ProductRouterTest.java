package org.franchise.management.entrypoints.webflux.router;

import org.franchise.management.entrypoints.webflux.handler.ProductHandler;
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

class ProductRouterTest {

    private ProductHandler handler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        handler = mock(ProductHandler.class);
        ProductRouter router = new ProductRouter();
        RouterFunction<ServerResponse> route = router.productRoutes(handler);
        webTestClient = WebTestClient.bindToWebHandler(toWebHandler(route)).build();
    }

    @Test
    @DisplayName("Should route POST /branches/{branchId}/products to addProduct handler")
    void shouldRouteToAddProduct() {
        when(handler.addProduct(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.post()
                .uri("/branches/123/products")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).addProduct(any());
    }

    @Test
    @DisplayName("Should route DELETE /branches/{branchId}/products/{productId} to deleteProduct handler")
    void shouldRouteToDeleteProduct() {
        when(handler.deleteProduct(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.delete()
                .uri("/branches/123/products/456")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).deleteProduct(any());
    }

    @Test
    @DisplayName("Should route PUT /products/{productId}/stock to updateStock handler")
    void shouldRouteToUpdateStock() {
        when(handler.updateStock(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.put()
                .uri("/products/456/stock")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).updateStock(any());
    }

    @Test
    @DisplayName("Should route GET /franchises/{franchiseId}/products/max-stock to getMaxStockProducts handler")
    void shouldRouteToGetMaxStockProducts() {
        when(handler.getMaxStockProducts(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.get()
                .uri("/franchises/789/products/max-stock")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).getMaxStockProducts(any());
    }

    @Test
    @DisplayName("Should route PUT /products/{productId}/name to updateProductName handler")
    void shouldRouteToUpdateProductName() {
        when(handler.updateProductName(any())).thenReturn(ServerResponse.ok().build());

        webTestClient.put()
                .uri("/products/456/name")
                .exchange()
                .expectStatus().isOk();

        verify(handler, times(1)).updateProductName(any());
    }
}
