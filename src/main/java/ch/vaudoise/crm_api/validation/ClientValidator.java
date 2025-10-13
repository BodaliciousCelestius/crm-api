package ch.vaudoise.crm_api.validation;

import ch.vaudoise.crm_api.model.ClientType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ClientValidator implements ConstraintValidator<ValidClient, ClientValidatable> {

    @Override
    public boolean isValid(ClientValidatable client, ConstraintValidatorContext context) {
        if (client == null) return true;

        boolean valid = true;

        context.disableDefaultConstraintViolation();

        if (client.type() == ClientType.COMPANY) {
            if (client.birthday() != null) {
                context.buildConstraintViolationWithTemplate("Company must not have a birthday")
                        .addPropertyNode("birthday")
                        .addConstraintViolation();
                valid = false;
            }
            else if (client.companyIdentifier() == null || client.companyIdentifier().isBlank()) {
                context.buildConstraintViolationWithTemplate("Company must have a companyIdentifier")
                        .addPropertyNode("companyIdentifier")
                        .addConstraintViolation();
                valid = false;
            }
        }

        if (client.type() == ClientType.PERSON) {
            if (client.birthday() == null) {
                context.buildConstraintViolationWithTemplate("Person must have a birthday")
                        .addPropertyNode("birthday")
                        .addConstraintViolation();
                valid = false;
            }
            if (client.companyIdentifier() == null || client.companyIdentifier().isBlank()) {
                context.buildConstraintViolationWithTemplate("Person must not have a companyIdentifier")
                        .addPropertyNode("companyIdentifier")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}

