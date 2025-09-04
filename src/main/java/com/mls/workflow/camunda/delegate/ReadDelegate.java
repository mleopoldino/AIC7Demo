package com.mls.workflow.camunda.delegate;

import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("readDelegate")
public class ReadDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ReadDelegate.class);
    private final CadastroService cadastroService;

    public ReadDelegate(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        LOG.info("[{}] Activity: {} - Starting ReadDelegate", processInstanceId, activityId);

        Long id = (Long) execution.getVariable("id");

        if (id == null) {
            LOG.warn("[{}] Activity: {} - ID is missing for READ operation.", processInstanceId, activityId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID is missing for READ operation.");
            return;
        }

        LOG.debug("[{}] Activity: {} - Reading record with ID: {}", processInstanceId, activityId, id);
        Optional<CadastroDto> cadastroOptional = cadastroService.read(id);

        if (cadastroOptional.isPresent()) {
            execution.setVariable("result", cadastroOptional.get());
            execution.setVariable("statusCode", 200);
            execution.setVariable("message", "Recurso encontrado");
            LOG.info("[{}] Activity: {} - Finished ReadDelegate. Record found.", processInstanceId, activityId);
        } else {
            // Set result to null explicitly for clarity in the process variables
            execution.setVariable("result", null);
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
            LOG.warn("[{}] Activity: {} - Finished ReadDelegate. Record with ID {} not found.", processInstanceId, activityId, id);
        }
    }
}
