package com.mls.workflow.controller;

import com.mls.workflow.core.dto.ProcessRequestDto;
import com.mls.workflow.core.dto.ProcessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cadastro")
@Tag(name = "Process Controller (Legacy)", description = "Endpoints for starting and managing BPMN processes.")
@Deprecated(since = "1.1.0", forRemoval = true)
public class ProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);
    private final RuntimeService runtimeService;

    @Autowired
    public ProcessController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/process")
    @Operation(summary = "Execute CRUD operations via BPMN process (DEPRECATED)",
            description = "DEPRECATED: Use the new /api/v1/cadastro endpoints. This endpoint will be removed in a future version. Executes CREATE, READ, UPDATE or DELETE operations via BPMN workflow. The operation type is determined by the 'tarefa' field in the request payload.",
            deprecated = true,
            requestBody = @RequestBody(
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProcessRequestDto.class),
                    examples = {
                        @ExampleObject(name = "CREATE", summary = "Create new record",
                            value = "{\n  \"tarefa\": \"CREATE\",\n  \"payload\": {\n    \"nome\": \"João Silva\",\n    \"email\": \"joao@teste.com\",\n    \"idade\": 30\n  }\n}"),
                        @ExampleObject(name = "READ", summary = "Read record by ID",
                            value = "{\n  \"tarefa\": \"read\",\n  \"id\": 1\n}"),
                        @ExampleObject(name = "UPDATE", summary = "Update existing record",
                            value = "{\n  \"tarefa\": \"UPDATE\",\n  \"id\": 1,\n  \"payload\": {\n    \"nome\": \"João Silva Santos\",\n    \"email\": \"joao.santos@teste.com\",\n    \"idade\": 35\n  }\n}"),
                        @ExampleObject(name = "DELETE", summary = "Delete record by ID",
                            value = "{\n  \"tarefa\": \"DELETE\",\n  \"id\": 1\n}")
                    }
                )
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Process started successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProcessResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ProcessResponseDto> startProcess(
            @Valid @org.springframework.web.bind.annotation.RequestBody ProcessRequestDto request) {

        LOG.warn("The /api/cadastro/process endpoint is deprecated and will be removed in a future version. Please migrate to the new /api/v1/cadastro endpoints.");

        Map<String, Object> variables = new HashMap<>();
        variables.put("tarefa", request.getTarefa());
        variables.put("id", request.getId());
        variables.put("payload", request.getPayload());

        // This logic is kept for backward compatibility, but the new V1 controller is preferred.
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("processVariables", variables);


        String businessKey;
        if ("CREATE".equals(request.getTarefa()) && request.getId() == null) {
            businessKey = java.util.UUID.randomUUID().toString();
        } else {
            businessKey = request.getId() != null ? String.valueOf(request.getId()) : null;
        }

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "DemoAIProjectCRUDProcess",
                businessKey,
                processVariables
        );

        ProcessResponseDto response = new ProcessResponseDto(processInstance.getId(), processInstance.getBusinessKey());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
