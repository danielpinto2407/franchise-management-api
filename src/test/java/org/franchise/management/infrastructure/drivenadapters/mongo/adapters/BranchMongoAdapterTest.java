package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.BranchMongoRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.FranchiseMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchMongoAdapterTest {

    @Mock
    private BranchMongoRepository branchMongoRepository;

    @Mock
    private FranchiseMongoRepository franchiseMongoRepository;

    @InjectMocks
    private BranchMongoAdapter branchMongoAdapter;

    private Branch branch;
    private Franchise franchise;
    private final String branchId = "branch123";
    private final String franchiseId = "franchise123";

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id(branchId)
                .name("Main Branch")
                .build();

        franchise = Franchise.builder()
                .id(franchiseId)
                .branchIds(Collections.singletonList(branchId))
                .build();
    }

    @Test
    @DisplayName("Should add branch to an existing franchise")
    void shouldAddBranchToExistingFranchise() {
        when(franchiseMongoRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchMongoRepository.save(any(Branch.class))).thenReturn(Mono.just(branch));
        when(franchiseMongoRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(branchMongoAdapter.addBranchToFranchise(franchiseId, branch))
                .expectNext(branch)
                .verifyComplete();

        verify(franchiseMongoRepository).findById(franchiseId);
        verify(branchMongoRepository).save(branch);
        verify(franchiseMongoRepository).save(franchise);
    }

    @Test
    @DisplayName("Should throw error when franchise not found while adding branch")
    void shouldThrowErrorWhenFranchiseNotFound() {
        when(franchiseMongoRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(branchMongoAdapter.addBranchToFranchise(franchiseId, branch))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Franquicia no encontrada"))
                .verify();

        verify(branchMongoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should list all branches of a franchise")
    void shouldFindAllBranchesByFranchise() {
        when(franchiseMongoRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.just(branch));

        StepVerifier.create(branchMongoAdapter.findAllByFranchise(franchiseId))
                .expectNext(branch)
                .verifyComplete();

        verify(franchiseMongoRepository).findById(franchiseId);
        verify(branchMongoRepository).findById(branchId);
    }

    @Test
    @DisplayName("Should return empty flux when franchise has no branches")
    void shouldReturnEmptyFluxWhenFranchiseHasNoBranches() {
        franchise.setBranchIds(Collections.emptyList());
        when(franchiseMongoRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));

        StepVerifier.create(branchMongoAdapter.findAllByFranchise(franchiseId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find branch by ID")
    void shouldFindBranchById() {
        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.just(branch));

        StepVerifier.create(branchMongoAdapter.findById(branchId))
                .expectNext(branch)
                .verifyComplete();

        verify(branchMongoRepository).findById(branchId);
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateBranchName() {
        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(branchMongoRepository.save(any())).thenReturn(Mono.just(branch));

        StepVerifier.create(branchMongoAdapter.updateBranchName(branchId, "New Name"))
                .expectNext(branch)
                .verifyComplete();

        verify(branchMongoRepository).findById(branchId);
        verify(branchMongoRepository).save(branch);
    }

    @Test
    @DisplayName("Should return empty when branch not found while updating name")
    void shouldReturnEmptyWhenBranchNotFoundOnUpdate() {
        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(branchMongoAdapter.updateBranchName(branchId, "New Name"))
                .verifyComplete();

        verify(branchMongoRepository, never()).save(any());
    }
}
