package msg.onlineshopapi.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    @NotBlank
    private String country;
    @NotBlank
    private String city;
    @NotBlank
    private String county;
    @NotBlank
    private String streetAddress;
}
