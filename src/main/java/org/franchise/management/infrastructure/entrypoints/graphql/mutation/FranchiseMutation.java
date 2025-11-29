package org.franchise.management.infrastructure.entrypoints.graphql.mutation;

import lombok.RequiredArgsConstructor;
import org.franchise.management.application.usecase.CreateFranchiseUseCase;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.infrastructure.entrypoints.graphql.dto.FranchiseInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class FranchiseMutation {

    private final CreateFranchiseUseCase createFranchiseUseCase;

    @MutationMapping
    public Mono<Franchise> createFranchise(
            @Argument("input") FranchiseInput input
    ) {
        Franchise franchise = Franchise.builder()
                .name(input.getName())
                .build();

        return createFranchiseUseCase.createFranchise(franchise);
    }
}
