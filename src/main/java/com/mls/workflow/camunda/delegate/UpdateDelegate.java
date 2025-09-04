package com.mls.workflow.camunda.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.v1.UpdateRequestDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("updateDelegate")
public class UpdateDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateDelegate.class);
    private final CadastroService cadastroService;
    private final ObjectMapper objectMapper;

    public UpdateDelegate(CadastroService cadastroService, ObjectMapper objectMapper) {
        this.cadastroService = cadastroService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String processInstanceId = execution.getProcessInstanceId();
        String activityId = execution.getCurrentActivityId();
        LOG.info("[{}] Activity: {} - Starting UpdateDelegate", processInstanceId, activityId);

        Long id = (Long) execution.getVariable("id");
        Object payloadObj = execution.getVariable("payload");

        if (id == null || payloadObj == null) {
            LOG.warn("[{}] Activity: {} - ID or Payload is missing for UPDATE operation.", processInstanceId, activityId);
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID or Payload is missing for UPDATE operation.");
            return;
        }
        
        UpdateRequestDto payload = objectMapper.convertValue(payloadObj, UpdateRequestDto.class);

        // Fetch the existing record
        Optional<CadastroDto> existingCadastroOpt = cadastroService.read(id);

        if (existingCadastroOpt.isEmpty()) {
            LOG.warn("[{}] Activity: {} - Record with ID {} not found for update.", processInstanceId, activityId, id);
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado para atualização.");
            return;
        }

        CadastroDto existingCadastro = existingCadastroOpt.get();

        // Apply changes from DTO
        if (payload.getNome() != null) {
            existingCadastro.setNome(payload.getNome());
        }
        if (payload.getEmail() != null) {
            existingCadastro.setEmail(payload.getEmail());
        }
        if (payload.getIdade() != null) {
            existingCadastro.setIdade(payload.getIdade());
        }

        LOG.debug("[{}] Activity: {} - Updating record ID {} with data: {}", processInstanceId, activityId, id, existingCadastro);
        
        CadastroDto updatedCadastro = cadastroService.update(id, existingCadastro);

        execution.setVariable("result", updatedCadastro);
        execution.setVariable("statusCode", 200);
        execution.setVariable("message", "Recurso atualizado com sucesso");

        LOG.info("[{}] Activity: {} - Finished UpdateDelegate. Record updated.", processInstanceId, activityId);
    }
}
