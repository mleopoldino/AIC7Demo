package com.mls.workflow.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to start a CRUD process instance.")
public class ProcessRequestDto {

    @NotBlank(message = "Operation type (tarefa) cannot be blank")
    @Schema(description = "The type of CRUD operation to perform.", example = "CREATE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tarefa;

    @Schema(description = "The ID of the resource for READ, UPDATE, DELETE operations.", example = "123")
    private Long id; // Not @NotNull here, as it's conditional based on 'tarefa'

    @Valid // Enable nested validation for PayloadDto
    @Schema(description = "The data payload for CREATE and UPDATE operations.")
    private PayloadDto payload; // Not @NotNull here, as it's conditional based on 'tarefa'

    public ProcessRequestDto() {
    }

    public ProcessRequestDto(String tarefa, Long id, PayloadDto payload) {
        this.tarefa = tarefa;
        this.id = id;
        this.payload = payload;
    }

    public String getTarefa() {
        return tarefa;
    }

    public void setTarefa(String tarefa) {
        this.tarefa = tarefa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayloadDto getPayload() {
        return payload;
    }

    public void setPayload(PayloadDto payload) {
        this.payload = payload;
    }
}
