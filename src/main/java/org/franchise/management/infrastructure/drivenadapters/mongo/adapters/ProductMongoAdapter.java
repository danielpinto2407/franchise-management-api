package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.ProductMongoRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductMongoAdapter implements ProductRepository {

    private final ProductMongoRepository productMongoRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Product> addProductToBranch(String franchiseId, String branchId, Product product) {

        Query franchiseQuery = new Query(Criteria.where("_id").is(franchiseId)
                .and("branchIds").in(branchId));

        return mongoTemplate.exists(franchiseQuery, "franchises")
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.empty();
                    }

                    product.setBranchId(branchId);
                    return mongoTemplate.save(product);
                })
                .flatMap(savedProduct -> {
                    Query branchQuery = new Query(Criteria.where("_id").is(branchId));
                    Update update = new Update().addToSet("productIds", savedProduct.getId());

                    return mongoTemplate.updateFirst(branchQuery, update, "branches")
                            .thenReturn(savedProduct);
                });
    }

    @Override
    public Mono<Void> deleteProductFromBranch(String franchiseId, String branchId, String productId) {
        return productMongoRepository.deleteById(productId)
                .doOnSuccess(v -> log.info("Producto eliminado: {}", productId));
    }

    @Override
    public Mono<Product> updateProductStock(String productId, Integer newStock) {
        return productMongoRepository.findById(productId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")))
                .flatMap(product -> {
                    product.updateStock(newStock);
                    return productMongoRepository.save(product);
                })
                .doOnNext(p -> log.info("Stock actualizado: {} → {}", p.getName(), p.getStock()));
    }

    @Override
    public Flux<Product> findMaxStockProductByBranch(String franchiseId) {
        return productMongoRepository.findAll()
                .filter(p -> p.getBranchId() != null)
                .groupBy(Product::getBranchId)
                .flatMap(group -> group
                        .sort((p1, p2) -> p2.getStock().compareTo(p1.getStock()))
                        .next())
                .doOnComplete(() -> log.info("Consulta de productos con mayor stock completada"));
    }

    @Override
    public Mono<Product> updateProductName(String productId, String newName) {
        return productMongoRepository.findById(productId)
                .flatMap(product -> {
                    product.updateName(newName);
                    return productMongoRepository.save(product);
                })
                .doOnNext(p -> log.info("Nombre de producto actualizado: {} → {}", productId, newName))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado")));
    }

}