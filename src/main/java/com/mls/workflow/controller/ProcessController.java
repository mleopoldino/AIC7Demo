package com.mls.workflow.controller;

import com.mls.workflow.core.dto.ProcessRequestDto;
import com.mls.workflow.core.dto.ProcessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject; // Keep this import for Swagger examples
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // This imports Spring's @RequestBody

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
                                    schema = @Schema(implementation = ProcessResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<ProcessResponseDto> startProcess(
            @Valid @RequestBody ProcessRequestDto request) { // Correct Spring @RequestBody
        Map<String, Object> variables = new HashMap<>();
        variables.put("tarefa", request.getTarefa());
        variables.put("id", request.getId());
        variables.put("payload", request.getPayload()); // PayloadDto will be serialized as a Map

        if ("THROW_ERROR".equals(request.getTarefa())) {
            throw new RuntimeException("Simulated runtime error");
        }

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