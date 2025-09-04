package com.mls.workflow.camunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("identificarTarefaDelegate")
public class IdentificarTarefaDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(IdentificarTarefaDelegate.class);

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();

        LOG.info("[{}] Activity: {} - Starting IdentificarTarefaDelegate", processInstanceId, activityId);

        // The controller now wraps all parameters in a 'processVariables' map.
        // This delegate unpacks them into the main process scope.
        Map<String, Object> processVariables = (Map<String, Object>) execution.getVariable("processVariables");

        if (processVariables == null) {
            LOG.error("[{}] Activity: {} - 'processVariables' map is missing. This is a configuration error.", processInstanceId, activityId);
            // Set a failure state
            execution.setVariable("statusCode", 500);
            execution.setVariable("message", "Erro interno: Mapa de variáveis do processo não encontrado.");
            return;
        }

        LOG.debug("[{}] Activity: {} - Unpacking variables: {}", processInstanceId, activityId, processVariables.keySet());

        // Set all variables from the map into the execution
        processVariables.forEach(execution::setVariable);

        String tarefa = (String) execution.getVariable("tarefa");
        if (tarefa == null) {
            LOG.warn("[{}] Activity: {} - 'tarefa' variable is null after unpacking.", processInstanceId, activityId);
        } else {
            LOG.info("[{}] Activity: {} - Operation identified: {}", processInstanceId, activityId, tarefa);
        }

        LOG.info("[{}] Activity: {} - Finished IdentificarTarefaDelegate", processInstanceId, activityId);
    }
}