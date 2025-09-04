package com.mls.workflow.core.validation;

import com.mls.workflow.core.dto.ProcessRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProcessRequestValidator implements ConstraintValidator<ValidProcessRequest, ProcessRequestDto> {

    @Override
    public void initialize(ValidProcessRequest constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(ProcessRequestDto request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean valid = true;
        String tarefa = request.getTarefa();
        
        // Don't disable default constraint violations - let @NotBlank work on tarefa
        if (tarefa == null || tarefa.trim().isEmpty()) {
            // Let @NotBlank handle this case
            return true; // The @NotBlank annotation will handle the validation
        }
        
        // Only add custom constraint violations for conditional validation
        context.disableDefaultConstraintViolation();
        
        // Validate conditional requirements based on operation type
        if ("READ".equals(tarefa) || "UPDATE".equals(tarefa) || "DELETE".equals(tarefa)) {
            if (request.getId() == null) {
                context.buildConstraintViolationWithTemplate("ID is required for " + tarefa + " operations")
                       .addPropertyNode("id")
                       .addConstraintViolation();
                valid = false;
            }
        }
        
        if ("CREATE".equals(tarefa) || "UPDATE".equals(tarefa)) {
            if (request.getPayload() == null) {
                context.buildConstraintViolationWithTemplate("Payload is required for " + tarefa + " operations")
                       .addPropertyNode("payload")
                       .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}