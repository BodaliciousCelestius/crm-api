package ch.vaudoise.crm_api.model.dto.client;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.validation.ClientValidatable;
import ch.vaudoise.crm_api.validation.ValidClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@ValidClient
public record CreateClientDTO(
    @NotNull ClientType type,
    @NotBlank String name,
    @NotBlank String phone,
    @NotBlank @Email String email,
    @Past LocalDate birthday,
    String companyIdentifier
) implements ClientValidatable {}
