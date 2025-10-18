package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.AddBranchToFranchiseUseCase;
import org.franchise.management.application.usecase.UpdateBranchNameUseCase;
import org.franchise.management.domain.model.Branch;
import org.franchise.management.entrypoints.webflux.util.ResponseUtil;
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

        /** POST /franchises/{franchiseId}/branches */
        public Mono<ServerResponse> addBranch(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");

                return request.bodyToMono(Branch.class)
                                .flatMap(this::validateBranchName)
                                .flatMap(branch -> addBranchToFranchiseUseCase.addBranch(franchiseId, branch))
                                .flatMap(ResponseUtil::ok)
                                .switchIfEmpty(ResponseUtil.emptyBody())
                                .onErrorResume(e -> ResponseUtil.handleError("agregar sucursal", e));
        }

        /** PUT /branches/{branchId}/name */
        public Mono<ServerResponse> updateBranchName(ServerRequest request) {
                String branchId = request.pathVariable("branchId");

                return request.bodyToMono(Branch.class)
                                .flatMap(this::validateBranchName)
                                .flatMap(branch -> updateBranchNameUseCase.updateBranchName(branchId, branch.getName()))
                                .flatMap(ResponseUtil::ok)
                                .switchIfEmpty(ResponseUtil.emptyBody())
                                .onErrorResume(e -> ResponseUtil.handleError("actualizar nombre de sucursal", e));
        }

        private Mono<Branch> validateBranchName(Branch branch) {
                if (branch.getName() == null || branch.getName().isBlank()) {
                        return Mono.error(
                                        new IllegalArgumentException("El nombre de la sucursal no puede estar vacío"));
                }
                return Mono.just(branch);
        }
}
