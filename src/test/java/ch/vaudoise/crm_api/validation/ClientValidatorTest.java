package ch.vaudoise.crm_api.validation;

import static org.junit.jupiter.api.Assertions.*;

import ch.vaudoise.crm_api.model.ClientType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClientValidatorTest {

  private static Validator validator;

  @ValidClient
  public record TestClient(@NotNull ClientType type, String companyIdentifier, LocalDate birthday)
      implements ClientValidatable {}

  @BeforeAll
  static void setupValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void companyWithoutIdentifier_shouldFail() {
    var client = new TestClient(ClientType.COMPANY, null, null);
    var violations = validator.validate(client);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("Company must have a companyIdentifier")));
  }

  @Test
  void companyWithBirthday_shouldFail() {
    var client = new TestClient(ClientType.COMPANY, "CHE-123", LocalDate.parse("1990-01-01"));
    var violations = validator.validate(client);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("Company must not have a birthday")));
  }

  @Test
  void validCompany_shouldPass() {
    var client = new TestClient(ClientType.COMPANY, "CHE-123", null);
    var violations = validator.validate(client);
    assertTrue(violations.isEmpty());
  }

  @Test
  void personWithoutBirthday_shouldFail() {
    var client = new TestClient(ClientType.PERSON, null, null);
    var violations = validator.validate(client);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().contains("Person must have a birthday")));
  }

  @Test
  void personWithCompanyIdentifier_shouldFail() {
    var client = new TestClient(ClientType.PERSON, "CHE-123", LocalDate.parse("1990-01-01"));
    var violations = validator.validate(client);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getMessage().contains("Person must not have a companyIdentifier")));
  }

  @Test
  void validPerson_shouldPass() {
    var client = new TestClient(ClientType.PERSON, null, LocalDate.parse("1990-01-01"));
    var violations = validator.validate(client);
    assertTrue(violations.isEmpty());
  }
}
