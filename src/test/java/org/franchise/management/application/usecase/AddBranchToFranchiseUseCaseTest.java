package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.repository.BranchRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AddBranchToFranchiseUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddBranchToFranchiseUseCase Tests")
class AddBranchToFranchiseUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private AddBranchToFranchiseUseCase useCase;

    private Branch branch;
    private String franchiseId;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise123";
        branch = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .build();
    }

    @Test
    @DisplayName("Should add branch to franchise successfully")
    void shouldAddBranchToFranchiseSuccessfully() {
        // Given
        Branch savedBranch = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        // When & Then
        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectNextMatches(b -> b.getId().equals("branch456") &&
                        b.getName().equals("Sucursal Centro") &&
                        b.getFranchiseId().equals(franchiseId))
                .verifyComplete();

        // Verify interactions
        verify(branchRepository, times(1))
                .addBranchToFranchise(eq(franchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should set franchise ID before saving")
    void shouldSetFranchiseIdBeforeSaving() {
        // Given
        Branch branchWithoutFranchiseId = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .build();

        Branch savedBranch = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        // When
        StepVerifier.create(useCase.addBranch(franchiseId, branchWithoutFranchiseId))
                .expectNextMatches(b -> b.getFranchiseId().equals(franchiseId))
                .verifyComplete();

        // Then - verify that franchiseId was set on the branch object
        assert branchWithoutFranchiseId.getFranchiseId().equals(franchiseId);
    }

    @Test
    @DisplayName("Should return error when franchise not found")
    void shouldReturnErrorWhenFranchiseNotFound() {
        // Given
        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Franquicia no encontrada."))
                .verify();

        verify(branchRepository, times(1))
                .addBranchToFranchise(eq(franchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle repository error")
    void shouldHandleRepositoryError() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Database connection error");

        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(repositoryError));

        // When & Then
        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection error"))
                .verify();

        verify(branchRepository, times(1))
                .addBranchToFranchise(eq(franchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle null franchise ID")
    void shouldHandleNullFranchiseId() {
        // Given
        when(branchRepository.addBranchToFranchise(eq(null), any(Branch.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Franchise ID cannot be null")));

        // When & Then
        StepVerifier.create(useCase.addBranch(null, branch))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Franchise ID cannot be null"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate IllegalArgumentException from repository")
    void shouldPropagateIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid branch data");

        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(exception));

        // When & Then
        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Invalid branch data"))
                .verify();
    }

    @Test
    @DisplayName("Should handle empty franchise ID")
    void shouldHandleEmptyFranchiseId() {
        // Given
        when(branchRepository.addBranchToFranchise(eq(""), any(Branch.class)))
                .thenReturn(Mono.error(new IllegalArgumentException("Franchise ID cannot be empty")));

        // When & Then
        StepVerifier.create(useCase.addBranch("", branch))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException)
                .verify();
    }

    @Test
    @DisplayName("Should log success when branch is added")
    void shouldLogSuccessWhenBranchIsAdded() {
        // Given
        Branch savedBranch = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        // When
        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectNextCount(1)
                .verifyComplete();

        // Then - verify repository was called
        verify(branchRepository).addBranchToFranchise(eq(franchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should maintain branch name after adding")
    void shouldMaintainBranchNameAfterAdding() {
        // Given
        String expectedName = "Sucursal Norte";
        Branch branchWithName = Branch.builder()
                .name(expectedName)
                .build();

        Branch savedBranch = Branch.builder()
                .id("branch789")
                .name(expectedName)
                .franchiseId(franchiseId)
                .build();

        when(branchRepository.addBranchToFranchise(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        // When & Then
        StepVerifier.create(useCase.addBranch(franchiseId, branchWithName))
                .expectNextMatches(b -> b.getName().equals(expectedName))
                .verifyComplete();
    }
}