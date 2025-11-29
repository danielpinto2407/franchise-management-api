package org.franchise.management.infrastructure.entrypoints.graphql.mutation;

import lombok.RequiredArgsConstructor;
import org.franchise.management.application.usecase.AddBranchToFranchiseUseCase;
import org.franchise.management.application.usecase.UpdateBranchNameUseCase;
import org.franchise.management.domain.model.Branch;
import org.franchise.management.infrastructure.entrypoints.graphql.dto.BranchInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class BranchMutation {

    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;

    @MutationMapping
    public Mono<Branch> addBranch(
            @Argument String franchiseId,
            @Argument("input") BranchInput input
    ) {
        Branch branch = Branch.builder()
                .name(input.getName())
                .build();

        return addBranchToFranchiseUseCase.addBranch(franchiseId, branch);
    }

    @MutationMapping
    public Mono<Branch> updateBranchName(
            @Argument String branchId,
            @Argument String name
    ) {
        return updateBranchNameUseCase.updateBranchName(branchId, name);
    }
}
