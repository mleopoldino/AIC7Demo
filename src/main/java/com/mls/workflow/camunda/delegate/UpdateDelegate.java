package com.mls.workflow.camunda.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.PayloadDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("updateDelegate")
public class UpdateDelegate implements JavaDelegate {

    private final CadastroService cadastroService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UpdateDelegate(CadastroService cadastroService, ObjectMapper objectMapper) {
        this.cadastroService = cadastroService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long id = (Long) execution.getVariable("id");
        Object payloadObj = execution.getVariable("payload");

        if (id == null || payloadObj == null) {
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID or Payload is missing for UPDATE operation.");
            return;
        }

        PayloadDto payloadDto = objectMapper.convertValue(payloadObj, PayloadDto.class);
        CadastroDto cadastroDto = new CadastroDto(id, payloadDto.getNome(), payloadDto.getEmail(), payloadDto.getIdade());

        CadastroDto updatedCadastro = cadastroService.update(id, cadastroDto);

        if (updatedCadastro != null) {
            execution.setVariable("result", updatedCadastro);
            execution.setVariable("statusCode", 200);
            execution.setVariable("message", "Recurso atualizado com sucesso");
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
        }
    }
}
