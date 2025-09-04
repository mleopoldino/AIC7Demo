package com.mls.workflow;

import com.mls.workflow.controller.ProcessController;
import com.mls.workflow.core.dto.ProcessRequestDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProcessController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WebMvcSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RuntimeService runtimeService;

    @Test
    public void testCreateEndpointWithMockMvc() throws Exception {
        // Mock ProcessInstance
        ProcessInstance mockProcessInstance = org.mockito.Mockito.mock(ProcessInstance.class);
        when(mockProcessInstance.getId()).thenReturn("mock-process-id");
        when(mockProcessInstance.getBusinessKey()).thenReturn("mock-business-key");

        when(runtimeService.startProcessInstanceByKey(anyString(), any(), anyMap()))
                .thenReturn(mockProcessInstance);

        String requestJson = "{\"tarefa\": \"CREATE\", \"payload\": {\"nome\": \"João\", \"email\": \"joao@teste.com\", \"idade\": 30}}";

        mockMvc.perform(post("/api/cadastro/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isAccepted());
    }
}