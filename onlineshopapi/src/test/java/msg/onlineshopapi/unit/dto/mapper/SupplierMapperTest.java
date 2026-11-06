package msg.onlineshopapi.unit.dto.mapper;

import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.dto.mapper.SupplierMapper;
import msg.onlineshopapi.model.Supplier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierMapperTest {

    private final SupplierMapper supplierMapper = new SupplierMapper();

    @Test
    void toDto_mapsAllFields() {
        Supplier supplier = supplier();

        SupplierDto result = supplierMapper.toDto(supplier);

        assertThat(result.getId()).isEqualTo(supplier.getId());
        assertThat(result.getName()).isEqualTo(supplier.getName());
        assertThat(result.getLocation()).isEqualTo(supplier.getLocation());
        assertThat(result.getDescription()).isEqualTo(supplier.getDescription());
        assertThat(result.getContactEmail()).isEqualTo(supplier.getContactEmail());
        assertThat(result.getPhone()).isEqualTo(supplier.getPhone());
    }

    @Test
    void toDto_returnsNull_whenSupplierIsNull() {
        assertThat(supplierMapper.toDto(null)).isNull();
    }

    private Supplier supplier() {
        return Supplier.builder()
                .id(UUID.randomUUID())
                .name("TechSource")
                .location("Shenzhen")
                .description("Electronics")
                .contactEmail("contact@example.com")
                .phone("+86-123")
                .build();
    }

}
