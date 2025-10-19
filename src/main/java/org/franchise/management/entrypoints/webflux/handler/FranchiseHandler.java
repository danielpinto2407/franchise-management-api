package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.CreateFranchiseUseCase;
import org.franchise.management.entrypoints.webflux.dto.DTOMapper;
import org.franchise.management.entrypoints.webflux.dto.FranchiseRequestDTO;
import org.franchise.management.entrypoints.webflux.util.ResponseUtil;
import org.franchise.management.entrypoints.webflux.util.ValidationUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final ValidationUtil validationUtil;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequestDTO.class)
                .flatMap(dto -> validationUtil.validate(dto)
                        .map(DTOMapper::toFranchise)
                        .flatMap(createFranchiseUseCase::createFranchise)
                        .flatMap(savedFranchise -> {
                            log.info("Franquicia creada exitosamente: {}", savedFranchise.getName());
                            return ResponseUtil.ok(savedFranchise);
                        }))
                .switchIfEmpty(ResponseUtil.emptyBody())
                .onErrorResume(e -> ResponseUtil.handleError("crear franquicia", e));
    }

}