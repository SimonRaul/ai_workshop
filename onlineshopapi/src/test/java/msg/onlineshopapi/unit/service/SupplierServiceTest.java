package msg.onlineshopapi.unit.service;

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

    private Supplier supplier() {
        return Supplier.builder().id(supplierId).name("TechSource").location("Shenzhen").build();
    }
}
