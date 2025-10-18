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

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;

    /** POST /franchises */
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::createFranchise)
                .flatMap(this::okResponse)
                .switchIfEmpty(handleEmptyBody())
                .onErrorResume(e -> handleError("crear franquicia", e));
    }

    private Mono<ServerResponse> okResponse(Object body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

    private Mono<ServerResponse> badRequest(String message) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("error", message));
    }

    private Mono<ServerResponse> handleEmptyBody() {
        return badRequest("El cuerpo de la solicitud está vacío");
    }

    private Mono<ServerResponse> handleError(String action, Throwable e) {
        log.error("❌ Error al {}: {}", action, e.getMessage(), e);
        return badRequest(e.getMessage());
    }
}
