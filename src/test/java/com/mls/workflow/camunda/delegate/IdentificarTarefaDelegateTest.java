package com.mls.workflow.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class IdentificarTarefaDelegateTest {

    @Test
    void shouldSetVariables() throws Exception {
        var exec = mock(DelegateExecution.class);
        when(exec.getVariable("tarefa")).thenReturn("CREATE");

        var delegate = new IdentificarTarefaDelegate();
        delegate.execute(exec);

        verify(exec).setVariable(eq("tarefaNormalizada"), eq("CREATE"));
    }
}