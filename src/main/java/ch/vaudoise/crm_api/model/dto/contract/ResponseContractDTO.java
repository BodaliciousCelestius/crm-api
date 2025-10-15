package ch.vaudoise.crm_api.model.dto.contract;

import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import lombok.Builder;
import org.bson.types.Decimal128;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseContractDTO(
    String id,
    @NotNull LocalDate startDate,
    LocalDate endDate,
    @NotNull @PositiveOrZero Decimal128 cost,
    @NotNull @Valid ResponseClientDTO client) {}
