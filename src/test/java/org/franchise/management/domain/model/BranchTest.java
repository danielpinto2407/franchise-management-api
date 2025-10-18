package org.franchise.management.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Branch
 */
@DisplayName("Branch Domain Model Tests")
class BranchTest {

    private Branch branch;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id("branch123")
                .name("Sucursal Centro")
                .franchiseId("franchise456")
                .build();
    }

    @Test
    @DisplayName("Should create branch with valid data")
    void shouldCreateBranch() {
        assertNotNull(branch);
        assertEquals("branch123", branch.getId());
        assertEquals("Sucursal Centro", branch.getName());
        assertEquals("franchise456", branch.getFranchiseId());
        assertNotNull(branch.getProductIds());
        assertTrue(branch.getProductIds().isEmpty());
        assertNotNull(branch.getCreatedAt());
        assertNotNull(branch.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create branch using builder")
    void shouldCreateBranchUsingBuilder() {
        Branch newBranch = Branch.builder()
                .name("Sucursal Norte")
                .franchiseId("franchise789")
                .build();

        assertNotNull(newBranch);
        assertEquals("Sucursal Norte", newBranch.getName());
        assertNotNull(newBranch.getProductIds());
        assertEquals(0, newBranch.getProductIds().size());
    }

    @Test
    @DisplayName("Should add product to branch")
    void shouldAddProduct() {
        branch.addProduct("product123");

        assertEquals(1, branch.getProductIds().size());
        assertTrue(branch.getProductIds().contains("product123"));
    }

    @Test
    @DisplayName("Should add multiple products to branch")
    void shouldAddMultipleProducts() {
        branch.addProduct("product1");
        branch.addProduct("product2");
        branch.addProduct("product3");

        assertEquals(3, branch.getProductIds().size());
        assertTrue(branch.getProductIds().contains("product1"));
        assertTrue(branch.getProductIds().contains("product2"));
        assertTrue(branch.getProductIds().contains("product3"));
    }

    @Test
    @DisplayName("Should not add duplicate product")
    void shouldNotAddDuplicateProduct() {
        branch.addProduct("product123");
        branch.addProduct("product123"); // Intentar agregar duplicado

        assertEquals(1, branch.getProductIds().size());
    }

    @Test
    @DisplayName("Should throw exception when adding null product ID")
    void shouldThrowExceptionWhenAddingNullProductId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> branch.addProduct(null));

        assertEquals("Product ID cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when adding empty product ID")
    void shouldThrowExceptionWhenAddingEmptyProductId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> branch.addProduct("   "));

        assertEquals("Product ID cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should remove product from branch")
    void shouldRemoveProduct() {
        branch.addProduct("product123");
        branch.addProduct("product456");

        branch.removeProduct("product123");

        assertEquals(1, branch.getProductIds().size());
        assertFalse(branch.getProductIds().contains("product123"));
        assertTrue(branch.getProductIds().contains("product456"));
    }

    @Test
    @DisplayName("Should handle removing non-existent product")
    void shouldHandleRemovingNonExistentProduct() {
        branch.addProduct("product123");

        branch.removeProduct("product999"); // No existe

        assertEquals(1, branch.getProductIds().size());
        assertTrue(branch.getProductIds().contains("product123"));
    }

    @Test
    @DisplayName("Should update branch name successfully")
    void shouldUpdateName() {
        branch.updateName("Sucursal Sur");

        assertEquals("Sucursal Sur", branch.getName());
        assertNotNull(branch.getUpdatedAt());
    }

    @Test
    @DisplayName("Should trim whitespace when updating name")
    void shouldTrimWhitespaceWhenUpdatingName() {
        branch.updateName("   Sucursal Este   ");

        assertEquals("Sucursal Este", branch.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating name with null")
    void shouldThrowExceptionWhenUpdatingNameWithNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> branch.updateName(null));

        assertEquals("Branch name cannot be empty", exception.getMessage());
        assertEquals("Sucursal Centro", branch.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating name with empty string")
    void shouldThrowExceptionWhenUpdatingNameWithEmptyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> branch.updateName("   "));

        assertEquals("Branch name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should initialize productIds list when null")
    void shouldInitializeProductIdsListWhenNull() {
        Branch newBranch = Branch.builder()
                .name("Test Branch")
                .franchiseId("franchise999")
                .productIds(null)
                .build();

        newBranch.addProduct("product123");

        assertNotNull(newBranch.getProductIds());
        assertEquals(1, newBranch.getProductIds().size());
    }
}