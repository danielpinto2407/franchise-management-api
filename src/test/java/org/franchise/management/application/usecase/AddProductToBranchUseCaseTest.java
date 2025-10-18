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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AddProductToBranchUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddProductToBranchUseCase Tests")
class AddProductToBranchUseCaseTest {

        @Mock
        private ProductMongoAdapter productRepository;

        @InjectMocks
        private AddProductToBranchUseCase useCase;

        private Product product;
        private String franchiseId;
        private String branchId;

        @BeforeEach
        void setUp() {
                franchiseId = "franchise123";
                branchId = "branch456";
                product = Product.builder()
                                .id("product789")
                                .name("Coca Cola")
                                .stock(100)
                                .branchId(branchId)
                                .build();
        }

        @Test
        @DisplayName("Should add product to branch successfully")
        void shouldAddProductToBranchSuccessfully() {

                Product savedProduct = Product.builder()
                                .id("product789")
                                .name("Coca Cola")
                                .stock(100)
                                .branchId(branchId)
                                .build();

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.just(savedProduct));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, product))
                                .expectNextMatches(p -> p.getId().equals("product789") &&
                                                p.getName().equals("Coca Cola") &&
                                                p.getStock().equals(100) &&
                                                p.getBranchId().equals(branchId))
                                .verifyComplete();

                verify(productRepository, times(1))
                                .addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class));
        }

        @Test
        @DisplayName("Should return error when branch not found")
        void shouldReturnErrorWhenBranchNotFound() {

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.empty());

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, product))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Franquicia o sucursal no encontradas."))
                                .verify();

                verify(productRepository, times(1))
                                .addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class));
        }

        @Test
        @DisplayName("Should handle repository error")
        void shouldHandleRepositoryError() {

                RuntimeException repositoryError = new RuntimeException("Database connection error");

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.error(repositoryError));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, product))
                                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                                                throwable.getMessage().equals("Database connection error"))
                                .verify();

                verify(productRepository, times(1))
                                .addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class));
        }

        @Test
        @DisplayName("Should propagate IllegalArgumentException from repository")
        void shouldPropagateIllegalArgumentException() {

                IllegalArgumentException exception = new IllegalArgumentException("Invalid product data");

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.error(exception));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, product))
                                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                                                throwable.getMessage().equals("Invalid product data"))
                                .verify();
        }

        @Test
        @DisplayName("Should add product with zero stock")
        void shouldAddProductWithZeroStock() {

                Product productWithZeroStock = Product.builder()
                                .id("product999")
                                .name("Producto Agotado")
                                .stock(0)
                                .branchId(branchId)
                                .build();

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.just(productWithZeroStock));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, productWithZeroStock))
                                .expectNextMatches(p -> p.getStock().equals(0))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should add product with large stock quantity")
        void shouldAddProductWithLargeStock() {

                Product productWithLargeStock = Product.builder()
                                .id("product888")
                                .name("Producto Popular")
                                .stock(10000)
                                .branchId(branchId)
                                .build();

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.just(productWithLargeStock));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, productWithLargeStock))
                                .expectNextMatches(p -> p.getStock().equals(10000))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should maintain product name after adding")
        void shouldMaintainProductNameAfterAdding() {

                String expectedName = "Pepsi";
                Product productWithName = Product.builder()
                                .name(expectedName)
                                .stock(50)
                                .branchId(branchId)
                                .build();

                Product savedProduct = Product.builder()
                                .id("product777")
                                .name(expectedName)
                                .stock(50)
                                .branchId(branchId)
                                .build();

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.just(savedProduct));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, productWithName))
                                .expectNextMatches(p -> p.getName().equals(expectedName))
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle different franchise and branch IDs")
        void shouldHandleDifferentIds() {

                String differentFranchiseId = "franchise999";
                String differentBranchId = "branch888";

                Product savedProduct = Product.builder()
                                .id("product777")
                                .name("Sprite")
                                .stock(75)
                                .branchId(differentBranchId)
                                .build();

                when(productRepository.addProductToBranch(
                                eq(differentFranchiseId),
                                eq(differentBranchId),
                                any(Product.class)))
                                .thenReturn(Mono.just(savedProduct));

                StepVerifier.create(useCase.addProduct(differentFranchiseId, differentBranchId, product))
                                .expectNextMatches(p -> p.getBranchId().equals(differentBranchId))
                                .verifyComplete();

                verify(productRepository).addProductToBranch(
                                eq(differentFranchiseId),
                                eq(differentBranchId),
                                any(Product.class));
        }

        @Test
        @DisplayName("Should log success when product is added")
        void shouldLogSuccessWhenProductIsAdded() {

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.just(product));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, product))
                                .expectNextCount(1)
                                .verifyComplete();

                verify(productRepository).addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class));
        }

        @Test
        @DisplayName("Should handle timeout error from repository")
        void shouldHandleTimeoutError() {
                RuntimeException timeoutError = new RuntimeException("Request timeout");

                when(productRepository.addProductToBranch(eq(franchiseId), eq(branchId), any(Product.class)))
                                .thenReturn(Mono.error(timeoutError));

                StepVerifier.create(useCase.addProduct(franchiseId, branchId, product))
                                .expectErrorMatches(throwable -> throwable.getMessage().equals("Request timeout"))
                                .verify();
        }
}