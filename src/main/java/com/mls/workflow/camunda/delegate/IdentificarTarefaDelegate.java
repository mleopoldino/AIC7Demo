package com.mls.workflow.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Component("identificarTarefaDelegate")
public class IdentificarTarefaDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(IdentificarTarefaDelegate.class);
    private static final List<String> VALID_OPERATIONS = Arrays.asList("CREATE", "READ", "UPDATE", "DELETE");

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessBusinessKey();
        String activityId = execution.getCurrentActivityId();

        LOG.info("[{}] - Activity: {} - Starting IdentificarTarefaDelegate for process instance: {} with business key: {}",
                activityId, processInstanceId, businessKey);

        String tarefa = (String) execution.getVariable("tarefa");

        if (tarefa == null || !VALID_OPERATIONS.contains(tarefa.toUpperCase())) {
            LOG.warn("[{}] - Activity: {} - Invalid operation received: {}. Setting status code 400.",
                    activityId, processInstanceId, tarefa);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "Operação inválida. A tarefa deve ser CREATE, READ, UPDATE ou DELETE.");
        } else {
            LOG.info("[{}] - Activity: {} - Valid operation: {}. Converting to uppercase.",
                    activityId, processInstanceId, tarefa);
            execution.setVariable("tarefaNormalizada", tarefa.toUpperCase());
        }
        LOG.info("[{}] - Activity: {} - Finished IdentificarTarefaDelegate for process instance: {}",
                activityId, processInstanceId);
    }
}
