package com.mls.workflow.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("identificarTarefaDelegate")
public class IdentificarTarefaDelegate implements JavaDelegate {

    private static final List<String> VALID_OPERATIONS = Arrays.asList("CREATE", "READ", "UPDATE", "DELETE");

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String tarefa = (String) execution.getVariable("tarefa");

        if (tarefa == null || !VALID_OPERATIONS.contains(tarefa.toUpperCase())) {
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "Operação inválida. A tarefa deve ser CREATE, READ, UPDATE ou DELETE.");
            // Optionally, throw a BpmnError here if you want to model this as a business error
            // throw new BpmnError("invalidOperation", "Operação inválida.");
        } else {
            // Ensure the 'tarefa' variable is in uppercase for consistent comparison in the gateway
            execution.setVariable("tarefa", tarefa.toUpperCase());
            // No need to set statusCode/message if valid, as the process will continue
        }
    }
}
