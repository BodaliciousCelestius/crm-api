package ch.vaudoise.crm_api.model.entity;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("clients")
public class Client {

  @Id private ObjectId id;

  @NotNull private ClientType type;

  @NotBlank
  @Indexed(unique = true)
  private String name;

  @NotBlank private String phone;

  @Email @NotBlank private String email;

  @Past private LocalDate birthday;

  private String companyIdentifier;

  @Version private Integer version;

  public ResponseClientDTO toDTO() {
    return ResponseClientDTO.builder()
        .id(id.toString())
        .type(type)
        .name(name)
        .phone(phone)
        .email(email)
        .birthday(birthday)
        .companyIdentifier(companyIdentifier)
        .build();
  }
}
