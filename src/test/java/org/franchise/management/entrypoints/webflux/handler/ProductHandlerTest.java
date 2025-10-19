package org.franchise.management.entrypoints.webflux.handler;

import org.franchise.management.application.usecase.*;
import org.franchise.management.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductHandler Tests")
class ProductHandlerTest {

        @Mock
        private AddProductToBranchUseCase addProductToBranchUseCase;

        @Mock
        private DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;

        @Mock
        private UpdateProductNameUseCase updateProductNameUseCase;

        @Mock
        private UpdateProductStockUseCase updateProductStockUseCase;

        @Mock
        private GetMaxStockProductByBranchUseCase findMaxStockProductByFranchiseUseCase;

        @Mock
        private ServerRequest serverRequest;

        @InjectMocks
        private ProductHandler productHandler;

        private String franchiseId;
        private String branchId;
        private String productId;
        private Product product;

        @BeforeEach
        void setUp() {
                franchiseId = "franchise123";
                branchId = "branch456";
                productId = "product789";
                product = Product.builder()
                                .id(productId)
                                .name("Coca Cola")
                                .stock(100)
                                .branchId(branchId)
                                .build();
        }

        @Test
        @DisplayName("Should add product successfully")
        void shouldAddProductSuccessfully() {

                Product savedProduct = Product.builder()
                                .id(productId)
                                .name("Coca Cola")
                                .stock(100)
                                .branchId(branchId)
                                .build();

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(product));
                when(addProductToBranchUseCase.addProduct(eq(branchId), any(Product.class)))
                                .thenReturn(Mono.just(savedProduct));

                Mono<ServerResponse> response = productHandler.addProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(addProductToBranchUseCase, times(1))
                                .addProduct(eq(branchId), any(Product.class));
        }

        @Test
        @DisplayName("Should return bad request when add product fails")
        void shouldReturnBadRequestWhenAddProductFails() {

                IllegalArgumentException exception = new IllegalArgumentException("Sucursal no encontrada");

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(product));
                when(addProductToBranchUseCase.addProduct(eq(branchId), any(Product.class)))
                                .thenReturn(Mono.error(exception));

                Mono<ServerResponse> response = productHandler.addProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle empty body when adding product")
        void shouldHandleEmptyBodyWhenAddingProduct() {

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.empty());

                Mono<ServerResponse> response = productHandler.addProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(addProductToBranchUseCase, never()).addProduct(any(), any());
        }

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProductSuccessfully() {

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(deleteProductFromBranchUseCase.deleteProduct(eq(branchId), eq(productId)))
                                .thenReturn(Mono.empty());

                Mono<ServerResponse> response = productHandler.deleteProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().value() == 204 // No
                                                                                                                // Content
                                )
                                .verifyComplete();

                verify(deleteProductFromBranchUseCase, times(1))
                                .deleteProduct(eq(branchId), eq(productId));
        }

        @Test
        @DisplayName("Should return bad request when delete product fails")
        void shouldReturnBadRequestWhenDeleteProductFails() {

                IllegalArgumentException exception = new IllegalArgumentException("Producto no encontrado");

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(deleteProductFromBranchUseCase.deleteProduct(eq(branchId), eq(productId)))
                                .thenReturn(Mono.error(exception));

                Mono<ServerResponse> response = productHandler.deleteProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should update stock successfully")
        void shouldUpdateStockSuccessfully() {

                Product updatedProduct = Product.builder()
                                .id(productId)
                                .name("Coca Cola")
                                .stock(150)
                                .branchId(branchId)
                                .build();

                Product requestBody = Product.builder()
                                .stock(150)
                                .build();

                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(requestBody));
                when(updateProductStockUseCase.updateStock(eq(productId), eq(150)))
                                .thenReturn(Mono.just(updatedProduct));

                Mono<ServerResponse> response = productHandler.updateStock(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(updateProductStockUseCase, times(1))
                                .updateStock(eq(productId), eq(150));
        }

        @Test
        @DisplayName("Should return bad request when update stock fails")
        void shouldReturnBadRequestWhenUpdateStockFails() {

                Product requestBody = Product.builder()
                                .stock(-10)
                                .build();

                IllegalArgumentException exception = new IllegalArgumentException("Stock cannot be negative");

                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(requestBody));
                when(updateProductStockUseCase.updateStock(eq(productId), eq(-10)))
                                .thenReturn(Mono.error(exception));

                Mono<ServerResponse> response = productHandler.updateStock(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should get max stock products successfully")
        void shouldGetMaxStockProductsSuccessfully() {

                Product product1 = Product.builder()
                                .id("product1")
                                .name("Product 1")
                                .stock(100)
                                .branchId("branch1")
                                .build();

                Product product2 = Product.builder()
                                .id("product2")
                                .name("Product 2")
                                .stock(150)
                                .branchId("branch2")
                                .build();

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(findMaxStockProductByFranchiseUseCase.getMaxStockProducts(franchiseId))
                                .thenReturn(Flux.just(product1, product2));

                Mono<ServerResponse> response = productHandler.getMaxStockProducts(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(findMaxStockProductByFranchiseUseCase, times(1))
                                .getMaxStockProducts(franchiseId);
        }

        @Test
        @DisplayName("Should return empty list when no products found")
        void shouldReturnEmptyListWhenNoProducts() {

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(findMaxStockProductByFranchiseUseCase.getMaxStockProducts(franchiseId))
                                .thenReturn(Flux.empty());

                Mono<ServerResponse> response = productHandler.getMaxStockProducts(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should return bad request when get max stock products fails")
        void shouldReturnBadRequestWhenGetMaxStockProductsFails() {
                IllegalArgumentException exception = new IllegalArgumentException("Franquicia no encontrada");

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(findMaxStockProductByFranchiseUseCase.getMaxStockProducts(franchiseId))
                                .thenReturn(Flux.error(exception));

                Mono<ServerResponse> response = productHandler.getMaxStockProducts(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode()
                                                .equals(HttpStatus.BAD_REQUEST))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle runtime exception in add product")
        void shouldHandleRuntimeExceptionInAddProduct() {

                RuntimeException exception = new RuntimeException("Database error");

                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(product));
                when(addProductToBranchUseCase.addProduct(any(), any()))
                                .thenReturn(Mono.error(exception));

                Mono<ServerResponse> response = productHandler.addProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should extract all path variables correctly")
        void shouldExtractAllPathVariablesCorrectly() {

                String customBranchId = "branch888";
                String customProductId = "product777";

                when(serverRequest.pathVariable("branchId")).thenReturn(customBranchId);
                when(serverRequest.pathVariable("productId")).thenReturn(customProductId);
                when(deleteProductFromBranchUseCase.deleteProduct(
                                eq(customBranchId),
                                eq(customProductId)))
                                .thenReturn(Mono.empty());

                Mono<ServerResponse> response = productHandler.deleteProduct(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().value() == 204)
                                .verifyComplete();

                verify(serverRequest, times(1)).pathVariable("branchId");
                verify(serverRequest, times(1)).pathVariable("productId");
        }

        @Test
        @DisplayName("Should update product name successfully")
        void shouldUpdateProductNameSuccessfully() {
                productId = "product123";
                Product body = Product.builder().name("Café Premium").build();

                Product updated = Product.builder()
                                .id(productId)
                                .name("Café Premium")
                                .branchId("branch123")
                                .stock(10)
                                .build();

                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(body));
                when(updateProductNameUseCase.updateProductName(eq(productId), eq(body.getName())))
                                .thenReturn(Mono.just(updated));

                Mono<ServerResponse> response = productHandler.updateProductName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(res -> res.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(updateProductNameUseCase, times(1))
                                .updateProductName(eq(productId), eq("Café Premium"));
        }

        @Test
        @DisplayName("Should return bad request when body is empty")
        void shouldReturnBadRequestWhenBodyIsEmpty() {
                productId = "product123";

                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.empty());

                Mono<ServerResponse> response = productHandler.updateProductName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(res -> res.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(updateProductNameUseCase, never()).updateProductName(any(), any());
        }

        @Test
        @DisplayName("Should return bad request when product name is empty")
        void shouldReturnBadRequestWhenProductNameIsEmpty() {
                productId = "product123";
                Product body = Product.builder().name("").build();

                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(body));

                Mono<ServerResponse> response = productHandler.updateProductName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(res -> res.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(updateProductNameUseCase, never()).updateProductName(any(), any());
        }

        @Test
        @DisplayName("Should handle exception when updating product name")
        void shouldHandleExceptionWhenUpdatingProductName() {
                productId = "product404";
                Product body = Product.builder().name("Café Premium").build();

                when(serverRequest.pathVariable("productId")).thenReturn(productId);
                when(serverRequest.bodyToMono(Product.class)).thenReturn(Mono.just(body));
                when(updateProductNameUseCase.updateProductName(eq(productId), eq("Café Premium")))
                                .thenReturn(Mono.error(new IllegalArgumentException("Producto no encontrado")));

                Mono<ServerResponse> response = productHandler.updateProductName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(res -> res.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(updateProductNameUseCase).updateProductName(eq(productId), eq("Café Premium"));
        }

}