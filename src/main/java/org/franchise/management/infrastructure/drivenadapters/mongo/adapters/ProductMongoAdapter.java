package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.BranchMongoRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.ProductMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductMongoAdapter implements ProductRepository {

    private final ProductMongoRepository productMongoRepository;
    private final BranchMongoRepository branchMongoRepository;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Product> addProductToBranch(String branchId, Product product) {

        Query branchExistsQuery = new Query(Criteria.where("_id").is(branchId));

        return mongoTemplate.exists(branchExistsQuery, "branches")
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
    public Mono<Void> deleteProductFromBranch(String branchId, String productId) {
        return validateBranchAndProduct(branchId, productId)
                .flatMap(tuple -> {
                    var branch = tuple.getT1();
                    var product = tuple.getT2();

                    branch.getProductIds().remove(productId);
                    branch.setUpdatedAt(LocalDateTime.now());

                    return productMongoRepository.delete(product)
                            .then(branchMongoRepository.save(branch))
                            .then();
                })
                .doOnSuccess(v -> log.info("Producto {} eliminado de branch {}", productId, branchId))
                .doOnError(e -> log.error("Error al eliminar producto {} de branch {}: {}", productId, branchId,
                        e.getMessage()));
    }

    private Mono<Tuple2<Branch, Product>> validateBranchAndProduct(String branchId, String productId) {
        return branchMongoRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Branch no encontrado: " + branchId)))
                .zipWhen(branch -> productMongoRepository.findById(productId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Producto no encontrado: " + productId)))
                        .flatMap(product -> {
                            if (!branchId.equals(product.getBranchId())) {
                                return Mono.error(new IllegalArgumentException(
                                        "El producto no pertenece al branch especificado"));
                            }
                            return Mono.just(product);
                        }));
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
        Query franchiseQuery = Query.query(Criteria.where("_id").is(franchiseId));

        return mongoTemplate.findOne(franchiseQuery, Franchise.class, "franchises")
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Franquicia no encontrada")))
                .flatMapMany(franchise -> {
                    if (franchise.getBranchIds() == null || franchise.getBranchIds().isEmpty()) {
                        log.warn("Franquicia {} no tiene sucursales", franchiseId);
                    }

                    return Flux.fromIterable(franchise.getBranchIds())
                            .flatMap(branchId -> mongoTemplate.find(
                                    Query.query(Criteria.where("branchId").is(branchId))
                                            .with(Sort.by(Sort.Direction.DESC, "stock"))
                                            .limit(1),
                                    Product.class));
                })
                .doOnNext(p -> log.info("Max stock product: {} (stock: {}, branch: {})",
                        p.getName(), p.getStock(), p.getBranchId()))
                .doOnComplete(() -> log.info("Query completed for franchise {}", franchiseId));
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