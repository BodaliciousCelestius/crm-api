package ch.vaudoise.crm_api.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class ClientTest {

  @Test
  void shouldConvertAllFieldsToDTO() {
    ObjectId id = new ObjectId();
    String type = "PERSON";
    String name = "John Doe";
    String phone = "+41791234567";
    String email = "john.doe@example.com";
    LocalDate birthday = LocalDate.of(1990, 5, 10);
    String companyIdentifier = "CH123456789";

    Client client =
        Client.builder()
            .id(id)
            .type(ClientType.valueOf(type))
            .name(name)
            .phone(phone)
            .email(email)
            .birthday(birthday)
            .companyIdentifier(companyIdentifier)
            .build();

    ResponseClientDTO dto = client.toDTO();

    assertThat(dto.id()).isEqualTo(id.toString());
    assertThat(dto.type().name()).isEqualTo(type);
    assertThat(dto.name()).isEqualTo(name);
    assertThat(dto.phone()).isEqualTo(phone);
    assertThat(dto.email()).isEqualTo(email);
    assertThat(dto.birthday()).isEqualTo(birthday);
    assertThat(dto.companyIdentifier()).isEqualTo(companyIdentifier);
  }

  @Test
  void shouldHandleNullFieldsGracefully() {
    Client client = Client.builder().build();

    ResponseClientDTO dto = client.toDTO();

    assertThat(dto.id()).isNull();
    assertThat(dto.type()).isNull();
    assertThat(dto.name()).isNull();
    assertThat(dto.phone()).isNull();
    assertThat(dto.email()).isNull();
    assertThat(dto.birthday()).isNull();
    assertThat(dto.companyIdentifier()).isNull();
  }

  @Test
  void shouldConvertOnlyNonNullFields() {
    ObjectId id = new ObjectId();
    String name = "Partial Client";
    String email = "partial@example.com";

    Client client = Client.builder().id(id).name(name).email(email).build();

    ResponseClientDTO dto = client.toDTO();

    assertThat(dto.id()).isEqualTo(id.toString());
    assertThat(dto.name()).isEqualTo(name);
    assertThat(dto.email()).isEqualTo(email);
    assertThat(dto.type()).isNull();
    assertThat(dto.phone()).isNull();
    assertThat(dto.birthday()).isNull();
    assertThat(dto.companyIdentifier()).isNull();
  }
}
