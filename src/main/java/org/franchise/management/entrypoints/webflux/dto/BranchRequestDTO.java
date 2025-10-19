package org.franchise.management.entrypoints.webflux.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequestDTO {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String name;
}