package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.CreateFranchiseUseCase;
import org.franchise.management.domain.model.Franchise;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;

    /**
     * Endpoint: POST /franchises
     */
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::createFranchise)
                .flatMap(savedFranchise -> {
                    log.info("✅ Franquicia creada exitosamente: {}", savedFranchise.getName());
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(savedFranchise);
                })
                .onErrorResume(e -> {
                    log.error("❌ Error al crear franquicia: {}", e.getMessage());
                    return ServerResponse.badRequest()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(
                                    String.format("{\"error\": \"%s\"}", e.getMessage()));
                });
    }
}