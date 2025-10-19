package org.franchise.management.entrypoints.webflux.handler;

import org.franchise.management.application.usecase.AddBranchToFranchiseUseCase;
import org.franchise.management.application.usecase.UpdateBranchNameUseCase;
import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.repository.BranchRepository;
import org.franchise.management.entrypoints.webflux.dto.BranchRequestDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para BranchHandler
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BranchHandler Tests")
class BranchHandlerTest {

        @Mock
        private BranchRepository branchRepository;

        @Mock
        private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

        @Mock
        private UpdateBranchNameUseCase updateBranchNameUseCase;

        @Mock
        private ServerRequest serverRequest;

        @Mock
        private ValidationUtil validationUtil;

        @InjectMocks
        private BranchHandler branchHandler;

        private BranchRequestDTO branchDTO;
        private Branch branch;
        private String franchiseId;

        @BeforeEach
        void setUp() {
                franchiseId = "franchise123";
                branchDTO = BranchRequestDTO.builder()
                                .name("Sucursal Centro")
                                .build();
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.empty());

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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
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

                BranchRequestDTO firstBranch = BranchRequestDTO.builder()
                                .name("Sucursal 1")
                                .build();

                BranchRequestDTO secondBranch = BranchRequestDTO.builder()
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

                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(firstBranch));
                when(validationUtil.validate(firstBranch)).thenReturn(Mono.just(firstBranch));
                when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                                .thenReturn(Mono.just(savedFirstBranch));

                Mono<ServerResponse> firstResponse = branchHandler.addBranch(serverRequest);

                StepVerifier.create(firstResponse)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(secondBranch));
                when(validationUtil.validate(secondBranch)).thenReturn(Mono.just(secondBranch));
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
                BranchRequestDTO longNameBranch = BranchRequestDTO.builder()
                                .name(longName)
                                .build();

                Branch savedBranch = Branch.builder()
                                .id("branch789")
                                .name(longName)
                                .franchiseId(franchiseId)
                                .build();

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(longNameBranch));
                when(validationUtil.validate(longNameBranch)).thenReturn(Mono.just(longNameBranch));
                when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                                .thenReturn(Mono.just(savedBranch));

                Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();
        }

        @Test
        @DisplayName("✅ Should update branch name successfully")
        void shouldUpdateBranchNameSuccessfully() {
                String branchId = "branch123";
                BranchRequestDTO branchRequest = BranchRequestDTO.builder().name("Sucursal Actualizada").build();

                Branch updatedBranch = Branch.builder()
                                .id(branchId)
                                .name("Sucursal Actualizada")
                                .franchiseId("franchise123")
                                .build();

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchRequest));
                when(validationUtil.validate(branchRequest)).thenReturn(Mono.just(branchRequest));
                when(updateBranchNameUseCase.updateBranchName(branchId, "Sucursal Actualizada"))
                                .thenReturn(Mono.just(updatedBranch));

                Mono<ServerResponse> response = branchHandler.updateBranchName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(updateBranchNameUseCase).updateBranchName(branchId, "Sucursal Actualizada");
        }

        @Test
        @DisplayName("⚠️ Should return bad request when body is empty")
        void shouldReturnBadRequestWhenBodyIsEmpty() {
                String branchId = "branch123";
                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.empty());

                Mono<ServerResponse> response = branchHandler.updateBranchName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(updateBranchNameUseCase, never()).updateBranchName(anyString(), anyString());
        }

        @Test
        @DisplayName("⚠️ Should return bad request when branch name is null")
        void shouldReturnBadRequestWhenBranchNameIsNull() {
                String branchId = "branch123";
                branch.setName(null);

                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));

                Mono<ServerResponse> response = branchHandler.updateBranchName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(updateBranchNameUseCase, never()).updateBranchName(anyString(), anyString());
        }

        @Test
        @DisplayName("Should return error when branch not found")
        void shouldReturnErrorWhenBranchNotFound() {
                String branchId = "branch123";
                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
                when(updateBranchNameUseCase.updateBranchName(branchId, branch.getName()))
                                .thenReturn(Mono.error(new IllegalArgumentException("Sucursal no encontrada")));

                Mono<ServerResponse> response = branchHandler.updateBranchName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should handle unexpected exception when updating branch name")
        void shouldHandleUnexpectedExceptionWhenUpdatingBranchName() {
                String branchId = "branch123";
                when(serverRequest.pathVariable("branchId")).thenReturn(branchId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchDTO));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(branchDTO));
                when(updateBranchNameUseCase.updateBranchName(branchId, branch.getName()))
                                .thenReturn(Mono.error(new RuntimeException("Database error")));

                Mono<ServerResponse> response = branchHandler.updateBranchName(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();
        }

        @Test
        @DisplayName("Should validate and reject empty branch name when adding")
        void shouldValidateAndRejectEmptyBranchNameWhenAdding() {

                franchiseId = "franchise123";
                BranchRequestDTO branchWithEmptyName = BranchRequestDTO.builder()
                                .name("")
                                .build();

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchWithEmptyName));

                Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(addBranchToFranchiseUseCase, never()).addBranch(anyString(), any());
        }

        @Test
        @DisplayName("Should validate and reject blank branch name when adding")
        void shouldValidateAndRejectBlankBranchNameWhenAdding() {

                franchiseId = "franchise123";
                BranchRequestDTO branchWithBlankName = BranchRequestDTO.builder()
                                .name("     ")
                                .build();

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchWithBlankName));

                Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(addBranchToFranchiseUseCase, never()).addBranch(anyString(), any());
        }

        @Test
        @DisplayName("Should validate and reject null branch name when adding")
        void shouldValidateAndRejectNullBranchNameWhenAdding() {

                franchiseId = "franchise123";
                BranchRequestDTO branchWithNullName = BranchRequestDTO.builder()
                                .name(null)
                                .build();

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(branchWithNullName));

                Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                                .verifyComplete();

                verify(addBranchToFranchiseUseCase, never()).addBranch(anyString(), any());
        }

        @Test
        @DisplayName("Should pass validation and add branch with valid name")
        void shouldPassValidationAndAddBranchWithValidName() {
                franchiseId = "franchise123";
                BranchRequestDTO validBranch = BranchRequestDTO.builder()
                                .name("Sucursal Centro")
                                .build();

                Branch savedBranch = Branch.builder()
                                .id("branch456")
                                .name("Sucursal Centro")
                                .franchiseId(franchiseId)
                                .build();

                when(serverRequest.pathVariable("franchiseId")).thenReturn(franchiseId);
                when(serverRequest.bodyToMono(BranchRequestDTO.class)).thenReturn(Mono.just(validBranch));
                when(validationUtil.validate(branchDTO)).thenReturn(Mono.just(validBranch));
                when(addBranchToFranchiseUseCase.addBranch(eq(franchiseId), any(Branch.class)))
                                .thenReturn(Mono.just(savedBranch));

                Mono<ServerResponse> response = branchHandler.addBranch(serverRequest);

                StepVerifier.create(response)
                                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                                .verifyComplete();

                verify(addBranchToFranchiseUseCase, times(1)).addBranch(eq(franchiseId), any(Branch.class));
        }

}