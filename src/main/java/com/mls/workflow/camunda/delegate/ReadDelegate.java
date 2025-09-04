package com.mls.workflow.camunda.delegate;

import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("readDelegate")
public class ReadDelegate implements JavaDelegate {

    private final CadastroService cadastroService;

    @Autowired
    public ReadDelegate(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long id = (Long) execution.getVariable("id");

        if (id == null) {
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID is missing for READ operation.");
            return;
        }

        Optional<CadastroDto> cadastroOptional = cadastroService.read(id);

        if (cadastroOptional.isPresent()) {
            execution.setVariable("result", cadastroOptional.get());
            execution.setVariable("statusCode", 200);
            execution.setVariable("message", "Recurso encontrado");
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
        }
    }
}
