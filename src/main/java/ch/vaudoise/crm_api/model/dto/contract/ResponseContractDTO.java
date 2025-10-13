package ch.vaudoise.crm_api.model.dto.contract;

import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseContractDTO(
    String id,
    @NotNull OffsetDateTime startDate,
    OffsetDateTime endDate,
    @NotNull @PositiveOrZero BigDecimal cost,
    @NotNull @Valid ResponseClientDTO client) {}
