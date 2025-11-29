package org.franchise.management.infrastructure.entrypoints.graphql.resolver;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.FranchiseMongoAdapter;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class BranchFieldResolver {

    private final FranchiseMongoAdapter franchiseRepository;

    @SchemaMapping(typeName = "Branch", field = "franchise")
    public Mono<Franchise> resolveFranchise(Branch branch) {
        return franchiseRepository.findById(branch.getFranchiseId());
    }
}

