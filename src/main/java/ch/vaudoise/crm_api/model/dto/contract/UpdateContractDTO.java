package ch.vaudoise.crm_api.model.dto.contract;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record UpdateContractDTO(
    OffsetDateTime startDate, OffsetDateTime endDate, @PositiveOrZero BigDecimal cost) {}
