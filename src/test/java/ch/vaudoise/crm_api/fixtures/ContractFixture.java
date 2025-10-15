package ch.vaudoise.crm_api.fixtures;

import static ch.vaudoise.crm_api.fixtures.ClientFixture.aClient;
import static ch.vaudoise.crm_api.fixtures.ClientFixture.aResponseClientDTO;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.model.entity.Contract;
import java.time.Instant;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;

public class ContractFixture {
  public static Contract aContract() {
    return Contract.builder()
        .id(new ObjectId())
        .startDate(LocalDate.of(2023, 10, 22))
        .endDate(LocalDate.of(2026, 12, 1))
        .cost(Decimal128.parse("2"))
        .updatedAt(Instant.now())
        .clientId(aClient().getId())
        .build();
  }

  public static ResponseContractDTO aResponseContractDTO() {
    return new ResponseContractDTO(
        "1",
        LocalDate.now().minusYears(2),
        LocalDate.now().minusMonths(5),
        Decimal128.parse("10"),
        aResponseClientDTO());
  }

  public static CreateContractDTO aCreateContractDTO() {
    return new CreateContractDTO(
        LocalDate.now().minusYears(2), LocalDate.now().minusMonths(5), Decimal128.parse("10"));
  }

  public static UpdateContractDTO anUpdateContractDTO() {
    return new UpdateContractDTO(
        LocalDate.now().minusYears(2), LocalDate.now().minusMonths(5), Decimal128.parse("2"));
  }
}
