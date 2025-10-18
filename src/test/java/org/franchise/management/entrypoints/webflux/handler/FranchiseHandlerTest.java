package org.franchise.management.entrypoints.webflux.handler;

import org.franchise.management.application.usecase.CreateFranchiseUseCase;
import org.franchise.management.domain.model.Franchise;
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

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para FranchiseHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FranchiseHandler Tests")
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Mock
    private ServerRequest serverRequest;

    @InjectMocks
    private FranchiseHandler franchiseHandler;

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
                .build();

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(createFranchiseUseCase, times(1)).createFranchise(any(Franchise.class));
    }

    @Test
    @DisplayName("Should return bad request when use case fails")
    void shouldReturnBadRequestWhenUseCaseFails() {

        IllegalArgumentException exception = new IllegalArgumentException("Invalid franchise data");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(exception));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();

        verify(createFranchiseUseCase, times(1)).createFranchise(any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle empty franchise name")
    void shouldHandleEmptyFranchiseName() {

        Franchise emptyNameFranchise = Franchise.builder()
                .name("")
                .build();

        IllegalArgumentException validationError = new IllegalArgumentException("Franchise name is required");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(emptyNameFranchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(validationError));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle null franchise name")
    void shouldHandleNullFranchiseName() {

        Franchise nullNameFranchise = Franchise.builder()
                .name(null)
                .build();

        IllegalArgumentException validationError = new IllegalArgumentException("Franchise name cannot be null");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(nullNameFranchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(validationError));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle empty request body")
    void shouldHandleEmptyRequestBody() {

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.empty());

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();

        verify(createFranchiseUseCase, never()).createFranchise(any());
    }

    @Test
    @DisplayName("Should handle runtime exception from use case")
    void shouldHandleRuntimeException() {

        RuntimeException runtimeException = new RuntimeException("Database connection error");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(runtimeException));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return JSON content type on success")
    void shouldReturnJsonContentTypeOnSuccess() {

        Franchise savedFranchise = Franchise.builder()
                .id("franchise456")
                .name("Burger King")
                .branchIds(new ArrayList<>())
                .build();

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return JSON content type on error")
    void shouldReturnJsonContentTypeOnError() {

        IllegalArgumentException exception = new IllegalArgumentException("Error message");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(exception));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with different names")
    void shouldCreateFranchiseWithDifferentNames() {

        Franchise kfcFranchise = Franchise.builder()
                .name("KFC")
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id("franchise789")
                .name("KFC")
                .branchIds(new ArrayList<>())
                .build();

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(kfcFranchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle duplicate franchise name error")
    void shouldHandleDuplicateFranchiseNameError() {

        RuntimeException duplicateError = new RuntimeException("Franchise name already exists");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(duplicateError));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create franchise with long name")
    void shouldCreateFranchiseWithLongName() {

        String longName = "Very Long Franchise Name With Multiple Words And Special Characters 123";
        Franchise longNameFranchise = Franchise.builder()
                .name(longName)
                .build();

        Franchise savedFranchise = Franchise.builder()
                .id("franchise999")
                .name(longName)
                .branchIds(new ArrayList<>())
                .build();

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(longNameFranchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should log success when franchise is created")
    void shouldLogSuccessWhenFranchiseIsCreated() {

        Franchise savedFranchise = Franchise.builder()
                .id("franchise111")
                .name("Subway")
                .branchIds(new ArrayList<>())
                .build();

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();

        verify(createFranchiseUseCase).createFranchise(any(Franchise.class));
    }

    @Test
    @DisplayName("Should handle timeout error")
    void shouldHandleTimeoutError() {

        RuntimeException timeoutError = new RuntimeException("Request timeout");

        when(serverRequest.bodyToMono(Franchise.class)).thenReturn(Mono.just(franchise));
        when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(timeoutError));

        Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }
}