package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.BranchMongoRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.ProductMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductMongoAdapter implements ProductRepository {

    private final ProductMongoRepository productRepo;
    private final BranchMongoRepository branchRepo;

    @Override
    public Mono<Product> addProductToBranch(String franchiseId, String branchId, Product product) {
        log.info("Adding product {} to branch {} of franchise {}", product.getName(), branchId, franchiseId);
        product.setBranchId(branchId);
        return productRepo.save(product)
                .flatMap(savedProduct -> branchRepo.findById(branchId)
                        .flatMap(branch -> {
                            branch.addProduct(savedProduct.getId());
                            return branchRepo.save(branch).thenReturn(savedProduct);
                        }));
    }

    @Override
    public Mono<Void> deleteProductFromBranch(String franchiseId, String branchId, String productId) {
        log.info("Deleting product {} from branch {} of franchise {}", productId, branchId, franchiseId);
        return branchRepo.findById(branchId)
                .flatMap(branch -> {
                    branch.removeProduct(productId);
                    return branchRepo.save(branch)
                            .then(productRepo.deleteById(productId));
                });
    }

    @Override
    public Mono<Product> updateProductStock(String franchiseId, String branchId, String productId, Integer newStock) {
        log.info("Updating stock for product {} in branch {} of franchise {} to {}", productId, branchId, franchiseId,
                newStock);
        return productRepo.findById(productId)
                .flatMap(product -> {
                    product.setStock(newStock);
                    return productRepo.save(product);
                });
    }

    // Recupera todas las sucursales del franchise y para cada una busca el producto
    // con mayor stock
    @Override
    public Flux<Product> findMaxStockProductByBranch(String franchiseId) {
        log.info("Finding max stock product for franchise {}", franchiseId);
        return branchRepo.findAll()
                .filter(branch -> branch.getFranchiseId().equals(franchiseId))
                .flatMap(branch -> productRepo.findByBranchId(branch.getId())
                        .sort((p1, p2) -> p2.getStock().compareTo(p1.getStock()))
                        .next());
    }
}