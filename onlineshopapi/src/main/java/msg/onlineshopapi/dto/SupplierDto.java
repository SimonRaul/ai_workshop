package msg.onlineshopapi.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {

    private UUID id;
    private String name;
    private String location;
    private String description;
    private String contactEmail;
    private String phone;
}
