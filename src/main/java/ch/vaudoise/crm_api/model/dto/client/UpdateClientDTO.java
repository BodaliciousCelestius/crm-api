package ch.vaudoise.crm_api.model.dto.client;

import ch.vaudoise.crm_api.model.ClientType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateClientDTO(
    ClientType type,
    String name,
    @Pattern(
            regexp = "^\\+?\\d{7,15}$",
            message =
                "Phone number must be numeric and between 7 and 15 digits, optionally starting with '+'")
        String phone,
    @Email String email) {}
