package org.franchise.management.infrastructure.entrypoints.webflux.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseRequestDTO {

    @NotBlank(message = "El nombre de la franquicia es obligatorio")
    private String name;
}
