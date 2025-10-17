package org.franchise.management.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Franchise (Franquicia) - Entidad de dominio que representa una franquicia
 * con múltiples sucursales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchises")
public class Franchise {
    @Id
    private String id;

    @NotBlank(message = "Franchise name is required")
    private String name;

    @Builder.Default
    private List<String> branchIds = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void addBranch(String branchId) {
        if (branchId == null || branchId.isBlank()) {
            throw new IllegalArgumentException("Branch ID cannot be empty");
        }
        if (this.branchIds == null) {
            this.branchIds = new ArrayList<>();
        }
        if (!this.branchIds.contains(branchId)) {
            this.branchIds.add(branchId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeBranch(String branchId) {
        if (this.branchIds != null) {
            this.branchIds.remove(branchId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Franchise name cannot be empty");
        }
        this.name = newName.trim();
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalBranches() {
        return branchIds != null ? branchIds.size() : 0;
    }
}
