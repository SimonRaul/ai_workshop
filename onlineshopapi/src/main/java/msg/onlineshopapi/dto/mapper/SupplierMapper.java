package msg.onlineshopapi.dto.mapper;

import msg.onlineshopapi.dto.SupplierDto;
import msg.onlineshopapi.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDto toDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .location(supplier.getLocation())
                .description(supplier.getDescription())
                .contactEmail(supplier.getContactEmail())
                .phone(supplier.getPhone())
                .build();
    }

    public Supplier toEntity(SupplierDto dto) {
        return Supplier.builder()
                .id(dto.getId())
                .name(dto.getName())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .contactEmail(dto.getContactEmail())
                .phone(dto.getPhone())
                .build();
    }
}
