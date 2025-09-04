package com.mls.workflow.core.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayloadDtoValidationTest {

    static Validator validator;
    static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldFailWhenNomeIsBlank() {
        var payload = new PayloadDto();
        payload.setNome(""); // blank
        payload.setEmail("valid@example.com");
        payload.setIdade(25);

        var violations = validator.validate(payload);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nome"));
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        var payload = new PayloadDto();
        payload.setNome("Valid Name");
        payload.setEmail("invalid-email"); // invalid email
        payload.setIdade(25);

        var violations = validator.validate(payload);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shouldFailWhenIdadeIsNegative() {
        var payload = new PayloadDto();
        payload.setNome("Valid Name");
        payload.setEmail("valid@example.com");
        payload.setIdade(-1); // negative age

        var violations = validator.validate(payload);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("idade"));
    }

    @Test
    void shouldPassWithValidPayload() {
        var payload = new PayloadDto();
        payload.setNome("Valid Name");
        payload.setEmail("valid@example.com");
        payload.setIdade(25);

        var violations = validator.validate(payload);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldDeserializeJsonCorrectly() throws Exception {
        String json = """
        {
            "nome": "João Silva",
            "email": "joao@example.com",
            "idade": 30
        }
        """;

        PayloadDto payload = objectMapper.readValue(json, PayloadDto.class);
        
        assertThat(payload.getNome()).isEqualTo("João Silva");
        assertThat(payload.getEmail()).isEqualTo("joao@example.com");
        assertThat(payload.getIdade()).isEqualTo(30);
    }

    @Test
    void shouldValidateJsonDeserializedPayload() throws Exception {
        String json = """
        {
            "nome": "Valid User",
            "email": "user@example.com",
            "idade": 25
        }
        """;

        PayloadDto payload = objectMapper.readValue(json, PayloadDto.class);
        var violations = validator.validate(payload);
        
        assertThat(violations).isEmpty();
    }
}