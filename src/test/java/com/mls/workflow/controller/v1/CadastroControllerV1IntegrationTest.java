package com.mls.workflow.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.v1.CreateRequestDto;
import com.mls.workflow.core.dto.v1.UpdateRequestDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CadastroControllerV1.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class CadastroControllerV1IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RuntimeService runtimeService;

    @Test
    void create_shouldReturn201_whenRequestIsValid() throws Exception {
        CreateRequestDto createDto = new CreateRequestDto();
        createDto.setNome("Test");
        createDto.setEmail("test@test.com");
        createDto.setIdade(30);

        CadastroDto resultDto = new CadastroDto(1L, "Test", "test@test.com", 30);
        
        ProcessInstance mockInstance = mock(ProcessInstance.class);
        when(mockInstance.getProcessInstanceId()).thenReturn("mock-process-id");

        VariableMap variables = Variables.createVariables()
                .putValue("result", resultDto)
                .putValue("statusCode", 201);

        when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(VariableMap.class)))
                .thenReturn(mockInstance);
        when(runtimeService.getVariablesTyped(anyString(), anyBoolean())).thenReturn(variables);

        mockMvc.perform(post("/api/v1/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/cadastro/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Test"));
    }

    @Test
    void read_shouldReturn200_whenIdExists() throws Exception {
        CadastroDto resultDto = new CadastroDto(1L, "Test", "test@test.com", 30);
        ProcessInstance mockInstance = mock(ProcessInstance.class);
        when(mockInstance.getProcessInstanceId()).thenReturn("mock-process-id");

        VariableMap variables = Variables.createVariables()
                .putValue("result", resultDto)
                .putValue("statusCode", 200);

        when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(VariableMap.class)))
                .thenReturn(mockInstance);
        when(runtimeService.getVariablesTyped(anyString(), anyBoolean())).thenReturn(variables);

        mockMvc.perform(get("/api/v1/cadastro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void read_shouldReturn404_whenIdDoesNotExist() throws Exception {
        ProcessInstance mockInstance = mock(ProcessInstance.class);
        when(mockInstance.getProcessInstanceId()).thenReturn("mock-process-id");

        VariableMap variables = Variables.createVariables()
                .putValue("result", null)
                .putValue("statusCode", 404);

        when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(VariableMap.class)))
                .thenReturn(mockInstance);
        when(runtimeService.getVariablesTyped(anyString(), anyBoolean())).thenReturn(variables);

        mockMvc.perform(get("/api/v1/cadastro/99"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void update_shouldReturn200_whenRequestIsValid() throws Exception {
        UpdateRequestDto updateDto = new UpdateRequestDto();
        updateDto.setNome("Updated Name");

        CadastroDto resultDto = new CadastroDto(1L, "Updated Name", "test@test.com", 30);
        ProcessInstance mockInstance = mock(ProcessInstance.class);
        when(mockInstance.getProcessInstanceId()).thenReturn("mock-process-id");

        VariableMap variables = Variables.createVariables()
                .putValue("result", resultDto)
                .putValue("statusCode", 200);

        when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(VariableMap.class)))
                .thenReturn(mockInstance);
        when(runtimeService.getVariablesTyped(anyString(), anyBoolean())).thenReturn(variables);

        mockMvc.perform(put("/api/v1/cadastro/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Updated Name"));
    }

    @Test
    void delete_shouldReturn204_whenIdExists() throws Exception {
        ProcessInstance mockInstance = mock(ProcessInstance.class);
        when(mockInstance.getProcessInstanceId()).thenReturn("mock-process-id");

        VariableMap variables = Variables.createVariables()
                .putValue("statusCode", 204);

        when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(VariableMap.class)))
                .thenReturn(mockInstance);
        when(runtimeService.getVariablesTyped(anyString(), anyBoolean())).thenReturn(variables);

        mockMvc.perform(delete("/api/v1/cadastro/1"))
                .andExpect(status().isNoContent());
    }
}
