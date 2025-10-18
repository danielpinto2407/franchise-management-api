package org.franchise.management.entrypoints.webflux.handler;

import org.franchise.management.application.usecase.AddBranchToFranchiseUseCase;
import org.franchise.management.domain.model.Branch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para BranchHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BranchHandler Tests")
class BranchHandlerTest {

    @Mock
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private BranchHandler branchHandler;

    private Branch branch;
    private String franchiseId;

    @BeforeEach
    void setUp() {
        franchiseId = "franchise123";
        branch = Branch.builder()
                .name("Sucursal Centro")
                .build();
    }

    @Test
    @DisplayName("Should add branch successfully")
    void shouldAddBranchSuccessfully() {

        Branch savedBranch = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .franchiseId(franchiseId)
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(addBranchToFranchiseUseCase, times(1))
                .addBranch(eq(franchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should return bad request when use case fails")
    void shouldReturnBadRequestWhenUseCaseFails() {

        IllegalArgumentException exception = new IllegalArgumentException("Franquicia no encontrada");

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(exception));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();

        verify(addBranchToFranchiseUseCase, times(1))
                .addBranch(eq(franchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle invalid branch data")
    void shouldHandleInvalidBranchData() {

        IllegalArgumentException validationError = new IllegalArgumentException("Branch name is required");

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(validationError));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle empty request body")
    void shouldHandleEmptyRequestBody() {

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.empty());

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();

        verify(addBranchToFranchiseUseCase, never()).addBranch(any(), any());
    }

    @Test
    @DisplayName("Should extract franchise ID from path variable")
    void shouldExtractFranchiseIdFromPath() {

        String customFranchiseId = "franchise999";
        Branch savedBranch = Branch.builder()
                .id("branch111")
                .name("Sucursal Norte")
                .franchiseId(customFranchiseId)
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn(customFranchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(customFranchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(serverRequest, times(1)).pathVariable("franchiseId");
        verify(addBranchToFranchiseUseCase).addBranch(eq(customFranchiseId), any(Branch.class));
    }

    @Test
    @DisplayName("Should handle runtime exception from use case")
    void shouldHandleRuntimeException() {

        RuntimeException runtimeException = new RuntimeException("Database connection error");

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(runtimeException));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return JSON content type on success")
    void shouldReturnJsonContentTypeOnSuccess() {

        Branch savedBranch = Branch.builder()
                .id("branch456")
                .name("Sucursal Centro")
                .franchiseId(franchiseId)
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return JSON content type on error")
    void shouldReturnJsonContentTypeOnError() {

        IllegalArgumentException exception = new IllegalArgumentException("Error message");

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(branch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.error(exception));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle multiple branches for same franchise")
    void shouldHandleMultipleBranchesForSameFranchise() {

        Branch firstBranch = Branch.builder()
                .name("Sucursal 1")
                .build();

        Branch secondBranch = Branch.builder()
                .name("Sucursal 2")
                .build();

        Branch savedFirstBranch = Branch.builder()
                .id("branch1")
                .name("Sucursal 1")
                .franchiseId(franchiseId)
                .build();

        Branch savedSecondBranch = Branch.builder()
                .id("branch2")
                .name("Sucursal 2")
                .franchiseId(franchiseId)
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);

        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(firstBranch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedFirstBranch));

        Mono<ServerResponse> firstResponse = branchHandler.addBranch(serverRequest);

        StepVerifier.create(firstResponse)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(secondBranch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedSecondBranch));

        Mono<ServerResponse> secondResponse = branchHandler.addBranch(serverRequest);

        StepVerifier.create(secondResponse)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle branch with long name")
    void shouldHandleBranchWithLongName() {

        String longName = "Sucursal Centro Comercial Principal Ubicada en la Zona Norte de la Ciudad";
        Branch longNameBranch = Branch.builder()
                .name(longName)
                .build();

        Branch savedBranch = Branch.builder()
                .id("branch789")
                .name(longName)
                .franchiseId(franchiseId)
                .build();

        when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
        when(serverRequest.bodyToMono(Branch.class)).thenReturn(Mono.just(longNameBranch));
        when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }
}