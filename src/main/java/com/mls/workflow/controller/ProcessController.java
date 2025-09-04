package com.mls.workflow.controller;

import com.mls.workflow.core.dto.ProcessRequestDto;
import com.mls.workflow.core.dto.ProcessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cadastro")
@Tag(name = "Process Controller", description = "Endpoints for starting and managing BPMN processes.")
public class ProcessController {

    private final RuntimeService runtimeService;

    @Autowired
    public ProcessController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/process")
    @Operation(summary = "Start a CRUD process instance",
            description = "Starts a new instance of the 'Demo AI Project - CRUD' BPMN process.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Process started successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProcessResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<ProcessResponseDto> startProcess(
            @Valid
            @RequestBody(
                    description = "Request to start a CRUD process instance",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Create Example",
                                            summary = "Example for CREATE operation",
                                            value = "{\"tarefa\": \"CREATE\", \"payload\": {\"nome\": \"Novo Registro\", \"email\": \"novo@example.com\", \"idade\": 30}}"
                                    ),
                                    @ExampleObject(
                                            name = "Read Example",
                                            summary = "Example for READ operation",
                                            value = "{\"tarefa\": \"READ\", \"id\": 1}"
                                    ),
                                    @ExampleObject(
                                            name = "Update Example",
                                            summary = "Example for UPDATE operation",
                                            value = "{\"tarefa\": \"UPDATE\", \"id\": 1, \"payload\": {\"nome\": \"Registro Atualizado\", \"email\": \"atualizado@example.com\", \"idade\": 35}}"
                                    ),
                                    @ExampleObject(
                                            name = "Delete Example",
                                            summary = "Example for DELETE operation",
                                            value = "{\"tarefa\": \"DELETE\", \"id\": 1}"
                                    ),
                                    @ExampleObject(
                                            name = "Invalid Operation Example",
                                            summary = "Example for an invalid operation (should trigger default flow)",
                                            value = "{\"tarefa\": \"UPSERT\", \"id\": 999, \"payload\": {\"nome\": \"Teste Invalido\", \"email\": \"invalido@example.com\", \"idade\": 10}}"
                                    )
                            }
                    )
            )
            ProcessRequestDto request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("tarefa", request.getTarefa());
        variables.put("id", request.getId());
        variables.put("payload", request.getPayload()); // PayloadDto will be serialized as a Map

        String businessKey = request.getId() != null ? String.valueOf(request.getId()) : null;

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "DemoAIProjectCRUDProcess", // Process Definition Key (ID from BPMN file)
                businessKey,
                variables
        );

        ProcessResponseDto response = new ProcessResponseDto(processInstance.getId(), processInstance.getBusinessKey());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
