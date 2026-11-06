package msg.onlineshopapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product name must not be blank")
    private String name;

    @NotBlank(message = "Product description must not be blank")
    private String description;

    @NotNull(message = "Product price must not be null")
    private BigDecimal price;

    @NotNull(message = "Product weight must not be null")
    private Double weight;

    @NotBlank(message = "Product image URL must not be blank")
    private String imageUrl;

    @NotNull(message = "Product category ID must not be null")
    private UUID categoryId;

    @NotNull(message = "Product supplier ID must not be null")
    private UUID supplierId;
}
