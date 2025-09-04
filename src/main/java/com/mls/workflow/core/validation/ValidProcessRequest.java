package com.mls.workflow.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ProcessRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProcessRequest {
    String message() default "Invalid process request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}