package ch.vaudoise.crm_api.model.entity;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
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

  @NotBlank
  @Pattern(
      regexp = "^\\+?\\d{7,15}$",
      message =
          "Phone number must be numeric and between 7 and 15 digits, optionally starting with '+'")
  private String phone;

  @Email @NotBlank private String email;

  @Past(message = "Birthday must be in the past")
  private LocalDate birthday;

  private String companyIdentifier;

  @Version private Integer version;

  public ResponseClientDTO toDTO() {
    ResponseClientDTO.ResponseClientDTOBuilder dtoBuilder = ResponseClientDTO.builder();

    if (id != null) dtoBuilder.id(id.toString());
    if (type != null) dtoBuilder.type(type);
    if (name != null) dtoBuilder.name(name);
    if (phone != null) dtoBuilder.phone(phone);
    if (email != null) dtoBuilder.email(email);
    if (birthday != null) dtoBuilder.birthday(birthday);
    if (companyIdentifier != null) dtoBuilder.companyIdentifier(companyIdentifier);

    return dtoBuilder.build();
  }
}
