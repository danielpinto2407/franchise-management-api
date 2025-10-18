package org.franchise.management.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateProductNameUseCase {

    private final ProductRepository productRepository;

    public Mono<Product> updateProductName(String productId, String newName) {
        return productRepository.updateProductName(productId, newName)
                .doOnNext(p -> log.info("✅ Nombre actualizado para producto {}: {}", productId, newName))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .onErrorResume(e -> {
                    log.error("❌ Error al actualizar nombre del producto {}: {}", productId, e.getMessage());
                    return Mono.error(e);
                });
    }
}
