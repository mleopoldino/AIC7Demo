package com.mls.workflow.core.validation.v1;

import com.mls.workflow.core.dto.v1.UpdateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AtLeastOneFieldValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAtLeastOneFieldIsPresent_thenValidationShouldPass() {
        UpdateRequestDto dto = new UpdateRequestDto();
        dto.setNome("Test Name");
        Set<ConstraintViolation<UpdateRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenAllFieldsArePresent_thenValidationShouldPass() {
        UpdateRequestDto dto = new UpdateRequestDto();
        dto.setNome("Test Name");
        dto.setEmail("test@test.com");
        dto.setIdade(30);
        Set<ConstraintViolation<UpdateRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNoFieldsArePresent_thenValidationShouldFail() {
        UpdateRequestDto dto = new UpdateRequestDto();
        Set<ConstraintViolation<UpdateRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Pelo menos um campo deve ser fornecido para atualização.");
    }
    
    @Test
    void whenFieldsArePresentButBlank_thenValidationShouldFail() {
        UpdateRequestDto dto = new UpdateRequestDto();
        dto.setNome(""); // Blank string
        Set<ConstraintViolation<UpdateRequestDto>> violations = validator.validate(dto);
        assertThat(violations).hasSize(1);
    }
}
