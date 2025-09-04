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

import java.util.Map;

@Component("createDelegate")
public class CreateDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CreateDelegate.class);
    private final CadastroService cadastroService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreateDelegate(CadastroService cadastroService, ObjectMapper objectMapper) {
        this.cadastroService = cadastroService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessBusinessKey();
        String activityId = execution.getCurrentActivityId();

        LOG.info("[{}] - Activity: {} - Starting CreateDelegate for process instance: {} with business key: {}",
                activityId, processInstanceId, businessKey);

        // Get payload from process variables
        Object payloadObj = execution.getVariable("payload");

        if (payloadObj == null) {
            LOG.warn("[{}] - Activity: {} - Payload is missing for CREATE operation. Setting status code 400.",
                    activityId, processInstanceId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "Payload is missing for CREATE operation.");
            LOG.info("[{}] - Activity: {} - Finished CreateDelegate for process instance: {}",
                    activityId, processInstanceId);
            return;
        }

        // Convert payload to PayloadDto, then to CadastroDto
        PayloadDto payloadDto = objectMapper.convertValue(payloadObj, PayloadDto.class);
        CadastroDto cadastroDto = new CadastroDto(null, payloadDto.getNome(), payloadDto.getEmail(), payloadDto.getIdade());

        LOG.debug("[{}] - Activity: {} - Creating new record with data: {}",
                activityId, processInstanceId, payloadDto);
        // Call service to create the record
        CadastroDto createdCadastro = cadastroService.create(cadastroDto);

        // Set process variables
        execution.setVariable("result", createdCadastro);
        execution.setVariable("statusCode", 201);
        execution.setVariable("message", "Recurso criado com sucesso");

        LOG.info("[{}] - Activity: {} - Finished CreateDelegate for process instance: {}. Created ID: {}",
                activityId, processInstanceId, createdCadastro.getId());
    }
}
