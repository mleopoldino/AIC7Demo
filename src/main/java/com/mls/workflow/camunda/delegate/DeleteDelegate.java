package com.mls.workflow.camunda.delegate;

import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("deleteDelegate")
public class DeleteDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteDelegate.class);
    private final CadastroService cadastroService;

    public DeleteDelegate(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        LOG.info("[{}] Activity: {} - Starting DeleteDelegate", processInstanceId, activityId);

        Long id = (Long) execution.getVariable("id");

        if (id == null) {
            LOG.warn("[{}] Activity: {} - ID is missing for DELETE operation.", processInstanceId, activityId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID is missing for DELETE operation.");
            return;
        }

        LOG.debug("[{}] Activity: {} - Deleting record with ID: {}", processInstanceId, activityId, id);
        boolean deleted = cadastroService.delete(id);

        if (deleted) {
            execution.setVariable("statusCode", 204);
            execution.setVariable("message", "Recurso removido com sucesso");
            LOG.info("[{}] Activity: {} - Finished DeleteDelegate. Record deleted.", processInstanceId, activityId);
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
            LOG.warn("[{}] Activity: {} - Finished DeleteDelegate. Record with ID {} not found.", processInstanceId, activityId, id);
        }
    }
}
