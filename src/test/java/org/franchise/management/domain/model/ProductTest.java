package org.franchise.management.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Product
 */
@DisplayName("Product Domain Model Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("prod123")
                .name("Coca Cola")
                .stock(100)
                .branchId("branch456")
                .build();
    }

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProduct() {
        assertNotNull(product);
        assertEquals("prod123", product.getId());
        assertEquals("Coca Cola", product.getName());
        assertEquals(100, product.getStock());
        assertEquals("branch456", product.getBranchId());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create product using builder")
    void shouldCreateProductUsingBuilder() {
        Product newProduct = Product.builder()
                .name("Pepsi")
                .stock(50)
                .branchId("branch789")
                .build();

        assertNotNull(newProduct);
        assertEquals("Pepsi", newProduct.getName());
        assertEquals(50, newProduct.getStock());
    }

    @Test
    @DisplayName("Should update stock successfully")
    void shouldUpdateStock() {
        product.updateStock(150);

        assertEquals(150, product.getStock());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update stock to zero")
    void shouldUpdateStockToZero() {
        product.updateStock(0);

        assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("Should throw exception when updating stock with negative value")
    void shouldThrowExceptionWhenStockIsNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.updateStock(-10));

        assertEquals("Stock cannot be negative", exception.getMessage());
        assertEquals(100, product.getStock()); // Stock no debe cambiar
    }

    @Test
    @DisplayName("Should update product name successfully")
    void shouldUpdateName() {
        String oldUpdatedAt = product.getUpdatedAt().toString();

        try {
            Thread.sleep(10); // Pequeña pausa para que cambie el timestamp
        } catch (InterruptedException e) {
            // Ignorar
        }

        product.updateName("Pepsi");

        assertEquals("Pepsi", product.getName());
        assertNotNull(product.getUpdatedAt());
    }

    @Test
    @DisplayName("Should trim whitespace when updating name")
    void shouldTrimWhitespaceWhenUpdatingName() {
        product.updateName("   Sprite   ");

        assertEquals("Sprite", product.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating name with null")
    void shouldThrowExceptionWhenUpdatingNameWithNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.updateName(null));

        assertEquals("Product name cannot be empty", exception.getMessage());
        assertEquals("Coca Cola", product.getName()); // Nombre no debe cambiar
    }

    @Test
    @DisplayName("Should throw exception when updating name with empty string")
    void shouldThrowExceptionWhenUpdatingNameWithEmptyString() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.updateName("   "));

        assertEquals("Product name cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should have default timestamps when created")
    void shouldHaveDefaultTimestamps() {
        Product newProduct = Product.builder()
                .name("Fanta")
                .stock(25)
                .branchId("branch999")
                .build();

        assertNotNull(newProduct.getCreatedAt());
        assertNotNull(newProduct.getUpdatedAt());
    }
}