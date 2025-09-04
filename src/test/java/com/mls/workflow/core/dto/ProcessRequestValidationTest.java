package com.mls.workflow.core.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessRequestValidationTest {

    static Validator validator;
    static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldFailWhenTarefaBlank() {
        var req = new ProcessRequestDto();
        req.setTarefa(" "); // blank
        var p = new PayloadDto();
        p.setNome("N"); p.setEmail("n@example.com"); p.setIdade(1);
        req.setPayload(p);

        var violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("tarefa"));
    }

    @Test
    void shouldDeserializeJsonCorrectly() throws Exception {
        String json = """
        {
            "tarefa": "CREATE",
            "id": 123,
            "payload": {
                "nome": "João Silva",
                "email": "joao@example.com", 
                "idade": 30
            }
        }
        """;

        ProcessRequestDto request = objectMapper.readValue(json, ProcessRequestDto.class);
        
        assertThat(request.getTarefa()).isEqualTo("CREATE");
        assertThat(request.getId()).isEqualTo(123L);
        assertThat(request.getPayload()).isNotNull();
        assertThat(request.getPayload().getNome()).isEqualTo("João Silva");
        assertThat(request.getPayload().getEmail()).isEqualTo("joao@example.com");
        assertThat(request.getPayload().getIdade()).isEqualTo(30);
    }

    @Test
    void shouldValidateJsonDeserializedRequest() throws Exception {
        String json = """
        {
            "tarefa": "CREATE",
            "payload": {
                "nome": "Valid Name",
                "email": "valid@example.com",
                "idade": 25
            }
        }
        """;

        ProcessRequestDto request = objectMapper.readValue(json, ProcessRequestDto.class);
        var violations = validator.validate(request);
        
        assertThat(violations).isEmpty();
    }
}
