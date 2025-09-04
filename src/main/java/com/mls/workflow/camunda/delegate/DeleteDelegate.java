package com.mls.workflow.camunda.delegate;

import com.mls.workflow.core.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deleteDelegate")
public class DeleteDelegate implements JavaDelegate {

    private final CadastroService cadastroService;

    @Autowired
    public DeleteDelegate(CadastroService cadastroService) {
        this.cadastroService = cadastroService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long id = (Long) execution.getVariable("id");

        if (id == null) {
            execution.setVariable("statusCode", 400);
            execution.setVariable("message", "ID is missing for DELETE operation.");
            return;
        }

        boolean deleted = cadastroService.delete(id);

        if (deleted) {
            execution.setVariable("statusCode", 204);
            execution.setVariable("message", "Recurso removido com sucesso");
        } else {
            execution.setVariable("statusCode", 404);
            execution.setVariable("message", "Recurso não encontrado");
        }
    }
}
