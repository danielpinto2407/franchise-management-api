package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.BranchMongoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchMongoAdapter branchRepository;

    @InjectMocks
    private UpdateBranchNameUseCase updateBranchNameUseCase;

    private final String branchId = "branch123";
    private final String newName = "Nueva Sucursal";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debe actualizar el nombre de la sucursal exitosamente")
    void shouldUpdateBranchNameSuccessfully() {
        Branch updatedBranch = Branch.builder()
                .id(branchId)
                .name(newName)
                .build();

        when(branchRepository.updateBranchName(branchId, newName))
                .thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(updateBranchNameUseCase.updateBranchName(branchId, newName))
                .expectNextMatches(branch -> branch.getId().equals(branchId)
                        && branch.getName().equals(newName))
                .verifyComplete();

        verify(branchRepository, times(1)).updateBranchName(branchId, newName);
    }

    @Test
    @DisplayName("Debe lanzar error cuando la sucursal no existe")
    void shouldThrowErrorWhenBranchNotFound() {
        when(branchRepository.updateBranchName(branchId, newName))
                .thenReturn(Mono.empty());

        StepVerifier.create(updateBranchNameUseCase.updateBranchName(branchId, newName))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().contains("Sucursal no encontrada"))
                .verify();

        verify(branchRepository, times(1)).updateBranchName(branchId, newName);
    }

    @Test
    @DisplayName("Debe propagar errores del repositorio")
    void shouldPropagateRepositoryErrors() {
        when(branchRepository.updateBranchName(branchId, newName))
                .thenReturn(Mono.error(new RuntimeException("Error en la base de datos")));

        StepVerifier.create(updateBranchNameUseCase.updateBranchName(branchId, newName))
                .expectErrorMatches(error -> error instanceof RuntimeException &&
                        error.getMessage().contains("Error en la base de datos"))
                .verify();

        verify(branchRepository, times(1)).updateBranchName(branchId, newName);
    }
}
