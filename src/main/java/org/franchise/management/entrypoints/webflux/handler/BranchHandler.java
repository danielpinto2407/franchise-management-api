package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.AddBranchToFranchiseUseCase;
import org.franchise.management.application.usecase.UpdateBranchNameUseCase;
import org.franchise.management.domain.model.Branch;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class BranchHandler {

        private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
        private final UpdateBranchNameUseCase updateBranchNameUseCase;

        /**
         * ✅ POST /franchises/{franchiseId}/branches
         * Agrega una nueva sucursal a una franquicia.
         */
        public Mono<ServerResponse> addBranch(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");

                return request.bodyToMono(Branch.class)
                                .flatMap(this::validateBranchName)
                                .flatMap(branch -> addBranchToFranchiseUseCase.addBranch(franchiseId, branch))
                                .flatMap(saved -> {
                                        log.info("✅ Sucursal agregada exitosamente a la franquicia {}: {}", franchiseId,
                                                        saved.getName());
                                        return buildOkResponse(saved);
                                })
                                .switchIfEmpty(buildBadRequest("El cuerpo de la solicitud está vacío"))
                                .onErrorResume(e -> buildBadRequest(e.getMessage()));
        }

        /**
         * ✅ PUT /branches/{branchId}/name
         * Actualiza el nombre de una sucursal existente.
         */
        public Mono<ServerResponse> updateBranchName(ServerRequest request) {
                String branchId = request.pathVariable("branchId");

                return request.bodyToMono(Branch.class)
                                .flatMap(this::validateBranchName)
                                .flatMap(validBody -> updateBranchNameUseCase.updateBranchName(branchId,
                                                validBody.getName()))
                                .flatMap(this::buildOkResponse)
                                .switchIfEmpty(buildBadRequest("El cuerpo de la solicitud está vacío"))
                                .onErrorResume(e -> buildBadRequest(e.getMessage()));
        }

        private Mono<Branch> validateBranchName(Branch branch) {
                if (branch.getName() == null || branch.getName().isBlank()) {
                        return Mono.error(
                                        new IllegalArgumentException("El nombre de la sucursal no puede estar vacío"));
                }
                return Mono.just(branch);
        }

        private Mono<ServerResponse> buildOkResponse(Object body) {
                return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(body);
        }

        private Mono<ServerResponse> buildBadRequest(String message) {
                log.error("❌ Error en solicitud: {}", message);
                return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", message));
        }
}
