package com.mls.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.PayloadDto;
import com.mls.workflow.core.dto.ProcessRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
public class SimpleJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testJsonDeserialization() throws Exception {
        String json = "{\"tarefa\": \"CREATE\", \"payload\": {\"nome\": \"João\", \"email\": \"joao@teste.com\", \"idade\": 30}}";
        
        ProcessRequestDto result = objectMapper.readValue(json, ProcessRequestDto.class);
        
        assertNotNull(result);
        assertEquals("CREATE", result.getTarefa());
        assertNotNull(result.getPayload());
        assertEquals("João", result.getPayload().getNome());
    }

    @Test
    public void testJsonSerialization() throws Exception {
        PayloadDto payload = new PayloadDto("João", "joao@teste.com", 30);
        ProcessRequestDto request = new ProcessRequestDto("CREATE", null, payload);
        
        String json = objectMapper.writeValueAsString(request);
        
        assertTrue(json.contains("\"tarefa\":\"CREATE\""));
        assertTrue(json.contains("\"nome\":\"João\""));
    }
}