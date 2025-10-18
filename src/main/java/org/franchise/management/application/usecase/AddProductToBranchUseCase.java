package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.ProductMongoAdapter;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class AddProductToBranchUseCase {

    private final ProductMongoAdapter productRepository;

    public Mono<Product> addProduct(String franchiseId, String branchId, Product product) {
        return productRepository.addProductToBranch(franchiseId, branchId, product)
                .doOnNext(p -> log.info("Producto agregado: " + p.getName()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia o sucursal no encontradas.")))
                .onErrorResume(e -> {
                    log.error("Error al agregar producto: " + e.getMessage());
                    return Mono.error(e);
                });
    }
}
