package ch.vaudoise.crm_api.model.entity;

import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.Instant;
import java.time.LocalDate;
import lombok.*;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("contracts")
public class Contract {

  @Id private ObjectId id;

  @Builder.Default private LocalDate startDate = LocalDate.now();

  @Builder.Default @Indexed private LocalDate endDate = null;

  @NotNull @PositiveOrZero private Decimal128 cost;

  @NotNull @LastModifiedDate @Indexed private Instant updatedAt;

  @NotNull @Indexed private ObjectId clientId;

  @Version private Integer version;

  public ResponseContractDTO toDTO(ResponseClientDTO dto) {
    ResponseContractDTO.ResponseContractDTOBuilder dtoBuilder = ResponseContractDTO.builder();
    if (id != null) dtoBuilder.id(id.toString());
    if (startDate != null) dtoBuilder.startDate(startDate);
    if (endDate != null) dtoBuilder.endDate(endDate);
    if (cost != null) dtoBuilder.cost(cost);
    if (clientId != null) dtoBuilder.client(dto);

    return dtoBuilder.build();
  }
}
