package ch.vaudoise.crm_api.model.dto.client;

import ch.vaudoise.crm_api.model.ClientType;
import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UpdateClientDTO(
        ClientType type,
        String name,
        String phone,
        @Email String email
) {}
