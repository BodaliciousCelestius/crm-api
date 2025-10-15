package ch.vaudoise.crm_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ClientValidator.class)
@Documented
public @interface ValidClient {

  String message() default "Invalid field combination for client type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
