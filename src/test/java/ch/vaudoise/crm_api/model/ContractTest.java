package ch.vaudoise.crm_api.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import ch.vaudoise.crm_api.model.entity.Contract;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class ContractTest {

  @Test
  void testShouldConvertAllFieldsToDTO() {
    ObjectId id = new ObjectId();
    ObjectId clientId = new ObjectId();
    LocalDate start = LocalDate.now();
    LocalDate end = LocalDate.now().plusDays(10);
    Decimal128 cost = new Decimal128(250);

    ResponseClientDTO clientDTO =
        ResponseClientDTO.builder().id(clientId.toString()).name("Test Client").build();

    Contract contract =
        Contract.builder()
            .id(id)
            .clientId(clientId)
            .startDate(start)
            .endDate(end)
            .cost(cost)
            .build();

    ResponseContractDTO dto = contract.toDTO(clientDTO);

    assertThat(dto.id()).isEqualTo(id.toString());
    assertThat(dto.startDate()).isEqualTo(start);
    assertThat(dto.endDate()).isEqualTo(end);
    assertThat(dto.cost()).isEqualTo(cost);
    assertThat(dto.client()).isEqualTo(clientDTO);
  }

  @Test
  void shouldHandleNullFieldsGracefully() {
    Contract contract = Contract.builder().build();
    ResponseClientDTO clientDTO = ResponseClientDTO.builder().id("abc").name("NoClient").build();

    ResponseContractDTO dto = contract.toDTO(clientDTO);

    assertThat(dto.id()).isNull();
    assertThat(dto.startDate()).isEqualTo(LocalDate.now());
    assertThat(dto.endDate()).isNull();
    assertThat(dto.client()).isNull();
  }

  @Test
  void shouldNotSetClientWhenClientIdIsNull() {
    ObjectId id = new ObjectId();
    LocalDate start = LocalDate.now();
    Decimal128 cost = new Decimal128(100);

    Contract contract =
        Contract.builder().id(id).startDate(start).cost(cost).clientId(null).build();

    ResponseClientDTO clientDTO = ResponseClientDTO.builder().id("some-id").name("Client").build();

    ResponseContractDTO dto = contract.toDTO(clientDTO);

    assertThat(dto.client()).isNull();
    assertThat(dto.id()).isEqualTo(id.toString());
    assertThat(dto.cost()).isEqualTo(cost);
  }
}
