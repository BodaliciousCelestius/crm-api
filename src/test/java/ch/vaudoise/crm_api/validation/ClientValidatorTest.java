package ch.vaudoise.crm_api.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.vaudoise.crm_api.model.ClientType;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ClientValidatorTest {

    private ClientValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ClientValidator();
        context = mock(ConstraintValidatorContext.class);
        ConstraintViolationBuilder violationBuilder = mock(ConstraintViolationBuilder.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));
    }

    record TestClient(ClientType type, String companyIdentifier, LocalDate birthday) implements ClientValidatable {}

    @Test
    void testCompanyWithoutIdentifier_shouldBeInvalid() {
        var client = new TestClient(ClientType.COMPANY, null, null);
        boolean result = validator.isValid(client, context);
        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate("Company must have a companyIdentifier");
    }

    @Test
    void testCompanyWithBirthday_shouldBeInvalid() {
        var client = new TestClient(ClientType.COMPANY, "CHE-123", LocalDate.parse("1990-01-01"));
        boolean result = validator.isValid(client, context);
        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate("Company must not have a birthday");
    }

    @Test
    void testValidCompany_shouldBeValid() {
        var client = new TestClient(ClientType.COMPANY, "CHE-123", null);
        boolean result = validator.isValid(client, context);
        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void testPersonWithoutBirthday_shouldBeInvalid() {
        var client = new TestClient(ClientType.PERSON, null, null);
        boolean result = validator.isValid(client, context);
        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate("Person must have a birthday");
    }

    @Test
    void testPersonWithCompanyIdentifier_shouldBeInvalid() {
        var client = new TestClient(ClientType.PERSON, "CHE-123", LocalDate.parse("1990-01-01"));
        boolean result = validator.isValid(client, context);
        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate("Person must not have a companyIdentifier");
    }

    @Test
    void testValidPerson_shouldBeValid() {
        var client = new TestClient(ClientType.PERSON, null, LocalDate.parse("1990-01-01"));
        boolean result = validator.isValid(client, context);
        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void testNullClient_shouldBeValid() {
        boolean result = validator.isValid(null, context);
        assertTrue(result);
        verifyNoInteractions(context);
    }
}
