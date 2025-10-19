package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Product;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.ProductMongoAdapter;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Log4j2
public class GetMaxStockProductByBranchUseCase {

    private final ProductMongoAdapter productRepository;

    public Flux<Product> getMaxStockProducts(String franchiseId) {
        return productRepository.findMaxStockProductByBranch(franchiseId)
                .doOnNext(p -> log.info("Producto con mayor stock: " + p.getName() + " (" + p.getStock() + ")"))
                .onErrorResume(e -> {
                    log.error("Error al obtener productos con mayor stock: " + e.getMessage());
                    return Flux.error(e);
                });
    }
}
