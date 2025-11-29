package org.franchise.management.infrastructure.entrypoints.graphql.mutation;

import lombok.RequiredArgsConstructor;
import org.franchise.management.application.usecase.*;
import org.franchise.management.domain.model.Product;
import org.franchise.management.infrastructure.entrypoints.graphql.dto.ProductInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ProductMutation {

    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;

    @MutationMapping
    public Mono<Product> addProduct(
            @Argument("input") ProductInput input
    ) {
        Product product = Product.builder()
                .name(input.getName())
                .stock(input.getStock())
                .branchId(input.getBranchId())
                .build();

        return addProductToBranchUseCase.addProduct(input.getBranchId(), product);
    }

    @MutationMapping
    public Mono<Boolean> deleteProduct(
            @Argument String branchId,
            @Argument String productId
    ) {
        return deleteProductFromBranchUseCase.deleteProduct(branchId, productId)
                .thenReturn(true);
    }

    @MutationMapping
    public Mono<Product> updateProductStock(
            @Argument String productId,
            @Argument Integer stock
    ) {
        return updateProductStockUseCase.updateStock(productId, stock);
    }

    @MutationMapping
    public Mono<Product> updateProductName(
            @Argument String productId,
            @Argument String name
    ) {
        return updateProductNameUseCase.updateProductName(productId, name);
    }
}
