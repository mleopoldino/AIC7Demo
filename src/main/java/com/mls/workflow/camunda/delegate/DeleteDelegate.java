package com.mls.workflow.camunda.delegate;

import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("deleteDelegate")
public class DeleteDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteDelegate.class);
    private final CadastroService cadastroService;

    @Autowired
    public DeleteDelegate(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessBusinessKey();
        String activityId = execution.getCurrentActivityId();

        LOG.info("[{}] - Activity: {} - Starting DeleteDelegate for process instance: {} with business key: {}",
                activityId, processInstanceId, businessKey);

        Long id = (Long) execution.getVariable("id");

        if (id == null) {
            LOG.warn("[{}] - Activity: {} - ID is missing for DELETE operation. Setting status code 400.",
                    activityId, processInstanceId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID is missing for DELETE operation.");
            LOG.info("[{}] - Activity: {} - Finished DeleteDelegate for process instance: {}",
                    activityId, processInstanceId);
            return;
        }

        LOG.debug("[{}] - Activity: {} - Deleting record with ID: {}",
                activityId, processInstanceId, id);
        boolean deleted = cadastroService.delete(id);

        if (deleted) {
            execution.setVariable("statusCode", 204);
            execution.setVariable("message", "Recurso removido com sucesso");
            LOG.info("[{}] - Activity: {} - Finished DeleteDelegate for process instance: {}. Record deleted.",
                    activityId, processInstanceId);
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
            LOG.warn("[{}] - Activity: {} - Finished DeleteDelegate for process instance: {}. Record with ID {} not found.",
                    activityId, processInstanceId, id);
        }
    }
}
