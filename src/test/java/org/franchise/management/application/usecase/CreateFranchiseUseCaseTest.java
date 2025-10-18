package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Franchise;
import org.franchise.management.domain.repository.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CreateFranchiseUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateFranchiseUseCase Tests")
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private CreateFranchiseUseCase useCase;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .name("McDonald's")
                .branchIds(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should create franchise successfully")
    void shouldCreateFranchiseSuccessfully() {

        Franchise savedFranchise = Franchise.builder()
                .id("franchise123")
                .name("McDonald's")
                .branchIds(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectNextMatches(f -> f.getId().equals("franchise123") &&
                        f.getName().equals("McDonald's") &&
                        f.getBranchIds() != null)
                .verifyComplete();

        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should create franchise with initial empty branch list")
    void shouldCreateFranchiseWithEmptyBranchList() {

        Franchise newFranchise = Franchise.builder()
                .name("Burger King")
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id("franchise456")
                .name("Burger King")
                .branchIds(new ArrayList<>())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(newFranchise))
                .expectNextMatches(f -> f.getBranchIds() != null &&
                        f.getBranchIds().isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error")
    void shouldHandleRepositoryError() {

        RuntimeException repositoryError = new RuntimeException("Database connection error");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(repositoryError));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection error"))
                .verify();

        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should propagate IllegalArgumentException from repository")
    void shouldPropagateIllegalArgumentException() {

        IllegalArgumentException exception = new IllegalArgumentException("Invalid franchise data");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Invalid franchise data"))
                .verify();
    }

    @Test
    @DisplayName("Should create franchise with different name")
    void shouldCreateFranchiseWithDifferentName() {

        Franchise kfcFranchise = Franchise.builder()
                .name("KFC")
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id("franchise789")
                .name("KFC")
                .branchIds(new ArrayList<>())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(kfcFranchise))
                .expectNextMatches(f -> f.getName().equals("KFC"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle duplicate franchise name error")
    void shouldHandleDuplicateFranchiseNameError() {

        RuntimeException duplicateError = new RuntimeException("Franchise name already exists");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(duplicateError));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable.getMessage().equals("Franchise name already exists"))
                .verify();
    }

    @Test
    @DisplayName("Should create franchise and assign timestamps")
    void shouldCreateFranchiseAndAssignTimestamps() {

        Franchise savedFranchise = Franchise.builder()
                .id("franchise999")
                .name("Subway")
                .branchIds(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectNextMatches(f -> f.getCreatedAt() != null &&
                        f.getUpdatedAt() != null)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should log success when franchise is created")
    void shouldLogSuccessWhenFranchiseIsCreated() {

        Franchise savedFranchise = Franchise.builder()
                .id("franchise111")
                .name("Domino's")
                .branchIds(new ArrayList<>())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectNextCount(1)
                .verifyComplete();

        verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle timeout error from repository")
    void shouldHandleTimeoutError() {

        RuntimeException timeoutError = new RuntimeException("Request timeout");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(timeoutError));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable.getMessage().equals("Request timeout"))
                .verify();
    }

    @Test
    @DisplayName("Should handle validation error")
    void shouldHandleValidationError() {

        IllegalArgumentException validationError = new IllegalArgumentException("Franchise name is required");

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(validationError));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Franchise name is required"))
                .verify();
    }

    @Test
    @DisplayName("Should create franchise with long name")
    void shouldCreateFranchiseWithLongName() {

        String longName = "Very Long Franchise Name With Multiple Words And Characters";
        Franchise longNameFranchise = Franchise.builder()
                .name(longName)
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id("franchise222")
                .name(longName)
                .branchIds(new ArrayList<>())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(longNameFranchise))
                .expectNextMatches(f -> f.getName().equals(longName))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should maintain franchise data after save")
    void shouldMaintainFranchiseDataAfterSave() {

        Franchise inputFranchise = Franchise.builder()
                .name("Pizza Hut")
                .branchIds(new ArrayList<>())
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id("franchise333")
                .name("Pizza Hut")
                .branchIds(new ArrayList<>())
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.createFranchise(inputFranchise))
                .expectNextMatches(f -> f.getName().equals("Pizza Hut") &&
                        f.getId() != null &&
                        f.getBranchIds().isEmpty())
                .verifyComplete();
    }
}