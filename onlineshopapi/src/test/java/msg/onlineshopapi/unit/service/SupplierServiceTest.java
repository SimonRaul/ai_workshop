package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.SupplierRepository;
import msg.onlineshopapi.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private final UUID supplierId = UUID.randomUUID();

    @Test
    void findAll_returnsSuppliers() {
        List<Supplier> suppliers = List.of(supplier());
        when(supplierRepository.findAll()).thenReturn(suppliers);

        assertThat(supplierService.findAll()).isSameAs(suppliers);
    }

    @Test
    void findById_returnsSupplier_whenFound() {
        Supplier supplier = supplier();
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));

        assertThat(supplierService.findById(supplierId)).containsSame(supplier);
    }

    @Test
    void findById_returnsEmpty_whenMissing() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThat(supplierService.findById(supplierId)).isEmpty();
    }

    @Test
    void save_delegatesToRepository() {
        Supplier supplier = supplier();
        when(supplierRepository.save(supplier)).thenReturn(supplier);

        assertThat(supplierService.save(supplier)).isSameAs(supplier);
        verify(supplierRepository).save(supplier);
    }

    @Test
    void update_updatesAllMutableFields_whenFound() {
        Supplier existing = supplier();
        Supplier changes = Supplier.builder()
                .name("Updated")
                .location("Bucharest")
                .description("Updated description")
                .contactEmail("updated@example.com")
                .phone("+40-123")
                .build();
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existing));
        when(supplierRepository.save(existing)).thenReturn(existing);

        assertThat(supplierService.update(supplierId, changes)).isSameAs(existing);
        assertThat(existing)
                .extracting(Supplier::getName, Supplier::getLocation, Supplier::getDescription,
                        Supplier::getContactEmail, Supplier::getPhone)
                .containsExactly("Updated", "Bucharest", "Updated description", "updated@example.com", "+40-123");
    }

    @Test
    void update_throwsResourceNotFound_whenMissing() {
        Supplier changes = supplier();
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> supplierService.update(supplierId, changes))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Supplier not found with id: " + supplierId);
    }

    @Test
    void deleteById_delegatesToRepository() {
        supplierService.deleteById(supplierId);

        verify(supplierRepository).deleteById(supplierId);
    }

    private Supplier supplier() {
        return Supplier.builder().id(supplierId).name("TechSource").location("Shenzhen").build();
    }
}
