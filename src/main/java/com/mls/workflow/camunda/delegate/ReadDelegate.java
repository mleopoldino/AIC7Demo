package com.mls.workflow.camunda.delegate;

import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Component("readDelegate")
public class ReadDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ReadDelegate.class);
    private final CadastroService cadastroService;

    @Autowired
    public ReadDelegate(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessBusinessKey();
        String activityId = execution.getCurrentActivityId();

        LOG.info("[{}] - Activity: {} - Starting ReadDelegate for process instance: {} with business key: {}",
                activityId, processInstanceId, businessKey);

        Long id = (Long) execution.getVariable("id");

        if (id == null) {
            LOG.warn("[{}] - Activity: {} - ID is missing for READ operation. Setting status code 400.",
                    activityId, processInstanceId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID is missing for READ operation.");
            LOG.info("[{}] - Activity: {} - Finished ReadDelegate for process instance: {}",
                    activityId, processInstanceId);
            return;
        }

        LOG.debug("[{}] - Activity: {} - Reading record with ID: {}",
                activityId, processInstanceId, id);
        Optional<CadastroDto> cadastroOptional = cadastroService.read(id);

        if (cadastroOptional.isPresent()) {
            execution.setVariable("result", cadastroOptional.get());
            execution.setVariable("statusCode", 200);
            execution.setVariable("message", "Recurso encontrado");
            LOG.info("[{}] - Activity: {} - Finished ReadDelegate for process instance: {}. Record found.",
                    activityId, processInstanceId);
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
            LOG.warn("[{}] - Activity: {} - Finished ReadDelegate for process instance: {}. Record with ID {} not found.",
                    activityId, processInstanceId, id);
        }
    }
}
