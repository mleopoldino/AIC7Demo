package com.mls.workflow.camunda.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.v1.CreateRequestDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("createDelegate")
public class CreateDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CreateDelegate.class);
    private final CadastroService cadastroService;
    private final ObjectMapper objectMapper;

    public CreateDelegate(CadastroService cadastroService, ObjectMapper objectMapper) {
        this.cadastroService = cadastroService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        LOG.info("[{}] Activity: {} - Starting CreateDelegate", processInstanceId, activityId);

        Object payloadObj = execution.getVariable("payload");

        if (payloadObj == null) {
            LOG.warn("[{}] Activity: {} - Payload is missing for CREATE operation.", processInstanceId, activityId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "Payload is missing for CREATE operation.");
            return;
        }
        
        CreateRequestDto payload = objectMapper.convertValue(payloadObj, CreateRequestDto.class);

        // Map to the service DTO
        CadastroDto cadastroToCreate = new CadastroDto();
        cadastroToCreate.setNome(payload.getNome());
        cadastroToCreate.setEmail(payload.getEmail());
        cadastroToCreate.setIdade(payload.getIdade());

        LOG.debug("[{}] Activity: {} - Creating new record with data: {}", processInstanceId, activityId, cadastroToCreate);
        
        CadastroDto createdCadastro = cadastroService.create(cadastroToCreate);

        execution.setVariable("result", createdCadastro);
        execution.setVariable("statusCode", 201);
        execution.setVariable("message", "Recurso criado com sucesso");

        LOG.info("[{}] Activity: {} - Finished CreateDelegate. Created ID: {}", processInstanceId, activityId, createdCadastro.getId());
    }
}
