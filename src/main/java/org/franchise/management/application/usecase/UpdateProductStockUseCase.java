package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class UpdateProductStockUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> updateStock(String franchiseId, String branchId, String productId, Integer newStock) {
        return productRepository.updateProductStock(franchiseId, branchId, productId, newStock)
                .doOnNext(p -> log.info("🔄 Stock actualizado: " + p.getName() + " = " + p.getStock()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado.")))
                .onErrorResume(e -> {
                    log.error("❌ Error al actualizar stock: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}