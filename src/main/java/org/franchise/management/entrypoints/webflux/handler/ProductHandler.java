package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import org.franchise.management.application.usecase.*;
import org.franchise.management.domain.model.Product;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductHandler {

        private final AddProductToBranchUseCase addProductToBranchUseCase;
        private final UpdateProductNameUseCase updateProductNameUseCase;
        private final DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;
        private final UpdateProductStockUseCase updateProductStockUseCase;
        private final GetMaxStockProductByBranchUseCase findMaxStockProductByFranchiseUseCase;

        /**
         * POST /franchises/{franchiseId}/branches/{branchId}/products
         * Agrega un nuevo producto a una sucursal
         */
        public Mono<ServerResponse> addProduct(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");
                String branchId = request.pathVariable("branchId");

                return request.bodyToMono(Product.class)
                                .flatMap(product -> addProductToBranchUseCase.addProduct(franchiseId, branchId,
                                                product))
                                .flatMap(saved -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(saved))
                                .onErrorResume(e -> handleError("agregar producto", e))
                                .switchIfEmpty(ServerResponse.badRequest().bodyValue("Request body is empty"));
        }

        /**
         * DELETE /franchises/{franchiseId}/branches/{branchId}/products/{productId}
         * Elimina un producto de una sucursal
         */
        public Mono<ServerResponse> deleteProduct(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");
                String branchId = request.pathVariable("branchId");
                String productId = request.pathVariable("productId");

                return deleteProductFromBranchUseCase.deleteProduct(franchiseId, branchId, productId)
                                .then(ServerResponse.noContent().build())
                                .onErrorResume(e -> handleError("eliminar producto", e));
        }

        /**
         * PUT /franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock
         * Modifica el stock de un producto
         */
        public Mono<ServerResponse> updateStock(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");
                String branchId = request.pathVariable("branchId");
                String productId = request.pathVariable("productId");

                return request.bodyToMono(Product.class)
                                .flatMap(body -> updateProductStockUseCase.updateStock(franchiseId, branchId, productId,
                                                body.getStock()))
                                .flatMap(updated -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(updated))
                                .onErrorResume(e -> handleError("actualizar stock", e));
        }

        /**
         * GET /franchises/{franchiseId}/max-stock
         * Muestra el producto con más stock por sucursal
         */
        public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");

                return findMaxStockProductByFranchiseUseCase.getMaxStockProducts(franchiseId)
                                .collectList()
                                .flatMap(products -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(products))
                                .onErrorResume(e -> handleError("obtener productos con mayor stock", e));
        }

        private Mono<ServerResponse> handleError(String action, Throwable e) {
                log.error("❌ Error al {}: {}", action, e.getMessage());
                return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("{\"error\": \"" + e.getMessage() + "\"}");
        }

        public Mono<ServerResponse> updateProductName(ServerRequest request) {
                String productId = request.pathVariable("productId");

                return request.bodyToMono(Product.class)
                                .flatMap(body -> {
                                        if (body.getName() == null || body.getName().isBlank()) {
                                                return ServerResponse.badRequest()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(Map.of("error",
                                                                                "El nombre del producto es requerido"));
                                        }
                                        return updateProductNameUseCase.updateProductName(productId, body.getName())
                                                        .flatMap(updated -> ServerResponse.ok()
                                                                        .contentType(MediaType.APPLICATION_JSON)
                                                                        .bodyValue(updated));
                                })
                                .switchIfEmpty(ServerResponse.badRequest()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(Map.of("error", "El cuerpo de la solicitud está vacío")))
                                .onErrorResume(e -> {
                                        log.error("❌ Error al actualizar nombre de producto: {}", e.getMessage());
                                        return ServerResponse.badRequest()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .bodyValue(Map.of("error", e.getMessage()));
                                });
        }

}
