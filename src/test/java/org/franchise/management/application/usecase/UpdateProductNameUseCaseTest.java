package org.franchise.management.application.usecase;

import org.franchise.management.domain.model.Product;
import org.franchise.management.domain.repository.ProductRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.adapters.ProductMongoAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ✅ Tests unitarios para UpdateProductNameUseCase
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductNameUseCase Tests")
class UpdateProductNameUseCaseTest {

        @Mock
        private ProductMongoAdapter productRepository;

        @InjectMocks
        private UpdateProductNameUseCase updateProductNameUseCase;

        private Product product;
        private String productId;
        private String newName;

        @BeforeEach
        void setUp() {
                productId = "product123";
                newName = "Café Premium 500g";
                product = Product.builder()
                                .id(productId)
                                .name(newName)
                                .branchId("branch001")
                                .stock(50)
                                .build();
        }

        @Test
        @DisplayName("✅ Should update product name successfully")
        void shouldUpdateProductNameSuccessfully() {
                when(productRepository.updateProductName(eq(productId), eq(newName)))
                                .thenReturn(Mono.just(product));

                StepVerifier.create(updateProductNameUseCase.updateProductName(productId, newName))
                                .expectNextMatches(p -> p.getName().equals(newName))
                                .verifyComplete();

                verify(productRepository).updateProductName(productId, newName);
        }

        @Test
        @DisplayName("❌ Should return error when product not found")
        void shouldReturnErrorWhenProductNotFound() {
                when(productRepository.updateProductName(eq(productId), eq(newName)))
                                .thenReturn(Mono.empty());

                StepVerifier.create(updateProductNameUseCase.updateProductName(productId, newName))
                                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                                                e.getMessage().equals("Producto no encontrado"))
                                .verify();

                verify(productRepository).updateProductName(productId, newName);
        }

        @Test
        @DisplayName("❌ Should handle unexpected exception when updating product name")
        void shouldHandleUnexpectedException() {
                when(productRepository.updateProductName(eq(productId), eq(newName)))
                                .thenReturn(Mono.error(new RuntimeException("Database error")));

                StepVerifier.create(updateProductNameUseCase.updateProductName(productId, newName))
                                .expectErrorMatches(e -> e instanceof RuntimeException &&
                                                e.getMessage().equals("Database error"))
                                .verify();

                verify(productRepository).updateProductName(productId, newName);
        }
}
