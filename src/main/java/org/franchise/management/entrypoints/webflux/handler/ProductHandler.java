package org.franchise.management.entrypoints.webflux.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.franchise.management.application.usecase.*;
import org.franchise.management.domain.model.Product;
import org.franchise.management.entrypoints.webflux.dto.DTOMapper;
import org.franchise.management.entrypoints.webflux.dto.ProductRequestDTO;
import org.franchise.management.entrypoints.webflux.dto.UpdateNameRequestDTO;
import org.franchise.management.entrypoints.webflux.dto.UpdateStockRequestDTO;
import org.franchise.management.entrypoints.webflux.util.ResponseUtil;
import org.franchise.management.entrypoints.webflux.util.ValidationUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
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
        private final ValidationUtil validationUtil;

        /** POST /franchises/{franchiseId}/branches/{branchId}/products */
        public Mono<ServerResponse> addProduct(ServerRequest request) {
                String branchId = request.pathVariable("branchId");

                return request.bodyToMono(ProductRequestDTO.class)
                                .flatMap(validationUtil::validate)
                                .map(DTOMapper::toProduct)
                                .flatMap(product -> addProductToBranchUseCase.addProduct(branchId,
                                                product))
                                .flatMap(ResponseUtil::ok)
                                .switchIfEmpty(ResponseUtil.emptyBody())
                                .onErrorResume(e -> ResponseUtil.handleError("agregar producto", e));
        }

        /** DELETE /franchises/{franchiseId}/branches/{branchId}/products/{productId} */
        public Mono<ServerResponse> deleteProduct(ServerRequest request) {
                String branchId = request.pathVariable("branchId");
                String productId = request.pathVariable("productId");

                return deleteProductFromBranchUseCase.deleteProduct(branchId, productId)
                                .then(ServerResponse.noContent().build())
                                .onErrorResume(e -> ResponseUtil.handleError("eliminar producto", e));
        }

        /**
         * PUT /franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock
         */
        public Mono<ServerResponse> updateStock(ServerRequest request) {
                String productId = request.pathVariable("productId");

                return request.bodyToMono(UpdateStockRequestDTO.class)
                                .flatMap(validationUtil::validate)
                                .map(DTOMapper::updateStockToProduct)
                                .flatMap(body -> updateProductStockUseCase.updateStock(productId,
                                                body.getStock()))
                                .flatMap(ResponseUtil::ok)
                                .switchIfEmpty(ResponseUtil.emptyBody())
                                .onErrorResume(e -> ResponseUtil.handleError("actualizar stock", e));
        }

        /** GET /franchises/{franchiseId}/max-stock */
        public Mono<ServerResponse> getMaxStockProducts(ServerRequest request) {
                String franchiseId = request.pathVariable("franchiseId");

                return findMaxStockProductByFranchiseUseCase.getMaxStockProducts(franchiseId)
                                .collectList()
                                .flatMap(ResponseUtil::ok)
                                .onErrorResume(e -> ResponseUtil.handleError("obtener productos con mayor stock", e));
        }

        /** PUT /franchises/{franchiseId}/products/{productId}/name */
        public Mono<ServerResponse> updateProductName(ServerRequest request) {
                String productId = request.pathVariable("productId");

                return request.bodyToMono(UpdateNameRequestDTO.class)
                                .flatMap(validationUtil::validate)
                                .map(DTOMapper::updateNameRequestToProduct)
                                .flatMap(body -> updateProductNameUseCase.updateProductName(productId, body.getName()))
                                .flatMap(ResponseUtil::ok)
                                .switchIfEmpty(ResponseUtil.emptyBody())
                                .onErrorResume(e -> ResponseUtil.handleError("actualizar nombre de producto", e));
        }
}
