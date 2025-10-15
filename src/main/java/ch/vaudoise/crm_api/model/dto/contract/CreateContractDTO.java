package ch.vaudoise.crm_api.model.dto.contract;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import lombok.Builder;
import org.bson.types.Decimal128;

@Builder(toBuilder = true)
public record CreateContractDTO(
    LocalDate startDate, LocalDate endDate, @NotNull @PositiveOrZero Decimal128 cost) {}
