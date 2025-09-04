package com.mls.workflow.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response after a process has been successfully started.")
public class ProcessResponseDto {

    @Schema(description = "The unique identifier of the process instance that was started.", example = "c8a2b7d3-9e5a-11ee-8f6b-0242ac120002")
    private String processInstanceId;

    @Schema(description = "The business key associated with the process instance.", example = "123")
    private String businessKey;

    public ProcessResponseDto() {
    }

    public ProcessResponseDto(String processInstanceId, String businessKey) {
        this.processInstanceId = processInstanceId;
        this.businessKey = businessKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }
}
