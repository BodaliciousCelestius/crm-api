package ch.vaudoise.crm_api.model.dto.client;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

import ch.vaudoise.crm_api.model.ClientType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record ResponseClientDTO(
    String id,
    @NotNull ClientType type,
    @NotBlank String name,
    @NotBlank
        @Pattern(
            regexp = "^\\+?\\d{7,15}$",
            message =
                "Phone number must be numeric and between 7 and 15 digits, optionally starting with '+'")
        String phone,
    @NotBlank @Email String email,
    @Past LocalDate birthday,
    String companyIdentifier) {}
