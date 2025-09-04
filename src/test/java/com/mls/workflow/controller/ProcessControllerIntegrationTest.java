package com.mls.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.ProcessRequestDto;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.profiles.active=test")
@ActiveProfiles("test")
class ProcessControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean // Mock RuntimeService to prevent actual Camunda engine interaction
    private RuntimeService runtimeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testStartProcess_MissingIdForRead_ShouldReturn400() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ProcessRequestDto request = new ProcessRequestDto("READ", null, null);


        mockMvc.perform(post("/api/cadastro/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error"))
                .andExpect(jsonPath("$.errors.id").exists());

        verifyNoInteractions(runtimeService);
    }

    @Test
    void testStartProcess_MissingPayloadForCreate_ShouldReturn400() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        ProcessRequestDto request = new ProcessRequestDto("CREATE", null, null);


        mockMvc.perform(post("/api/cadastro/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation error"))
                .andExpect(jsonPath("$.errors.payload").exists());

        verifyNoInteractions(runtimeService);
    }
}