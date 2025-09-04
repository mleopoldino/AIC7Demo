package com.mls.workflow.core.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonBindingDebugTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonDeserialization_CREATE() throws JsonProcessingException {
        String json = "{\"tarefa\": \"CREATE\", \"payload\": {\"nome\": \"João\", \"email\": \"joao@teste.com\", \"idade\": 30}}";
        
        ProcessRequestDto result = objectMapper.readValue(json, ProcessRequestDto.class);
        
        assertNotNull(result);
        assertEquals("CREATE", result.getTarefa());
        assertNotNull(result.getPayload());
        assertEquals("João", result.getPayload().getNome());
    }

    @Test
    public void testJsonDeserialization_READ() throws JsonProcessingException {
        String json = "{\"tarefa\": \"READ\", \"id\": 1}";
        
        ProcessRequestDto result = objectMapper.readValue(json, ProcessRequestDto.class);
        
        assertNotNull(result);
        assertEquals("READ", result.getTarefa());
        assertEquals(Long.valueOf(1), result.getId());
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        PayloadDto payload = new PayloadDto("João", "joao@teste.com", 30);
        ProcessRequestDto request = new ProcessRequestDto("CREATE", null, payload);
        
        String json = objectMapper.writeValueAsString(request);
        
        assertTrue(json.contains("\"tarefa\":\"CREATE\""));
        assertTrue(json.contains("\"nome\":\"João\""));
    }
}