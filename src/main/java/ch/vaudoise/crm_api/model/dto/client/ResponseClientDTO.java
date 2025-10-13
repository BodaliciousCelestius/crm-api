package ch.vaudoise.crm_api.model.dto.client;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import ch.vaudoise.crm_api.model.ClientType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record ResponseClientDTO(
    String id,
    @NotNull ClientType type,
    @NotBlank String name,
    @NotBlank String phone,
    @NotBlank @Email String email,
    @Past LocalDate birthday,
    String companyIdentifier) {}
