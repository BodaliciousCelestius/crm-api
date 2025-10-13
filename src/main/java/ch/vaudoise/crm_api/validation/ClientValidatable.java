package ch.vaudoise.crm_api.validation;

import ch.vaudoise.crm_api.model.ClientType;

import java.time.LocalDate;

public interface ClientValidatable {
    ClientType type();
    LocalDate birthday();
    String companyIdentifier();
}

