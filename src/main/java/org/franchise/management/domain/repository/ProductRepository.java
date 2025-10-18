package org.franchise.management.domain.repository;

import org.franchise.management.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * ✅ Este repositorio cubre los endpoints para productos: agregar, eliminar,
 * actualizar stock y obtener el de mayor stock por sucursal.
 */
public interface ProductRepository {

    Mono<Product> addProductToBranch(String franchiseId, String branchId, Product product);

    Mono<Void> deleteProductFromBranch(String franchiseId, String branchId, String productId);

    Mono<Product> updateProductStock(String franchiseId, String branchId, String productId, Integer newStock);

    Flux<Product> findMaxStockProductByBranch(String franchiseId);

    Mono<Product> updateProductName(String productId, String newName);

}
