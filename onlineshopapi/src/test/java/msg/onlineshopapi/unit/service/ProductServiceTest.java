package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.OrderDetailRepository;
import msg.onlineshopapi.repository.ProductRepository;
import msg.onlineshopapi.service.ProductService;
import msg.onlineshopapi.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private SupplierService supplierService;

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
        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(supplierService.findById(supplier.getId())).thenReturn(Optional.of(supplier));
        when(productRepository.save(existing)).thenReturn(existing);

        Product result = productService.update(productId, changes);

        assertThat(result.getSupplier()).isSameAs(supplier);
    }

    @Test
    void update_throwsResourceNotFound_whenProductMissing() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(productId, Product.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void save_resolvesSupplier_whenSupplierExists() {
        Supplier supplier = Supplier.builder().id(UUID.randomUUID()).build();
        Product product = Product.builder().supplier(supplier).build();
        when(supplierService.findById(supplier.getId())).thenReturn(Optional.of(supplier));
        when(productRepository.save(product)).thenReturn(product);

        assertThat(productService.save(product)).isSameAs(product);
    }

    @Test
    void save_throwsResourceNotFound_whenSupplierMissing() {
        UUID supplierId = UUID.randomUUID();
        Product product = Product.builder().supplier(Supplier.builder().id(supplierId).build()).build();
        when(supplierService.findById(supplierId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Supplier not found with id: " + supplierId);
    }

    @Test
    void save_throwsResourceNotFound_whenSupplierIsNull() {
        Product product = Product.builder().build();

        assertThatThrownBy(() -> productService.save(product))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Supplier not found with id: null");
    }

    @Test
    void update_throwsResourceNotFound_whenSupplierMissing() {
        UUID productId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        Product existing = Product.builder().id(productId).build();
        Product changes = Product.builder().supplier(Supplier.builder().id(supplierId).build()).build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(supplierService.findById(supplierId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(productId, changes))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Supplier not found with id: " + supplierId);
    }
}
