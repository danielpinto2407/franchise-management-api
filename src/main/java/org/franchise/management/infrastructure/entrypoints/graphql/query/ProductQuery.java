package org.franchise.management.infrastructure.entrypoints.graphql.query;

import lombok.RequiredArgsConstructor;
import org.franchise.management.application.usecase.GetMaxStockProductByBranchUseCase;
import org.franchise.management.domain.model.Product;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class ProductQuery {

    private final GetMaxStockProductByBranchUseCase getMaxStockProductByBranchUseCase;

    @QueryMapping
    public Flux<Product> maxStockProducts(
            @Argument String franchiseId
    ) {
        return getMaxStockProductByBranchUseCase.getMaxStockProducts(franchiseId);
    }
}
