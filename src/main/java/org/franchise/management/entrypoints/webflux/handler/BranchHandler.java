package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import org.franchise.management.application.usecase.AddBranchToFranchiseUseCase;
import org.franchise.management.application.usecase.UpdateBranchNameUseCase;
import org.franchise.management.domain.model.Branch;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class BranchHandler {

        private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
        private final UpdateBranchNameUseCase updateBranchNameUseCase;

        /**
         * POST /franchises/{franchiseId}/branches
         * Agrega una nueva sucursal a una franquicia
         */
        public Mono<ServerResponse> addBranch(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");

                return request.bodyToMono(Branch.class)
                                .flatMap(branch -> addBranchToFranchiseUseCase.addBranch(franchiseId, branch))
                                .flatMap(saved -> {
                                        log.info("✅ Sucursal agregada exitosamente a la franquicia {}: {}", franchiseId,
                                                        saved.getName());
                                        return ServerResponse.ok()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .bodyValue(saved);
                                })
                                .switchIfEmpty(ServerResponse.badRequest()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(Map.of("error", "El cuerpo de la solicitud está vacío")))
                                .onErrorResume(e -> {
                                        log.error("❌ Error al agregar sucursal a la franquicia {}: {}", franchiseId,
                                                        e.getMessage());
                                        return ServerResponse.badRequest()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .bodyValue(Map.of("error", e.getMessage()));
                                });
        }

        /**
         * PUT /branches/{branchId}/name
         * Actualiza el nombre de una sucursal
         */
        public Mono<ServerResponse> updateBranchName(ServerRequest request) {
                String branchId = request.pathVariable("branchId");

                return request.bodyToMono(Branch.class)
                                .flatMap(body -> {
                                        if (body.getName() == null || body.getName().isBlank()) {
                                                return ServerResponse.badRequest()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(Map.of("error",
                                                                                "El nombre de la sucursal no puede estar vacío"));
                                        }
                                        return updateBranchNameUseCase.updateBranchName(branchId, body.getName())
                                                        .flatMap(updated -> ServerResponse.ok()
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .bodyValue(updated));
                                })
                                .switchIfEmpty(ServerResponse.badRequest()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(Map.of("error", "El cuerpo de la solicitud está vacío")))
                                .onErrorResume(e -> {
                                        log.error("❌ Error al actualizar nombre de sucursal: {}", e.getMessage());
                                        return ServerResponse.badRequest()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .bodyValue(Map.of("error", e.getMessage()));
                                });
        }
}