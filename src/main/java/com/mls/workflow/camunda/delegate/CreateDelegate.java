package com.mls.workflow.camunda.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.PayloadDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("createDelegate")
public class CreateDelegate implements JavaDelegate {

    private final CadastroService cadastroService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CreateDelegate(CadastroService cadastroService, ObjectMapper objectMapper) {
        this.cadastroService = cadastroService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Get payload from process variables
        Object payloadObj = execution.getVariable("payload");

        if (payloadObj == null) {
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "Payload is missing for CREATE operation.");
            return;
        }

        // Convert payload to PayloadDto, then to CadastroDto
        PayloadDto payloadDto = objectMapper.convertValue(payloadObj, PayloadDto.class);
        CadastroDto cadastroDto = new CadastroDto(null, payloadDto.getNome(), payloadDto.getEmail(), payloadDto.getIdade());

        // Call service to create the record
        CadastroDto createdCadastro = cadastroService.create(cadastroDto);

        // Set process variables
        execution.setVariable("result", createdCadastro);
        execution.setVariable("statusCode", 201);
        execution.setVariable("message", "Recurso criado com sucesso");
    }
}
