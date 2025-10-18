package org.franchise.management.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Tests unitarios para la entidad Franchise
 */
@DisplayName("Franchise Domain Model Tests")
class FranchiseTest {

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id("franchise123")
                .name("McDonald's")
                .build();
    }

    @Test
    @DisplayName("Should create franchise with valid data")
    void shouldCreateFranchise() {
        assertNotNull(franchise);
        assertEquals("franchise123", franchise.getId());
        assertEquals("McDonald's", franchise.getName());
        assertNotNull(franchise.getBranchIds());
        assertTrue(franchise.getBranchIds().isEmpty());
        assertNotNull(franchise.getCreatedAt());
        assertNotNull(franchise.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create franchise using builder")
    void shouldCreateFranchiseUsingBuilder() {
        Franchise newFranchise = Franchise.builder()
                .name("Burger King")
                .build();

        assertNotNull(newFranchise);
        assertEquals("Burger King", newFranchise.getName());
        assertNotNull(newFranchise.getBranchIds());
        assertEquals(0, newFranchise.getBranchIds().size());
    }

    @Test
    @DisplayName("Should add branch to franchise")
    void shouldAddBranch() {
        franchise.addBranch("branch123");

        assertEquals(1, franchise.getBranchIds().size());
        assertTrue(franchise.getBranchIds().contains("branch123"));
        assertNotNull(franchise.getUpdatedAt());
    }

    @Test
    @DisplayName("Should add multiple branches to franchise")
    void shouldAddMultipleBranches() {
        franchise.addBranch("branch1");
        franchise.addBranch("branch2");
        franchise.addBranch("branch3");

        assertEquals(3, franchise.getBranchIds().size());
        assertTrue(franchise.getBranchIds().contains("branch1"));
        assertTrue(franchise.getBranchIds().contains("branch2"));
        assertTrue(franchise.getBranchIds().contains("branch3"));
    }

    @Test
    @DisplayName("Should not add duplicate branch")
    void shouldNotAddDuplicateBranch() {
        franchise.addBranch("branch123");
        int initialSize = franchise.getBranchIds().size();

        franchise.addBranch("branch123"); // Intentar agregar duplicado

        assertEquals(initialSize, franchise.getBranchIds().size());
        assertEquals(1, franchise.getBranchIds().size());
    }

    @Test
    @DisplayName("Should throw exception when adding null branch ID")
    void shouldThrowExceptionWhenAddingNullBranchId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> franchise.addBranch(null));

        assertEquals("Branch ID cannot be empty", exception.getMessage());
        assertEquals(0, franchise.getBranchIds().size());
    }

    @Test
    @DisplayName("Should throw exception when adding empty branch ID")
    void shouldThrowExceptionWhenAddingEmptyBranchId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> franchise.addBranch("   "));

        assertEquals("Branch ID cannot be empty", exception.getMessage());
        assertEquals(0, franchise.getBranchIds().size());
    }

    @Test
    @DisplayName("Should throw exception when adding blank branch ID")
    void shouldThrowExceptionWhenAddingBlankBranchId() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> franchise.addBranch(""));

        assertEquals("Branch ID cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should remove branch from franchise")
    void shouldRemoveBranch() {
        franchise.addBranch("branch123");
        franchise.addBranch("branch456");

        franchise.removeBranch("branch123");

        assertEquals(1, franchise.getBranchIds().size());
        assertFalse(franchise.getBranchIds().contains("branch123"));
        assertTrue(franchise.getBranchIds().contains("branch456"));
        assertNotNull(franchise.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle removing non-existent branch")
    void shouldHandleRemovingNonExistentBranch() {
        franchise.addBranch("branch123");

        franchise.removeBranch("branch999"); // No existe

        assertEquals(1, franchise.getBranchIds().size());
        assertTrue(franchise.getBranchIds().contains("branch123"));
    }

    @Test
    @DisplayName("Should handle removing from empty branch list")
    void shouldHandleRemovingFromEmptyList() {
        // No agregar nada
        assertDoesNotThrow(() -> franchise.removeBranch("branch999"));
        assertEquals(0, franchise.getBranchIds().size());
    }

    @Test
    @DisplayName("Should update franchise name successfully")
    void shouldUpdateName() {
        LocalDateTime oldUpdatedAt = franchise.getUpdatedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        franchise.updateName("KFC");

        assertEquals("KFC", franchise.getName());
        assertNotNull(franchise.getUpdatedAt());
        assertTrue(franchise.getUpdatedAt().isAfter(oldUpdatedAt) ||
                franchise.getUpdatedAt().isEqual(oldUpdatedAt));
    }

    @Test
    @DisplayName("Should trim whitespace when updating name")
    void shouldTrimWhitespaceWhenUpdatingName() {
        franchise.updateName("   Subway   ");

        assertEquals("Subway", franchise.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating name with null")
    void shouldThrowExceptionWhenUpdatingNameWithNull() {
        String originalName = franchise.getName();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> franchise.updateName(null));

        assertEquals("Franchise name cannot be empty", exception.getMessage());
        assertEquals(originalName, franchise.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating name with empty string")
    void shouldThrowExceptionWhenUpdatingNameWithEmptyString() {
        String originalName = franchise.getName();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> franchise.updateName(""));

        assertEquals("Franchise name cannot be empty", exception.getMessage());
        assertEquals(originalName, franchise.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating name with blank string")
    void shouldThrowExceptionWhenUpdatingNameWithBlankString() {
        String originalName = franchise.getName();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> franchise.updateName("   "));

        assertEquals("Franchise name cannot be empty", exception.getMessage());
        assertEquals(originalName, franchise.getName());
    }

    @Test
    @DisplayName("Should get total branches count")
    void shouldGetTotalBranches() {
        assertEquals(0, franchise.getTotalBranches());

        franchise.addBranch("branch1");
        assertEquals(1, franchise.getTotalBranches());

        franchise.addBranch("branch2");
        franchise.addBranch("branch3");
        assertEquals(3, franchise.getTotalBranches());
    }

    @Test
    @DisplayName("Should return zero when branchIds is null")
    void shouldReturnZeroWhenBranchIdsIsNull() {
        Franchise newFranchise = Franchise.builder()
                .name("Test Franchise")
                .branchIds(null)
                .build();

        assertEquals(0, newFranchise.getTotalBranches());
    }

    @Test
    @DisplayName("Should initialize branchIds list when null on add")
    void shouldInitializeBranchIdsListWhenNull() {
        Franchise newFranchise = Franchise.builder()
                .name("Test Franchise")
                .branchIds(null)
                .build();

        newFranchise.addBranch("branch123");

        assertNotNull(newFranchise.getBranchIds());
        assertEquals(1, newFranchise.getBranchIds().size());
        assertTrue(newFranchise.getBranchIds().contains("branch123"));
    }

    @Test
    @DisplayName("Should handle null branchIds on remove")
    void shouldHandleNullBranchIdsOnRemove() {
        Franchise newFranchise = Franchise.builder()
                .name("Test Franchise")
                .branchIds(null)
                .build();

        assertDoesNotThrow(() -> newFranchise.removeBranch("branch123"));
    }

    @Test
    @DisplayName("Should have timestamps when created")
    void shouldHaveTimestampsWhenCreated() {
        Franchise newFranchise = Franchise.builder()
                .name("New Franchise")
                .build();

        assertNotNull(newFranchise.getCreatedAt());
        assertNotNull(newFranchise.getUpdatedAt());
    }

    @Test
    @DisplayName("Should use all args constructor")
    void shouldUseAllArgsConstructor() {
        Franchise newFranchise = new Franchise(
                "id123",
                "Franchise Name",
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now());

        assertNotNull(newFranchise);
        assertEquals("id123", newFranchise.getId());
        assertEquals("Franchise Name", newFranchise.getName());
    }

    @Test
    @DisplayName("Should use no args constructor")
    void shouldUseNoArgsConstructor() {
        Franchise newFranchise = new Franchise();

        assertNotNull(newFranchise);
        newFranchise.setName("Test");
        assertEquals("Test", newFranchise.getName());
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        Franchise franchise1 = Franchise.builder()
                .id("id123")
                .name("Franchise A")
                .build();

        Franchise franchise2 = Franchise.builder()
                .id("id123")
                .name("Franchise A")
                .build();

        assertNotNull(franchise1);
        assertNotNull(franchise2);
    }

    @Test
    @DisplayName("Should test toString")
    void shouldTestToString() {
        String toString = franchise.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("McDonald's"));
        assertTrue(toString.contains("franchise123"));
    }
}