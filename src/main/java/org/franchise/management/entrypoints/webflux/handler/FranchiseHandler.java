package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.CreateFranchiseUseCase;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.entrypoints.webflux.util.ResponseUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;

    /** POST /franchises */
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(Franchise.class)
                .flatMap(createFranchiseUseCase::createFranchise)
                .flatMap(ResponseUtil::ok)
                .switchIfEmpty(ResponseUtil.emptyBody())
                .onErrorResume(e -> ResponseUtil.handleError("crear franquicia", e));
    }
}
