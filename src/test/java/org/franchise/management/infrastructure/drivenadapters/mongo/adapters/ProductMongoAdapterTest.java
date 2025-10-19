package org.franchise.management.infrastructure.drivenadapters.mongo.adapters;

import org.franchise.management.domain.model.Branch;
import org.franchise.management.domain.model.Product;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.BranchMongoRepository;
import org.franchise.management.infrastructure.drivenadapters.mongo.repository.ProductMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductMongoAdapterTest {

    @Mock
    private ProductMongoRepository productMongoRepository;

    @Mock
    private BranchMongoRepository branchMongoRepository;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    private ProductMongoAdapter productMongoAdapter;

    private Branch branch;
    private Product product;
    private String branchId;
    private String productId;

    @BeforeEach
    void setUp() {
        branchId = "branch123";
        productId = "product123";

        branch = Branch.builder()
                .id(branchId)
                .productIds(List.of(productId))
                .updatedAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .id(productId)
                .name("Café")
                .branchId(branchId)
                .stock(10)
                .build();
    }

    @Test
    @DisplayName("Should add product to existing branch successfully")
    void shouldAddProductToExistingBranch() {
        when(mongoTemplate.exists(any(Query.class), eq("branches"))).thenReturn(Mono.just(true));
        when(mongoTemplate.save(any(Product.class))).thenReturn(Mono.just(product));
        when(mongoTemplate.updateFirst(any(Query.class), any(Update.class), eq("branches")))
                .thenReturn(Mono.empty());

        StepVerifier.create(productMongoAdapter.addProductToBranch(branchId, product))
                .expectNext(product)
                .verifyComplete();

        verify(mongoTemplate).exists(any(Query.class), eq("branches"));
        verify(mongoTemplate).save(product);
    }

    @Test
    @DisplayName("Should return empty when branch does not exist")
    void shouldReturnEmptyWhenBranchDoesNotExist() {
        when(mongoTemplate.exists(any(Query.class), eq("branches"))).thenReturn(Mono.just(false));

        StepVerifier.create(productMongoAdapter.addProductToBranch(branchId, product))
                .verifyComplete();

        verify(mongoTemplate).exists(any(Query.class), eq("branches"));
        verify(mongoTemplate, never()).save(any());
    }

    @Test
    @DisplayName("Should delete product from branch successfully")
    void shouldDeleteProductFromBranchSuccessfully() {
        branchId = "branch1";
        productId = "prod1";

        branch = Branch.builder()
                .id(branchId)
                .productIds(new ArrayList<>(List.of(productId)))
                .updatedAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .id(productId)
                .branchId(branchId)
                .build();

        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productMongoRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productMongoRepository.delete(product)).thenReturn(Mono.empty());
        when(branchMongoRepository.save(any())).thenReturn(Mono.just(branch));

        StepVerifier.create(productMongoAdapter.deleteProductFromBranch(branchId, productId))
                .verifyComplete();

        verify(branchMongoRepository).findById(branchId);
        verify(productMongoRepository).findById(productId);
        verify(productMongoRepository).delete(product);
        verify(branchMongoRepository).save(any());
    }

    @Test
    @DisplayName("Should throw error when branch not found during deletion")
    void shouldThrowErrorWhenBranchNotFoundDuringDeletion() {
        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(productMongoAdapter.deleteProductFromBranch(branchId, productId))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Branch no encontrado"))
                .verify();
    }

    @Test
    @DisplayName("Should throw error when product not found during deletion")
    void shouldThrowErrorWhenProductNotFoundDuringDeletion() {
        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productMongoRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productMongoAdapter.deleteProductFromBranch(branchId, productId))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Producto no encontrado"))
                .verify();
    }

    @Test
    @DisplayName("Should throw error when product does not belong to branch")
    void shouldThrowErrorWhenProductDoesNotBelongToBranch() {
        Product wrongBranchProduct = Product.builder()
                .id(productId)
                .branchId("anotherBranch")
                .build();

        when(branchMongoRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productMongoRepository.findById(productId)).thenReturn(Mono.just(wrongBranchProduct));

        StepVerifier.create(productMongoAdapter.deleteProductFromBranch(branchId, productId))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("no pertenece"))
                .verify();
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() {
        when(productMongoRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productMongoRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productMongoAdapter.updateProductStock(productId, 20))
                .expectNext(product)
                .verifyComplete();

        verify(productMongoRepository).save(product);
    }

    @Test
    @DisplayName("Should throw error when product not found during stock update")
    void shouldThrowErrorWhenProductNotFoundDuringStockUpdate() {
        when(productMongoRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productMongoAdapter.updateProductStock(productId, 20))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Producto no encontrado"))
                .verify();
    }

    @Test
    @DisplayName("Should update product name successfully")
    void shouldUpdateProductNameSuccessfully() {
        when(productMongoRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productMongoRepository.save(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productMongoAdapter.updateProductName(productId, "Nuevo Café"))
                .expectNext(product)
                .verifyComplete();

        verify(productMongoRepository).findById(productId);
        verify(productMongoRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw error when product not found during name update")
    void shouldThrowErrorWhenProductNotFoundDuringNameUpdate() {
        when(productMongoRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productMongoAdapter.updateProductName(productId, "Nuevo Café"))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("Producto no encontrado"))
                .verify();
    }

    @Test
    @DisplayName("Should find max stock product by branch")
    void shouldFindMaxStockProductByBranch() {
        Product p1 = Product.builder().id("1").branchId("b1").name("P1").stock(10).build();
        Product p2 = Product.builder().id("2").branchId("b1").name("P2").stock(20).build();
        Product p3 = Product.builder().id("3").branchId("b2").name("P3").stock(5).build();
        Product p4 = Product.builder().id("4").branchId("b2").name("P4").stock(50).build();

        when(productMongoRepository.findAll()).thenReturn(Flux.just(p1, p2, p3, p4));

        StepVerifier.create(productMongoAdapter.findMaxStockProductByBranch("anyFranchise"))
                .recordWith(ArrayList::new)
                .expectNextCount(2)
                .consumeRecordedWith(products -> {
                    List<String> productIds = products.stream()
                            .map(Product::getId)
                            .toList();

                    assertTrue(productIds.containsAll(List.of("2", "4"))
                            && productIds.size() == 2);
                })
                .verifyComplete();

        verify(productMongoRepository).findAll();
    }
}
