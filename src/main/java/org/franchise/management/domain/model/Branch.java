package org.franchise.management.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "branches")
public class Branch {

    @Id
    private String id;

    @NotBlank(message = "Branch name is required")
    private String name;

    @NotBlank(message = "Franchise ID is required")
    private String franchiseId;

    @Builder.Default
    private List<String> productIds = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addProduct(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (this.productIds == null) {
            this.productIds = new ArrayList<>();
        }
        if (!this.productIds.contains(productId)) {
            this.productIds.add(productId);
        }
    }

    public void removeProduct(String productId) {
        if (this.productIds != null) {
            this.productIds.remove(productId);
        }
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Branch name cannot be empty");
        }
        this.name = newName.trim();
    }
}
