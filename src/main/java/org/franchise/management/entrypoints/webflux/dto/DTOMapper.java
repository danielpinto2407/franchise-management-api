package org.franchise.management.entrypoints.webflux.dto;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.model.Franchise;
import org.franchise.management.domain.model.Product;

/**
 * Mapper para convertir entre DTOs y modelos de dominio
 */
public class DTOMapper {

    public static Franchise toFranchise(FranchiseRequestDTO dto) {
        return Franchise.builder()
                .name(dto.getName())
                .build();
    }

    public static Branch toBranch(BranchRequestDTO dto) {
        return Branch.builder()
                .name(dto.getName())
                .build();
    }

    public static Product toProduct(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .stock(dto.getStock())
                .build();
    }

    public static Product updateStockToProduct(UpdateStockRequestDTO dto) {
        return Product.builder()
                .stock(dto.getStock())
                .build();
    }

    public static Product updateNameRequestToProduct(UpdateNameRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .build();
    }

}