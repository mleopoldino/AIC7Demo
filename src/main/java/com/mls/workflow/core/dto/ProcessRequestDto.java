package com.mls.workflow.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to start a CRUD process instance.")
public class ProcessRequestDto {

    @Schema(description = "The type of CRUD operation to perform.", example = "CREATE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tarefa;

    @Schema(description = "The ID of the resource for READ, UPDATE, DELETE operations.", example = "123")
    private Long id;

    @Schema(description = "The data payload for CREATE and UPDATE operations.")
    private PayloadDto payload;

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