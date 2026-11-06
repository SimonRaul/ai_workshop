package msg.onlineshopapi.unit.dto.mapper;

import msg.onlineshopapi.dto.ProductRequestDto;
import msg.onlineshopapi.dto.ProductResponseDto;
import msg.onlineshopapi.dto.mapper.ProductCategoryMapper;
import msg.onlineshopapi.dto.mapper.ProductMapper;
import msg.onlineshopapi.dto.mapper.SupplierMapper;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.ProductCategory;
import msg.onlineshopapi.model.Supplier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper productMapper = new ProductMapper(new ProductCategoryMapper(), new SupplierMapper());

    @Test
    void toDto_mapsSupplierAndCategory() {
        UUID productId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .description("Portable computer")
                .price(BigDecimal.valueOf(999.99))
                .weight(1.5)
                .imageUrl("image")
                .category(ProductCategory.builder().id(categoryId).name("Electronics").build())
                .supplier(Supplier.builder().id(supplierId).name("TechSource").location("Shenzhen").build())
                .build();

        ProductResponseDto result = productMapper.toDto(product);

        assertThat(result.getSupplier().getId()).isEqualTo(supplierId);
        assertThat(result.getSupplier().getName()).isEqualTo("TechSource");
        assertThat(result.getCategory().getId()).isEqualTo(categoryId);
    }

    @Test
    void toEntity_mapsSupplierReference() {
        UUID categoryId = UUID.randomUUID();
        UUID supplierId = UUID.randomUUID();
        ProductRequestDto request = ProductRequestDto.builder()
                .name("Laptop")
                .price(BigDecimal.valueOf(999.99))
                .categoryId(categoryId)
                .supplierId(supplierId)
                .build();

        Product result = productMapper.toEntity(request);

        assertThat(result.getCategory().getId()).isEqualTo(categoryId);
        assertThat(result.getSupplier().getId()).isEqualTo(supplierId);
    }
}
