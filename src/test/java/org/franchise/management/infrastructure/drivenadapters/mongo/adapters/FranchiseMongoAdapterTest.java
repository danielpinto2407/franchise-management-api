package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import org.franchise.management.domain.model.Franchise;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.FranchiseMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseMongoAdapterTest {

    @Mock
    private FranchiseMongoRepository franchiseMongoRepository;

    @InjectMocks
    private FranchiseMongoAdapter franchiseMongoAdapter;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id("franchise123")
                .name("Franchise Test")
                .build();
    }

    @Test
    @DisplayName("Should save a new franchise successfully")
    void shouldSaveFranchiseSuccessfully() {
        when(franchiseMongoRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseMongoAdapter.save(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseMongoRepository).save(franchise);
    }

    @Test
    @DisplayName("Should update franchise successfully")
    void shouldUpdateFranchiseSuccessfully() {
        when(franchiseMongoRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseMongoAdapter.update(franchise))
                .expectNext(franchise)
                .verifyComplete();

        verify(franchiseMongoRepository).save(franchise);
    }

    @Test
    @DisplayName("Should handle error when saving franchise fails")
    void shouldHandleErrorWhenSavingFranchiseFails() {
        when(franchiseMongoRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(franchiseMongoAdapter.save(franchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(franchiseMongoRepository).save(franchise);
    }

    @Test
    @DisplayName("Should handle error when updating franchise fails")
    void shouldHandleErrorWhenUpdatingFranchiseFails() {
        when(franchiseMongoRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Update failed")));

        StepVerifier.create(franchiseMongoAdapter.update(franchise))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Update failed"))
                .verify();

        verify(franchiseMongoRepository).save(franchise);
    }
}
