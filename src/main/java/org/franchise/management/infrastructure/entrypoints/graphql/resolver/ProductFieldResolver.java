package org.franchise.management.infrastructure.entrypoints.graphql.resolver;

import lombok.RequiredArgsConstructor;
import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.BranchRepository;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ProductFieldResolver {

    private final BranchRepository branchRepository;

    @SchemaMapping(typeName = "Product", field = "branch")
    public Mono<Branch> resolveBranch(Product product) {
        return branchRepository.findById(product.getBranchId());
    }
}
