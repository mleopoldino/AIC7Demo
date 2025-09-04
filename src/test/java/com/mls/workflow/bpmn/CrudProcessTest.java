/*
package com.mls.workflow.bpmn;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.junit5.CamundaProcessEngineExtension;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.assertj.core.api.Assertions.assertThat;

class CrudProcessTest {

    @RegisterExtension
    static CamundaProcessEngineExtension extension = CamundaProcessEngineExtension.builder().build();

    @Test
    @Deployment(resources = "bpmn/process.bpmn")
    void shouldPassThroughCreatePath() {
        Map<String, Object> vars = Map.of(
                "tarefa", "CREATE",
                "payload", Map.of("nome","N","email","n@example.com","idade",1)
        );

        ProcessInstance pi = runtimeService().startProcessInstanceByKey("DemoAIProjectCRUDProcess", vars);

        assertThat(pi).isNotNull();
        assertThat(processInstanceQuery().processInstanceId(pi.getId()).count()).isEqualTo(1);

        // Exemplo de asserções de caminho (adapte IDs reais do seu BPMN)
        assertThat(pi).isStarted();
        assertThat(task()).isNull(); // se o fluxo é 100% service tasks
        assertThat(pi).isEnded();
    }
}
*/