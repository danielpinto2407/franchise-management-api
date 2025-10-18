package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.*;
import org.franchise.management.domain.model.Product;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProductHandler {

        private final AddProductToBranchUseCase addProductToBranchUseCase;
        private final UpdateProductNameUseCase updateProductNameUseCase;
        private final DeleteProductFromBranchUseCase deleteProductFromBranchUseCase;
        private final UpdateProductStockUseCase updateProductStockUseCase;
        private final GetMaxStockProductByBranchUseCase findMaxStockProductByFranchiseUseCase;

        /** POST /franchises/{franchiseId}/branches/{branchId}/products */
        public Mono<ServerResponse> addProduct(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");
                String branchId = request.pathVariable("branchId");

                return request.bodyToMono(Product.class)
                                .flatMap(product -> addProductToBranchUseCase.addProduct(franchiseId, branchId,
                                                product))
                                .flatMap(this::okResponse)
                                .switchIfEmpty(handleEmptyBody())
                                .onErrorResume(e -> handleError("agregar producto", e));
        }

        /** DELETE /franchises/{franchiseId}/branches/{branchId}/products/{productId} */
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
         */
        public Mono<ServerResponse> updateStock(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");
                String branchId = request.pathVariable("branchId");
                String productId = request.pathVariable("productId");

                return request.bodyToMono(Product.class)
                                .flatMap(body -> updateProductStockUseCase.updateStock(franchiseId, branchId, productId,
                                                body.getStock()))
                                .flatMap(this::okResponse)
                                .switchIfEmpty(handleEmptyBody())
                                .onErrorResume(e -> handleError("actualizar stock", e));
        }

        /** GET /franchises/{franchiseId}/max-stock */
        public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");

                return findMaxStockProductByFranchiseUseCase.getMaxStockProducts(franchiseId)
                                .collectList()
                                .flatMap(this::okResponse)
                                .onErrorResume(e -> handleError("obtener productos con mayor stock", e));
        }

        /** PUT /franchises/{franchiseId}/products/{productId}/name */
        public Mono<ServerResponse> updateProductName(ServerRequest request) {
                String productId = request.pathVariable("productId");

                return request.bodyToMono(Product.class)
                                .flatMap(body -> {
                                        if (body.getName() == null || body.getName().isBlank()) {
                                                return badRequest("El nombre del producto es requerido");
                                        }
                                        return updateProductNameUseCase.updateProductName(productId, body.getName())
                                                        .flatMap(this::okResponse);
                                })
                                .switchIfEmpty(handleEmptyBody())
                                .onErrorResume(e -> handleError("actualizar nombre de producto", e));
        }

        private Mono<ServerResponse> okResponse(Object body) {
                return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(body);
        }

        private Mono<ServerResponse> badRequest(String message) {
                return ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", message));
        }

        private Mono<ServerResponse> handleEmptyBody() {
                return badRequest("El cuerpo de la solicitud está vacío");
        }

        private Mono<ServerResponse> handleError(String action, Throwable e) {
                log.error("❌ Error al {}: {}", action, e.getMessage(), e);
                return badRequest(e.getMessage());
        }
}
