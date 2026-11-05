package msg.onlineshopapi.dto;

import lombok.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    private List<OrderItemRequestDto> items;

    @Valid
    @NotNull
    private AddressDto address;
}
