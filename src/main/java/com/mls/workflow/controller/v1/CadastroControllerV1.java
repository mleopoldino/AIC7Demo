package com.mls.workflow.controller.v1;

import com.mls.workflow.core.dto.CadastroDto;
import com.mls.workflow.core.dto.v1.CreateRequestDto;
import com.mls.workflow.core.dto.v1.UpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cadastro")
@Tag(name = "Cadastro V1", description = "API RESTful para operações de cadastro orquestradas por BPMN")
@Validated
@RequiredArgsConstructor
public class CadastroControllerV1 {

    private final RuntimeService runtimeService;

    private static final String PROCESS_DEFINITION_KEY = "DemoAIProjectCRUDProcess";

    @PostMapping
    @Operation(summary = "Cria um novo registro de cadastro", responses = {
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    public ResponseEntity<CadastroDto> create(@Valid @RequestBody CreateRequestDto createRequest) {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("tarefa", "CREATE");
        processVariables.put("payload", createRequest);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, UUID.randomUUID().toString(), Variables.createVariables().putValue("processVariables", processVariables));

        VariableMap variables = runtimeService.getVariablesTyped(processInstance.getProcessInstanceId(), false);
        CadastroDto result = variables.getValue("result", CadastroDto.class);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um registro por ID", responses = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<CadastroDto> read(@PathVariable Long id) {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("tarefa", "READ");
        processVariables.put("id", id);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, id.toString(), Variables.createVariables().putValue("processVariables", processVariables));

        VariableMap variables = runtimeService.getVariablesTyped(processInstance.getProcessInstanceId(), false);
        Integer statusCode = variables.getValue("statusCode", Integer.class);

        if (statusCode != null && statusCode == 404) {
            return ResponseEntity.notFound().build();
        }
        
        CadastroDto result = variables.getValue("result", CadastroDto.class);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um registro existente", responses = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<CadastroDto> update(@PathVariable Long id, @Valid @RequestBody UpdateRequestDto updateRequest) {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("tarefa", "UPDATE");
        processVariables.put("id", id);
        processVariables.put("payload", updateRequest);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, id.toString(), Variables.createVariables().putValue("processVariables", processVariables));

        VariableMap variables = runtimeService.getVariablesTyped(processInstance.getProcessInstanceId(), false);
        Integer statusCode = variables.getValue("statusCode", Integer.class);

        if (statusCode != null && statusCode == 404) {
            return ResponseEntity.notFound().build();
        }
        
        CadastroDto result = variables.getValue("result", CadastroDto.class);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um registro existente", responses = {
            @ApiResponse(responseCode = "204", description = "Registro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("tarefa", "DELETE");
        processVariables.put("id", id);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, id.toString(), Variables.createVariables().putValue("processVariables", processVariables));

        VariableMap variables = runtimeService.getVariablesTyped(processInstance.getProcessInstanceId(), false);
        Integer statusCode = variables.getValue("statusCode", Integer.class);

        if (statusCode != null && statusCode == 404) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}