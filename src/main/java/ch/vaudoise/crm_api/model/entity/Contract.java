package ch.vaudoise.crm_api.model.entity;

import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("contracts")
public class Contract {

  @Id private ObjectId id;

  private OffsetDateTime startDate = OffsetDateTime.now();

  @Indexed private OffsetDateTime endDate = null;

  @NotNull @PositiveOrZero private BigDecimal cost;

  @NotNull @LastModifiedDate @Indexed private Instant updatedAt;

  @NotNull
  @DocumentReference(lazy = true)
  private Client client;

  @Version private Integer version;

  public ResponseContractDTO toDTO() {
    return ResponseContractDTO.builder()
        .id(id.toString())
        .startDate(startDate)
        .endDate(endDate)
        .cost(cost)
        .client(client.toDTO())
        .build();
  }
}
