package ch.vaudoise.crm_api.model.dto.client;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.validation.ClientValidatable;
import ch.vaudoise.crm_api.validation.ValidClient;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;

@Builder(toBuilder = true)
@ValidClient
public record CreateClientDTO(
    @NotNull ClientType type,
    @NotBlank String name,
    @NotBlank
        @Pattern(
            regexp = "^\\+?\\d{7,15}$",
            message =
                "Phone number must be numeric and between 7 and 15 digits, optionally starting with '+'")
        String phone,
    @NotBlank @Email String email,
    @Past(message = "Birthday must be in the past") LocalDate birthday,
    String companyIdentifier)
    implements ClientValidatable {}
