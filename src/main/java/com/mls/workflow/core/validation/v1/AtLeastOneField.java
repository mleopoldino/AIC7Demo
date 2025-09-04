package com.mls.workflow.core.validation.v1;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneFieldValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneField {
    String message() default "Pelo menos um dos campos deve ser preenchido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields();
}
