package com.mls.workflow.controller;

import com.mls.workflow.core.dto.ProcessRequestDto;
import com.mls.workflow.core.dto.ProcessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cadastro")
@Tag(name = "Process Controller", description = "Endpoints for starting and managing BPMN processes.")
public class ProcessController {

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
    public ResponseEntity<ProcessResponseDto> startProcess(@RequestBody ProcessRequestDto request) {
        // This is a stub. The actual process starting logic will be implemented in Phase 6.
        // For now, we return a dummy response.
        String processInstanceId = UUID.randomUUID().toString();
        String businessKey = request.getId() != null ? String.valueOf(request.getId()) : null;

        ProcessResponseDto response = new ProcessResponseDto(processInstanceId, businessKey);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
