package org.franchise.management.infrastructure.entrypoints.graphql.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseInput {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
}
