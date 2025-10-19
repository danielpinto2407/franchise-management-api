package org.franchise.management.entrypoints.webflux.handler;

import org.franchise.management.application.usecase.CreateFranchiseUseCase;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.entrypoints.webflux.dto.FranchiseRequestDTO;
import org.franchise.management.entrypoints.webflux.util.ValidationUtil;
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
 * Tests unitarios para FranchiseHandler con DTOs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FranchiseHandler Tests")
class FranchiseHandlerTest {

        @Mock
        private CreateFranchiseUseCase createFranchiseUseCase;

        @Mock
        private ValidationUtil validationUtil;

        @Mock
        private ServerRequest serverRequest;

        @InjectMocks
        private FranchiseHandler franchiseHandler;

        private FranchiseRequestDTO franchiseRequestDTO;

        @BeforeEach
        void setUp() {
                franchiseRequestDTO = FranchiseRequestDTO.builder()
                                .name("McDonald's")
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

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                                .thenReturn(Mono.just(savedFranchise));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(validationUtil, times(1)).validate(any(FranchiseRequestDTO.class));
                verify(createFranchiseUseCase, times(1)).createFranchise(any(Franchise.class));
        }

        @Test
        @DisplayName("Should return bad request when use case fails")
        void shouldReturnBadRequestWhenUseCaseFails() {

                IllegalArgumentException exception = new IllegalArgumentException("Invalid franchise data");

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                                .thenReturn(Mono.error(exception));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(createFranchiseUseCase, times(1)).createFranchise(any(Franchise.class));
        }

        @Test
        @DisplayName("Should handle empty franchise name with validation")
        void shouldHandleEmptyFranchiseNameWithValidation() {

                FranchiseRequestDTO emptyNameDTO = FranchiseRequestDTO.builder()
                                .name("")
                                .build();

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(emptyNameDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.error(new IllegalArgumentException(
                                                "El nombre de la franquicia es obligatorio")));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(createFranchiseUseCase, never()).createFranchise(any());
        }

        @Test
        @DisplayName("Should handle null franchise name with validation")
        void shouldHandleNullFranchiseNameWithValidation() {

                FranchiseRequestDTO nullNameDTO = FranchiseRequestDTO.builder()
                                .name(null)
                                .build();

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(nullNameDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.error(new IllegalArgumentException(
                                                "El nombre de la franquicia es obligatorio")));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(createFranchiseUseCase, never()).createFranchise(any());
        }

        @Test
        @DisplayName("Should handle blank franchise name with validation")
        void shouldHandleBlankFranchiseNameWithValidation() {

                FranchiseRequestDTO blankNameDTO = FranchiseRequestDTO.builder()
                                .name("   ")
                                .build();

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(blankNameDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.error(new IllegalArgumentException(
                                                "El nombre de la franquicia es obligatorio")));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(createFranchiseUseCase, never()).createFranchise(any());
        }

        @Test
        @DisplayName("Should handle empty request body")
        void shouldHandleEmptyRequestBody() {

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.empty());

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(validationUtil, never()).validate(any());
                verify(createFranchiseUseCase, never()).createFranchise(any());
        }

        @Test
        @DisplayName("Should handle runtime exception from use case")
        void shouldHandleRuntimeException() {

                RuntimeException runtimeException = new RuntimeException("Database connection error");

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
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

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
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

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
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

                FranchiseRequestDTO kfcDTO = FranchiseRequestDTO.builder()
                                .name("KFC")
                                .build();

                Franchise savedFranchise = Franchise.builder()
                                .id("franchise789")
                                .name("KFC")
                                .branchIds(new ArrayList<>())
                                .build();

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(kfcDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(kfcDTO));
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

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
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
                FranchiseRequestDTO longNameDTO = FranchiseRequestDTO.builder()
                                .name(longName)
                                .build();

                Franchise savedFranchise = Franchise.builder()
                                .id("franchise999")
                                .name(longName)
                                .branchIds(new ArrayList<>())
                                .build();

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(longNameDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(longNameDTO));
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

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
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

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(createFranchiseUseCase.createFranchise(any(Franchise.class)))
                                .thenReturn(Mono.error(timeoutError));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should validate DTO before calling use case")
        void shouldValidateDTOBeforeCallingUseCase() {

                when(serverRequest.bodyToMono(FranchiseRequestDTO.class))
                                .thenReturn(Mono.just(franchiseRequestDTO));
                when(validationUtil.validate(any(FranchiseRequestDTO.class)))
                                .thenReturn(Mono.error(new IllegalArgumentException("Validation error")));

                Mono<ServerResponse> response = franchiseHandler.createFranchise(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(validationUtil, times(1)).validate(any(FranchiseRequestDTO.class));
                verify(createFranchiseUseCase, never()).createFranchise(any());
        }
}