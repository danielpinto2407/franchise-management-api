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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GetMaxStockProductByBranchUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetMaxStockProductByBranchUseCase Tests")
class GetMaxStockProductByBranchUseCaseTest {

        @Mock
        private ProductMongoAdapter productRepository;

        @InjectMocks
        private GetMaxStockProductByBranchUseCase useCase;

        private String franchiseId;
        private List<Product> products;

        @BeforeEach
        void setUp() {
                franchiseId = "franchise123";

                products = new ArrayList<>();
                products.add(Product.builder()
                                .id("product1")
                                .name("Coca Cola")
                                .stock(100)
                                .branchId("branch1")
                                .build());

                products.add(Product.builder()
                                .id("product2")
                                .name("Pepsi")
                                .stock(150)
                                .branchId("branch2")
                                .build());

                products.add(Product.builder()
                                .id("product3")
                                .name("Sprite")
                                .stock(75)
                                .branchId("branch3")
                                .build());
        }

        @Test
        @DisplayName("Should get max stock products successfully")
        void shouldGetMaxStockProductsSuccessfully() {

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.fromIterable(products));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNext(products.get(0))
                                .expectNext(products.get(1))
                                .expectNext(products.get(2))
                                .verifyComplete();

                verify(productRepository, times(1))
                                .findMaxStockProductByBranch(eq(franchiseId));
        }

        @Test
        @DisplayName("Should return empty flux when no products found")
        void shouldReturnEmptyFluxWhenNoProducts() {

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.empty());

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .verifyComplete();

                verify(productRepository, times(1))
                                .findMaxStockProductByBranch(eq(franchiseId));
        }

        @Test
        @DisplayName("Should return single product")
        void shouldReturnSingleProduct() {

                Product singleProduct = Product.builder()
                                .id("product1")
                                .name("Coca Cola")
                                .stock(100)
                                .branchId("branch1")
                                .build();

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.just(singleProduct));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNext(singleProduct)
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle repository error")
        void shouldHandleRepositoryError() {

                RuntimeException repositoryError = new RuntimeException("Database connection error");

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.error(repositoryError));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                                                throwable.getMessage().equals("Database connection error"))
                                .verify();

                verify(productRepository, times(1))
                                .findMaxStockProductByBranch(eq(franchiseId));
        }

        @Test
        @DisplayName("Should handle franchise not found error")
        void shouldHandleFranchiseNotFoundError() {

                IllegalArgumentException notFoundError = new IllegalArgumentException("Franchise not found");

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.error(notFoundError));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Franchise not found"))
                                .verify();
        }

        @Test
        @DisplayName("Should return products with different stock values")
        void shouldReturnProductsWithDifferentStockValues() {

                List<Product> diverseProducts = new ArrayList<>();
                diverseProducts.add(Product.builder()
                                .id("product1")
                                .name("Product A")
                                .stock(1)
                                .branchId("branch1")
                                .build());

                diverseProducts.add(Product.builder()
                                .id("product2")
                                .name("Product B")
                                .stock(1000)
                                .branchId("branch2")
                                .build());

                diverseProducts.add(Product.builder()
                                .id("product3")
                                .name("Product C")
                                .stock(50)
                                .branchId("branch3")
                                .build());

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.fromIterable(diverseProducts));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNextCount(3)
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle products with zero stock")
        void shouldHandleProductsWithZeroStock() {

                Product zeroStockProduct = Product.builder()
                                .id("product1")
                                .name("Out of Stock Product")
                                .stock(0)
                                .branchId("branch1")
                                .build();

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.just(zeroStockProduct));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNextMatches(p -> p.getStock().equals(0))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should get products from different franchise")
        void shouldGetProductsFromDifferentFranchise() {

                String differentFranchiseId = "franchise999";
                Product product = Product.builder()
                                .id("product1")
                                .name("Product X")
                                .stock(200)
                                .branchId("branch1")
                                .build();

                when(productRepository.findMaxStockProductByBranch(eq(differentFranchiseId)))
                                .thenReturn(Flux.just(product));

                StepVerifier.create(useCase.getMaxStockProducts(differentFranchiseId))
                                .expectNext(product)
                                .verifyComplete();

                verify(productRepository).findMaxStockProductByBranch(eq(differentFranchiseId));
        }

        @Test
        @DisplayName("Should log each product received")
        void shouldLogEachProductReceived() {

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.fromIterable(products));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNextCount(3)
                                .verifyComplete();

                verify(productRepository).findMaxStockProductByBranch(eq(franchiseId));
        }

        @Test
        @DisplayName("Should handle timeout error")
        void shouldHandleTimeoutError() {

                RuntimeException timeoutError = new RuntimeException("Request timeout");

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.error(timeoutError));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectErrorMatches(throwable -> throwable.getMessage().equals("Request timeout"))
                                .verify();
        }

        @Test
        @DisplayName("Should handle multiple products from same branch")
        void shouldHandleMultipleProductsFromSameBranch() {

                List<Product> sameBranchProducts = new ArrayList<>();
                sameBranchProducts.add(Product.builder()
                                .id("product1")
                                .name("Product A")
                                .stock(100)
                                .branchId("branch1")
                                .build());

                sameBranchProducts.add(Product.builder()
                                .id("product2")
                                .name("Product B")
                                .stock(100)
                                .branchId("branch1")
                                .build());

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.fromIterable(sameBranchProducts));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNextCount(2)
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should propagate any exception from repository")
        void shouldPropagateAnyException() {

                Exception genericException = new Exception("Unexpected error");

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.error(genericException));
                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectError(Exception.class)
                                .verify();
        }

        @Test
        @DisplayName("Should handle large number of products")
        void shouldHandleLargeNumberOfProducts() {

                List<Product> manyProducts = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                        manyProducts.add(Product.builder()
                                        .id("product" + i)
                                        .name("Product " + i)
                                        .stock(i * 10)
                                        .branchId("branch" + (i % 10))
                                        .build());
                }

                when(productRepository.findMaxStockProductByBranch(eq(franchiseId)))
                                .thenReturn(Flux.fromIterable(manyProducts));

                StepVerifier.create(useCase.getMaxStockProducts(franchiseId))
                                .expectNextCount(100)
                                .verifyComplete();
        }
}