package org.franchise.management.infrastructure.entrypoints.graphql.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductInput {

    private String name;
    private Integer stock;
}
