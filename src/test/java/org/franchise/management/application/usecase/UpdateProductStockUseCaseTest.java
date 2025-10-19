package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.ProductMongoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UpdateProductStockUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductStockUseCase Tests")
class UpdateProductStockUseCaseTest {

        @Mock
        private ProductMongoAdapter productRepository;

        @InjectMocks
        private UpdateProductStockUseCase useCase;

        private String franchiseId;
        private String branchId;
        private String productId;
        private Integer newStock;

        @BeforeEach
        void setUp() {
                franchiseId = "franchise123";
                branchId = "branch456";
                productId = "product789";
                newStock = 150;
        }

        @Test
        @DisplayName("Should update product stock successfully")
        void shouldUpdateProductStockSuccessfully() {

                Product updatedProduct = Product.builder()
                                .id(productId)
                                .name("Coca Cola")
                                .stock(newStock)
                                .branchId(branchId)
                                .build();

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectNextMatches(p -> p.getId().equals(productId) &&
                                                p.getStock().equals(newStock) &&
                                                p.getName().equals("Coca Cola"))
                                .verifyComplete();

                verify(productRepository, times(1))
                                .updateProductStock(eq(productId), eq(newStock));
        }

        @Test
        @DisplayName("Should return error when product not found")
        void shouldReturnErrorWhenProductNotFound() {

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.empty());

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Producto no encontrado."))
                                .verify();

                verify(productRepository, times(1))
                                .updateProductStock(eq(productId), eq(newStock));
        }

        @Test
        @DisplayName("Should handle repository error")
        void shouldHandleRepositoryError() {

                RuntimeException repositoryError = new RuntimeException("Database connection error");

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.error(repositoryError));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                                                throwable.getMessage().equals("Database connection error"))
                                .verify();
        }

        @Test
        @DisplayName("Should update stock to zero")
        void shouldUpdateStockToZero() {

                Integer zeroStock = 0;
                Product updatedProduct = Product.builder()
                                .id(productId)
                                .name("Pepsi")
                                .stock(zeroStock)
                                .branchId(branchId)
                                .build();

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(zeroStock)))
                                .thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(useCase.updateStock(productId, zeroStock))
                                .expectNextMatches(p -> p.getStock().equals(0))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should update stock to large number")
        void shouldUpdateStockToLargeNumber() {

                Integer largeStock = 10000;
                Product updatedProduct = Product.builder()
                                .id(productId)
                                .name("Sprite")
                                .stock(largeStock)
                                .branchId(branchId)
                                .build();

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(largeStock)))
                                .thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(useCase.updateStock(productId, largeStock))
                                .expectNextMatches(p -> p.getStock().equals(largeStock))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle negative stock error")
        void shouldHandleNegativeStockError() {

                Integer negativeStock = -10;
                IllegalArgumentException negativeError = new IllegalArgumentException("Stock cannot be negative");

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(negativeStock)))
                                .thenReturn(Mono.error(negativeError));

                StepVerifier.create(useCase.updateStock(productId, negativeStock))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Stock cannot be negative"))
                                .verify();
        }

        @Test
        @DisplayName("Should handle branch not found error")
        void shouldHandleBranchNotFoundError() {

                IllegalArgumentException notFoundError = new IllegalArgumentException("Branch not found");

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.error(notFoundError));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Branch not found"))
                                .verify();
        }

        @Test
        @DisplayName("Should handle franchise not found error")
        void shouldHandleFranchiseNotFoundError() {

                IllegalArgumentException notFoundError = new IllegalArgumentException("Franchise not found");

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.error(notFoundError));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Franchise not found"))
                                .verify();
        }

        @Test
        @DisplayName("Should update stock with different IDs")
        void shouldUpdateStockWithDifferentIds() {

                String differentFranchiseId = "franchise999";
                String differentBranchId = "branch888";
                String differentProductId = "product777";
                Integer differentStock = 500;

                Product updatedProduct = Product.builder()
                                .id(differentProductId)
                                .name("Fanta")
                                .stock(differentStock)
                                .branchId(differentBranchId)
                                .build();

                when(productRepository.updateProductStock(
                                eq(differentProductId),
                                eq(differentStock)))
                                .thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(useCase.updateStock(
                                differentProductId,
                                differentStock))
                                .expectNextMatches(p -> p.getId().equals(differentProductId) &&
                                                p.getStock().equals(differentStock))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should log success when stock is updated")
        void shouldLogSuccessWhenStockIsUpdated() {

                Product updatedProduct = Product.builder()
                                .id(productId)
                                .name("Test Product")
                                .stock(newStock)
                                .branchId(branchId)
                                .build();

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.just(updatedProduct));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectNextCount(1)
                                .verifyComplete();

                verify(productRepository).updateProductStock(
                                eq(productId),
                                eq(newStock));
        }

        @Test
        @DisplayName("Should handle timeout error")
        void shouldHandleTimeoutError() {

                RuntimeException timeoutError = new RuntimeException("Request timeout");

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.error(timeoutError));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectErrorMatches(throwable -> throwable.getMessage().equals("Request timeout"))
                                .verify();
        }

        @Test
        @DisplayName("Should handle concurrent update error")
        void shouldHandleConcurrentUpdateError() {

                RuntimeException concurrentError = new RuntimeException(
                                "Product stock was modified by another process");

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(newStock)))
                                .thenReturn(Mono.error(concurrentError));

                StepVerifier.create(useCase.updateStock(productId, newStock))
                                .expectErrorMatches(throwable -> throwable.getMessage()
                                                .contains("modified by another process"))
                                .verify();
        }

        @Test
        @DisplayName("Should update stock multiple times in sequence")
        void shouldUpdateStockMultipleTimes() {

                Integer firstStock = 100;
                Integer secondStock = 200;

                Product firstUpdate = Product.builder()
                                .id(productId)
                                .name("Product")
                                .stock(firstStock)
                                .branchId(branchId)
                                .build();

                Product secondUpdate = Product.builder()
                                .id(productId)
                                .name("Product")
                                .stock(secondStock)
                                .branchId(branchId)
                                .build();

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(firstStock)))
                                .thenReturn(Mono.just(firstUpdate));

                when(productRepository.updateProductStock(
                                eq(productId),
                                eq(secondStock)))
                                .thenReturn(Mono.just(secondUpdate));

                StepVerifier.create(useCase.updateStock(productId, firstStock))
                                .expectNextMatches(p -> p.getStock().equals(firstStock))
                                .verifyComplete();

                StepVerifier.create(useCase.updateStock(productId, secondStock))
                                .expectNextMatches(p -> p.getStock().equals(secondStock))
                                .verifyComplete();
        }
}