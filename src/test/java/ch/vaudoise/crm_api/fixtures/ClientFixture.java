package ch.vaudoise.crm_api.fixtures;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.model.entity.Client;
import java.time.LocalDate;
import org.bson.types.ObjectId;

public class ClientFixture {

  public static Client aClient() {
    return Client.builder()
        .id(new ObjectId("507f1f77bcf86cd799439011"))
        .type(ClientType.PERSON)
        .name("John Doe")
        .phone("+5126323423422")
        .email("john.doe@anonymous.org")
        .birthday(LocalDate.of(1994, 10, 22))
        .build();
  }

  public static ResponseClientDTO aResponseClientDTO() {
    return new ResponseClientDTO(
        "1",
        ClientType.PERSON,
        "John Doe",
        "+41791234567",
        "john@example.com",
        LocalDate.of(1994, 10, 19),
        null);
  }

  public static CreateClientDTO aCreateClientDTO() {
    return new CreateClientDTO(
        ClientType.COMPANY,
        "Corpo Business LLC.",
        "+41791234567",
        "john@example.com",
        null,
        "corpo-b");
  }

  public static UpdateClientDTO anUpdateClientDTO() {
    return new UpdateClientDTO(
        ClientType.PERSON, "John Doe", "+41797654321", "john.smith@example.com");
  }
}
