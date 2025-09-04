package com.mls.workflow.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

class IdentificarTarefaDelegateTest {

    @Test
    void shouldUnpackProcessVariables() throws Exception {
        var exec = mock(DelegateExecution.class);
        when(exec.getProcessInstanceId()).thenReturn("test-instance-123");
        when(exec.getCurrentActivityId()).thenReturn("IdentificarTarefa");
        
        Map<String, Object> processVariables = Map.of(
            "tarefa", "CREATE",
            "id", 1L,
            "payload", Map.of("nome", "Test")
        );
        
        when(exec.getVariable("processVariables")).thenReturn(processVariables);
        when(exec.getVariable("tarefa")).thenReturn("CREATE");

        var delegate = new IdentificarTarefaDelegate();
        delegate.execute(exec);

        // Verify that all variables from the map are set
        verify(exec).setVariable("tarefa", "CREATE");
        verify(exec).setVariable("id", 1L);
        verify(exec).setVariable("payload", Map.of("nome", "Test"));
    }

    @Test
    void shouldHandleMissingProcessVariables() throws Exception {
        var exec = mock(DelegateExecution.class);
        when(exec.getProcessInstanceId()).thenReturn("test-instance-123");
        when(exec.getCurrentActivityId()).thenReturn("IdentificarTarefa");
        when(exec.getVariable("processVariables")).thenReturn(null);

        var delegate = new IdentificarTarefaDelegate();
        delegate.execute(exec);

        verify(exec).setVariable("statusCode", 500);
        verify(exec).setVariable("message", "Erro interno: Mapa de variáveis do processo não encontrado.");
    }
}