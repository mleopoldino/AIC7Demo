package com.mls.workflow.camunda.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.PayloadDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("updateDelegate")
public class UpdateDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateDelegate.class);
    private final CadastroService cadastroService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UpdateDelegate(CadastroService cadastroService, ObjectMapper objectMapper) {
        this.cadastroService = cadastroService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessBusinessKey();
        String activityId = execution.getCurrentActivityId();

        LOG.info("[{}] - Activity: {} - Starting UpdateDelegate for process instance: {} with business key: {}",
                activityId, processInstanceId, businessKey);

        Long id = (Long) execution.getVariable("id");
        Object payloadObj = execution.getVariable("payload");

        if (id == null || payloadObj == null) {
            LOG.warn("[{}] - Activity: {} - ID or Payload is missing for UPDATE operation. Setting status code 400.",
                    activityId, processInstanceId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID or Payload is missing for UPDATE operation.");
            LOG.info("[{}] - Activity: {} - Finished UpdateDelegate for process instance: {}",
                    activityId, processInstanceId);
            return;
        }

        PayloadDto payloadDto = objectMapper.convertValue(payloadObj, PayloadDto.class);
        CadastroDto cadastroDto = new CadastroDto(id, payloadDto.getNome(), payloadDto.getEmail(), payloadDto.getIdade());

        LOG.debug("[{}] - Activity: {} - Updating record with ID: {} and data: {}",
                activityId, processInstanceId, id, payloadDto);
        CadastroDto updatedCadastro = cadastroService.update(id, cadastroDto);

        if (updatedCadastro != null) {
            execution.setVariable("result", updatedCadastro);
            execution.setVariable("statusCode", 200);
            execution.setVariable("message", "Recurso atualizado com sucesso");
            LOG.info("[{}] - Activity: {} - Finished UpdateDelegate for process instance: {}. Record updated.",
                    activityId, processInstanceId);
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
            LOG.warn("[{}] - Activity: {} - Finished UpdateDelegate for process instance: {}. Record with ID {} not found.",
                    activityId, processInstanceId, id);
        }
    }
}
