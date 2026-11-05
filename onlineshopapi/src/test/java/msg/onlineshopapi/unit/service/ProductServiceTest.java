package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.OrderDetailRepository;
import msg.onlineshopapi.repository.ProductRepository;
import msg.onlineshopapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void update_setsSupplier_whenProductFound() {
        UUID productId = UUID.randomUUID();
        Supplier supplier = Supplier.builder().id(UUID.randomUUID()).name("TechSource").location("Shenzhen").build();
        Product existing = Product.builder().id(productId).name("Old")
                .supplier(Supplier.builder().id(UUID.randomUUID()).name("Old Supplier").location("Old Location").build())
                .build();
        Product changes = Product.builder().name("New").supplier(supplier).build();
        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.update(productId, changes);

        assertThat(result.getSupplier()).isSameAs(supplier);
    }

    @Test
    void update_throwsResourceNotFound_whenProductMissing() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> productService.update(productId, Product.builder()
                .supplier(Supplier.builder().id(UUID.randomUUID()).name("Supplier").location("Location").build())
                .build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
