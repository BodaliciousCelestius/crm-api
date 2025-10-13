package ch.vaudoise.crm_api.model.dto.contract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateContractDTO(
    OffsetDateTime startDate, OffsetDateTime endDate, @NotNull @PositiveOrZero BigDecimal cost) {}
