package com.mls.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.config.GlobalExceptionHandler;
import com.mls.workflow.core.dto.PayloadDto;
import com.mls.workflow.core.dto.ProcessRequestDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Map;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(controllers = ProcessController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false) // se houver Spring Security
@EnableWebMvc // Explicitly enable Spring MVC
class ProcessControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean RuntimeService runtimeService; // mocka o Camunda

  @Test
  void should_return_400_on_invalid_body_and_not_start_process() throws Exception {
    // payload inválido: tarefa em branco, id null, campos inválidos no payload
    String json = """
      {
        "tarefa": "",
        "id": null,
        "payload": { "nome": "", "email": "bad", "idade": 999 }
      }
      """;

    mockMvc.perform(post("/api/cadastro/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation error"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.path").value("N/A")) // Updated to N/A
        .andExpect(jsonPath("$.errors.tarefa").exists())
        .andExpect(jsonPath("$.errors['payload.nome']").exists())
        .andExpect(jsonPath("$.errors['payload.email']").exists());

    verifyNoInteractions(runtimeService);
  }

  @Test
  void should_return_202_on_valid_body_and_start_process() throws Exception {
    String json = """
      {
        "tarefa": "CREATE",
        "id": 123,
        "payload": { "nome": "João", "email": "joao@ex.com", "idade": 30 }
      }
      """;

    ProcessInstance mockProcessInstance = mock(ProcessInstance.class);
    when(mockProcessInstance.getId()).thenReturn("mockProcessInstanceId");
    when(mockProcessInstance.getBusinessKey()).thenReturn("123");

    when(runtimeService.startProcessInstanceByKey(
            eq("DemoAIProjectCRUDProcess"),
            eq("123"), // businessKey is 123
            any(Map.class)
    )).thenReturn(mockProcessInstance);

    mockMvc.perform(post("/api/cadastro/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.processInstanceId").value("mockProcessInstanceId"))
        .andExpect(jsonPath("$.businessKey").value("123"));
  }

  @Test
  void testStartProcess_TarefaLowercase_ShouldReturn400() throws Exception {
      ProcessRequestDto request = new ProcessRequestDto("create", 789L, 
          new PayloadDto("Pedro Oliveira", "pedro@example.com", 35));

      mockMvc.perform(post("/api/cadastro/process")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Validation error"))
              .andExpect(jsonPath("$.errors.tarefa").value("tarefa must be one of CREATE, READ, UPDATE, DELETE"));

      verifyNoInteractions(runtimeService);
  }

  @Test
  void testStartProcess_TarefaUpsert_ShouldReturn400() throws Exception {
      ProcessRequestDto request = new ProcessRequestDto("UPSERT", 999L, 
          new PayloadDto("Ana Costa", "ana@example.com", 28));

      mockMvc.perform(post("/api/cadastro/process")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("Validation error"))
              .andExpect(jsonPath("$.errors.tarefa").value("tarefa must be one of CREATE, READ, UPDATE, DELETE"));

      verifyNoInteractions(runtimeService);
  }

  @Test
  void testStartProcess_ReadOperation_ShouldReturn202() throws Exception {
      ProcessInstance mockProcessInstance = mock(ProcessInstance.class);
      when(mockProcessInstance.getId()).thenReturn("process-instance-read");
      when(mockProcessInstance.getBusinessKey()).thenReturn("100");
      when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(Map.class)))
              .thenReturn(mockProcessInstance);

      ProcessRequestDto request = new ProcessRequestDto("READ", 100L, null);

      mockMvc.perform(post("/api/cadastro/process")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isAccepted());

      ArgumentCaptor<Map> variablesCaptor = ArgumentCaptor.forClass(Map.class);
      verify(runtimeService).startProcessInstanceByKey(anyString(), anyString(), variablesCaptor.capture());
      Map<String, Object> processVariables = (Map<String, Object>) variablesCaptor.getValue().get("processVariables");
      assertThat(processVariables.get("tarefa")).isEqualTo("READ");
      assertThat(processVariables.get("id")).isEqualTo(100L);
      assertThat(processVariables.get("payload")).isNull();
  }

  @Test
  void testStartProcess_UpdateOperation_ShouldReturn202() throws Exception {
      ProcessInstance mockProcessInstance = mock(ProcessInstance.class);
      when(mockProcessInstance.getId()).thenReturn("process-instance-update");
      when(mockProcessInstance.getBusinessKey()).thenReturn("200");
      when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(Map.class)))
              .thenReturn(mockProcessInstance);

      ProcessRequestDto request = new ProcessRequestDto("UPDATE", 200L, 
          new PayloadDto("Updated Name", "updated@example.com", 40));

      mockMvc.perform(post("/api/cadastro/process")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isAccepted());

      ArgumentCaptor<Map> variablesCaptor = ArgumentCaptor.forClass(Map.class);
      verify(runtimeService).startProcessInstanceByKey(anyString(), anyString(), variablesCaptor.capture());
      Map<String, Object> processVariables = (Map<String, Object>) variablesCaptor.getValue().get("processVariables");
      assertThat(processVariables.get("tarefa")).isEqualTo("UPDATE");
      assertThat(processVariables.get("id")).isEqualTo(200L);
  }

  @Test
  void testStartProcess_DeleteOperation_ShouldReturn202() throws Exception {
      ProcessInstance mockProcessInstance = mock(ProcessInstance.class);
      when(mockProcessInstance.getId()).thenReturn("process-instance-delete");
      when(mockProcessInstance.getBusinessKey()).thenReturn("300");
      when(runtimeService.startProcessInstanceByKey(anyString(), anyString(), any(Map.class)))
              .thenReturn(mockProcessInstance);

      ProcessRequestDto request = new ProcessRequestDto("DELETE", 300L, null);

      mockMvc.perform(post("/api/cadastro/process")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isAccepted());

      ArgumentCaptor<Map> variablesCaptor = ArgumentCaptor.forClass(Map.class);
      verify(runtimeService).startProcessInstanceByKey(anyString(), anyString(), variablesCaptor.capture());
      Map<String, Object> processVariables = (Map<String, Object>) variablesCaptor.getValue().get("processVariables");
      assertThat(processVariables.get("tarefa")).isEqualTo("DELETE");
      assertThat(processVariables.get("id")).isEqualTo(300L);
      assertThat(processVariables.get("payload")).isNull();
  }
}