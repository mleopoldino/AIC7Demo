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
        context.disableDefaultConstraintViolation();

        String tarefa = request.getTarefa();
        
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
            } else {
                // Validate nested payload fields when payload is present
                if (request.getPayload().getNome() == null || request.getPayload().getNome().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Name cannot be blank")
                           .addPropertyNode("payload.nome")
                           .addConstraintViolation();
                    valid = false;
                }
                if (request.getPayload().getEmail() == null || request.getPayload().getEmail().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate("Email cannot be blank")
                           .addPropertyNode("payload.email")
                           .addConstraintViolation();
                    valid = false;
                } else if (!isValidEmail(request.getPayload().getEmail())) {
                    context.buildConstraintViolationWithTemplate("Email should be valid")
                           .addPropertyNode("payload.email")
                           .addConstraintViolation();
                    valid = false;
                }
                if (request.getPayload().getIdade() < 0) {
                    context.buildConstraintViolationWithTemplate("Age cannot be negative")
                           .addPropertyNode("payload.idade")
                           .addConstraintViolation();
                    valid = false;
                }
            }
        }

        return valid;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}