package com.mls.workflow.core.validation.v1;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {

    private String[] fields;

    @Override
    public void initialize(AtLeastOneField constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Not responsible for null checking
        }

        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);

        for (String fieldName : fields) {
            Object fieldValue = beanWrapper.getPropertyValue(fieldName);
            if (fieldValue != null) {
                // For strings, we might want to check if they are not blank
                if (fieldValue instanceof String && !((String) fieldValue).isBlank()) {
                    return true;
                }
                // For other types, just being not-null is enough
                if (!(fieldValue instanceof String)) {
                    return true;
                }
            }
        }

        return false;
    }
}
