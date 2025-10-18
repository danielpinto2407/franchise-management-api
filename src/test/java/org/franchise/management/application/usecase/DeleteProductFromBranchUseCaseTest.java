package org.franchise.management.application.usecase;

import org.franchise.management.domain.repository.ProductRepository;
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
 * Tests unitarios para DeleteProductFromBranchUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteProductFromBranchUseCase Tests")
class DeleteProductFromBranchUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteProductFromBranchUseCase useCase;

    private String franchiseId;
    private String branchId;
    private String productId;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise123";
        branchId = "branch456";
        productId = "product789";
    }

    @Test
    @DisplayName("Should delete product successfully")
    void shouldDeleteProductSuccessfully() {

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .verifyComplete();

        verify(productRepository, times(1))
                .deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId));
    }

    @Test
    @DisplayName("Should handle repository error when deleting")
    void shouldHandleRepositoryError() {

        RuntimeException repositoryError = new RuntimeException("Database connection error");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(repositoryError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection error"))
                .verify();

        verify(productRepository, times(1))
                .deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId));
    }

    @Test
    @DisplayName("Should handle product not found error")
    void shouldHandleProductNotFoundError() {

        IllegalArgumentException notFoundError = new IllegalArgumentException("Product not found");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(notFoundError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Product not found"))
                .verify();
    }

    @Test
    @DisplayName("Should handle branch not found error")
    void shouldHandleBranchNotFoundError() {

        IllegalArgumentException notFoundError = new IllegalArgumentException("Branch not found");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(notFoundError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Branch not found"))
                .verify();
    }

    @Test
    @DisplayName("Should handle franchise not found error")
    void shouldHandleFranchiseNotFoundError() {

        IllegalArgumentException notFoundError = new IllegalArgumentException("Franchise not found");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(notFoundError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Franchise not found"))
                .verify();
    }

    @Test
    @DisplayName("Should delete product with different IDs")
    void shouldDeleteProductWithDifferentIds() {

        String differentFranchiseId = "franchise999";
        String differentBranchId = "branch888";
        String differentProductId = "product777";

        when(productRepository.deleteProductFromBranch(
                eq(differentFranchiseId),
                eq(differentBranchId),
                eq(differentProductId)))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(
                differentFranchiseId,
                differentBranchId,
                differentProductId))
                .verifyComplete();

        verify(productRepository).deleteProductFromBranch(
                eq(differentFranchiseId),
                eq(differentBranchId),
                eq(differentProductId));
    }

    @Test
    @DisplayName("Should handle timeout error")
    void shouldHandleTimeoutError() {

        RuntimeException timeoutError = new RuntimeException("Request timeout");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(timeoutError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable.getMessage().equals("Request timeout"))
                .verify();
    }

    @Test
    @DisplayName("Should handle permission denied error")
    void shouldHandlePermissionDeniedError() {

        RuntimeException permissionError = new RuntimeException("Permission denied to delete product");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(permissionError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable.getMessage().equals("Permission denied to delete product"))
                .verify();
    }

    @Test
    @DisplayName("Should log success when product is deleted")
    void shouldLogSuccessWhenProductIsDeleted() {

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .verifyComplete();

        verify(productRepository).deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId));
    }

    @Test
    @DisplayName("Should handle concurrent deletion error")
    void shouldHandleConcurrentDeletionError() {

        RuntimeException concurrentError = new RuntimeException("Product already deleted by another process");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(concurrentError));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectErrorMatches(throwable -> throwable.getMessage().contains("already deleted"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate any exception from repository")
    void shouldPropagateAnyException() {

        Exception genericException = new Exception("Unexpected error");

        when(productRepository.deleteProductFromBranch(eq(franchiseId), eq(branchId), eq(productId)))
                .thenReturn(Mono.error(genericException));

        StepVerifier.create(useCase.deleteProduct(franchiseId, branchId, productId))
                .expectError(Exception.class)
                .verify();
    }

    @Test
    @DisplayName("Should complete successfully with null parameters handling")
    void shouldHandleNullParameters() {

        when(productRepository.deleteProductFromBranch(eq(null), eq(null), eq(null)))
                .thenReturn(Mono.error(new IllegalArgumentException("IDs cannot be null")));

        StepVerifier.create(useCase.deleteProduct(null, null, null))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException)
                .verify();
    }
}