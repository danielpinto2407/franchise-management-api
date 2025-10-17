package org.franchise.management.domain.usecase;

import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Log4j2
public class GetMaxStockProductByBranchUseCase {

    private final ProductRepository productRepository;

    public Flux<Product> getMaxStockProducts(String franchiseId) {
        return productRepository.findMaxStockProductByBranch(franchiseId)
                .doOnNext(p -> log.info("📦 Producto con mayor stock: " + p.getName() + " (" + p.getStock() + ")"))
                .onErrorResume(e -> {
                    log.error("❌ Error al obtener productos con mayor stock: " + e.getMessage());
                    return Flux.error(e);
                });
    }
}
