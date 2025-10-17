package org.franchise.management.application.usecase;

import org.franchise.management.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeleteProductFromBranchUseCase {

    private final ProductRepository productRepository;

    public Mono<Void> deleteProduct(String franchiseId, String branchId, String productId) {
        return productRepository.deleteProductFromBranch(franchiseId, branchId, productId)
                .doOnSuccess(v -> log.info("🗑️ Producto eliminado: " + productId))
                .onErrorResume(e -> {
                    log.error("❌ Error al eliminar producto: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}
