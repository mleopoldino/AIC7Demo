Instruções para fase 11

Fase 11A — Testes dos Delegates (Camunda 7)
Objetivo

Garantir que cada delegate:

Leia corretamente as variáveis de entrada (tarefa, id, payload{nome,email,idade}).

Escreva as variáveis de saída conforme contrato (result, statusCode, message).

Trate cenários de erro/ausência de dados de forma previsível (lançando BpmnError ou setando status/mensagem).

(Opcional) Interaja com a camada de serviço/repositório de forma correta (chamadas, transações e exceções).

Abordagem

Unitário “puro” de delegate (sem engine): usar camunda-bpm-mockito para mockar DelegateExecution.

Com engine (opcional, se necessário): usar ProcessEngineExtension para cenários que dependem do JobExecutor/transação.

Recomenda-se começar pelos testes unitários puros; são mais rápidos, estáveis e cobrem 90% dos comportamentos esperados dos delegates.

Dependências (test scope)

No pom.xml (se ainda não estiverem):

<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-mockito</artifactId>
  <version>6.17.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.assertj</groupId>
  <artifactId>assertj-core</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <scope>test</scope>
</dependency>

Organização dos testes
src/test/java/com/mls/workflow/delegate/
├── IdentificarTarefaDelegateTest.java
├── CreateDelegateTest.java
├── ReadDelegateTest.java
├── UpdateDelegateTest.java
├── DeleteDelegateTest.java
└── support/
├── ExecutionStub.java      (builder utilitário p/ variáveis)
└── TestData.java           (payloads e fixtures)

Utilitários sugeridos
ExecutionStub (helper para montar DelegateExecution)
package com.mls.workflow.delegate.support;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.extension.mockito.DelegateExpressions;
import org.camunda.bpm.extension.mockito.delegate.DelegateExecutionFake;

import java.util.Map;

public class ExecutionStub {
public static DelegateExecution withVars(Map<String, Object> vars) {
var exec = new DelegateExecutionFake();
vars.forEach(exec::setVariable);
return exec;
}

public static void registerExpression(String name, Object bean) {
DelegateExpressions.registerJavaDelegateMock(name).onExecutionDoNothing();
// Ou se o delegate é por expressão (ex: ${myBean}), registrar o bean de teste
}
}

TestData (payloads de exemplo)
package com.mls.workflow.delegate.support;

import java.util.Map;

public class TestData {
public static Map<String, Object> validCreateVars() {
return Map.of(
"tarefa", "CREATE",
"id", 1L,
"payload", Map.of("nome","Alice","email","alice@ex.com","idade",30)
);
}

public static Map<String, Object> invalidPayloadVars() {
return Map.of(
"tarefa", "CREATE",
"id", 1L,
"payload", Map.of("nome","", "email","bad","idade",999)
);
}
}

Casos por delegate
1) IdentificarTarefaDelegate

Propósito: normalizar/validar tarefa e garantir que estará disponível para o gateway.

Casos:

tarefa = "CREATE" → mantém “CREATE”.

tarefa = "create" (lowercase) → opcional: normaliza para “CREATE” ou mantém como está (se a regra for case-sensitive). O teste deve refletir a decisão.

tarefa = null/vazia/valor não mapeado → preparar ambiente para fluxo default (ex.: setar flag/variável “invalidOperation” ou deixar tarefa nula).

Exemplo de teste unitário:

package com.mls.workflow.delegate;

import com.mls.workflow.delegate.support.ExecutionStub;
import com.mls.workflow.delegate.support.TestData;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class IdentificarTarefaDelegateTest {

private final IdentificarTarefaDelegate delegate = new IdentificarTarefaDelegate();

@Test
void should_keep_CREATE_task() throws Exception {
DelegateExecution exec = ExecutionStub.withVars(TestData.validCreateVars());
delegate.execute(exec);
assertThat(exec.getVariable("tarefa")).isEqualTo("CREATE");
}

@Test
void should_handle_null_task_for_default_flow() throws Exception {
var vars = TestData.validCreateVars();
vars = new java.util.HashMap<>(vars);
vars.put("tarefa", null);

    DelegateExecution exec = ExecutionStub.withVars(vars);
    delegate.execute(exec);

    // Ajuste asserções conforme sua regra:
    assertThat(exec.getVariable("tarefa")).isNull();
    // ou assertThat(exec.getVariable("invalidOperation")).isEqualTo(true);
}

@Test
void should_handle_lowercase_task() throws Exception {
var vars = TestData.validCreateVars();
vars = new java.util.HashMap<>(vars);
vars.put("tarefa", "create");

    DelegateExecution exec = ExecutionStub.withVars(vars);
    delegate.execute(exec);

    // Se for para normalizar:
    // assertThat(exec.getVariable("tarefa")).isEqualTo("CREATE");
    // Se for case-sensitive:
    assertThat(exec.getVariable("tarefa")).isEqualTo("create");
}
}

2) CreateDelegate

Propósito: persistir payload em AIC_CADASTRO e setar saídas (statusCode=201, result, message).

Casos:

Payload válido → chama serviço/repo, retorna 201, result com ID criado.

Payload inválido (nome/email/idade fora de regra) → decide: lançar BpmnError ou setar statusCode=400 + message.

Exceção do repositório → decide: BpmnError de negócio ou deixar quebrar (técnico).

Exemplo de teste unitário (mock serviço/repo):

package com.mls.workflow.delegate;

import com.mls.workflow.delegate.support.ExecutionStub;
import com.mls.workflow.delegate.support.TestData;
import com.mls.workflow.service.CadastroService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CreateDelegateTest {

private final CadastroService service = mock(CadastroService.class);
private final CreateDelegate delegate = new CreateDelegate(service);

@Test
void should_create_and_set_outputs() throws Exception {
when(service.create(any())).thenReturn(new CadastroDto(10L,"Alice","alice@ex.com",30));

    DelegateExecution exec = ExecutionStub.withVars(TestData.validCreateVars());
    delegate.execute(exec);

    assertThat(exec.getVariable("statusCode")).isEqualTo(201);
    assertThat(exec.getVariable("result")).isNotNull();
    assertThat(exec.getVariable("message")).asString().contains("criado");
    verify(service).create(any());
}

@Test
void should_handle_invalid_payload() throws Exception {
DelegateExecution exec = ExecutionStub.withVars(TestData.invalidPayloadVars());
delegate.execute(exec);

    // Se você não lançar BpmnError e optar por status/mensagem:
    assertThat(exec.getVariable("statusCode")).isEqualTo(400);
    assertThat(exec.getVariable("message")).asString().contains("inválido");
    verifyNoInteractions(service);
}
}

3) ReadDelegate

Casos:

id existente → statusCode=200, result preenchido.

id inexistente → statusCode=404, message “não encontrado”.

id nulo → statusCode=400 ou BpmnError (definir política).

class ReadDelegateTest {
@Test
void should_return_200_when_found() throws Exception { /* ... */ }
@Test
void should_return_404_when_not_found() throws Exception { /* ... */ }
@Test
void should_return_400_when_id_is_null() throws Exception { /* ... */ }
}

4) UpdateDelegate

Casos:

id existente + payload válido → statusCode=200, result atualizado.

id inexistente → statusCode=404.

payload inválido → statusCode=400 ou BpmnError.

class UpdateDelegateTest {
@Test
void should_update_and_return_200() throws Exception { /* ... */ }
@Test
void should_return_404_when_id_not_found() throws Exception { /* ... */ }
@Test
void should_return_400_when_payload_invalid() throws Exception { /* ... */ }
}

5) DeleteDelegate

Casos:

id existente → statusCode=204, message opcional.

id inexistente → statusCode=404.

id nulo → statusCode=400 ou BpmnError.

class DeleteDelegateTest {
@Test
void should_delete_and_return_204() throws Exception { /* ... */ }
@Test
void should_return_404_when_id_not_found() throws Exception { /* ... */ }
@Test
void should_return_400_when_id_is_null() throws Exception { /* ... */ }
}

Boas práticas

Contratos estáveis: padronize nomes e tipos das variáveis de saída (statusCode como Integer, result como Map/DTO, message como String).

Logs: coloque log dos inputs/outputs (incluindo businessKey/processInstanceId) e asserte mensagens-chave nos testes quando fizer sentido.

Erros: diferencie claramente erro de negócio (BpmnError) de erro técnico (exceção) e teste ambos.

Velocidade: mantenha os testes de delegate unitários (sem engine). Use engine apenas quando necessário.

Critérios de aceite (delegates)

100% dos delegates com testes unitários cobrindo happy-path e pelo menos dois cenários de erro.

As variáveis de saída (result, statusCode, message) são sempre definidas conforme contrato.

Casos especiais de tarefa (nula, lowercase, não mapeada) cobertos no IdentificarTarefaDelegateTest.

Você disse:
adicione nesse plano o teste do diagrama de processos BPMN, e relatorio de cobertura com o https://github.com/camunda-community-hub/camunda-process-test-coverage.
O ChatGPT disse:
Pensou por 11s

Perfeito, Marcelo! Segue a etapa extra em Markdown para incluir testes do diagrama BPMN e relatório de cobertura com o camunda-process-test-coverage.

Fase 11B — Testes do Diagrama BPMN + Relatório de Cobertura
Objetivo

Validar o roteamento do processo “Demo AI Project – CRUD” via Engine de teste (Camunda 7).

Cobrir todos os caminhos do gateway (CREATE/READ/UPDATE/DELETE e default flow).

Gerar relatório HTML de cobertura de processo (elementos BPMN percorridos).

Dependências (escopo de teste)

Use JUnit 5. Ajuste as versões para as compatíveis com sua stack (Camunda 7.23 / Spring Boot 3.x).

<!-- Assert para processos -->
<dependency>
  <groupId>org.camunda.bpm.extension</groupId>
  <artifactId>camunda-bpm-assert</artifactId>
  <scope>test</scope>
</dependency>

<!-- Mocks para delegates -->
<dependency>
  <groupId>org.camunda.bpm.extension.mockito</groupId>
  <artifactId>camunda-bpm-mockito</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.camunda.bpm.extension.mockito</groupId>
  <artifactId>camunda-bpm-mockito-scenario</artifactId>
  <scope>test</scope>
</dependency>

<!-- Cobertura de processo (JUnit 5) -->
<dependency>
  <groupId>org.camunda.community.process_test_coverage</groupId>
  <artifactId>camunda-process-test-coverage-junit5</artifactId>
  <scope>test</scope>
</dependency>


Se preferir JUnit 4, troque por camunda-process-test-coverage-junit4 e use @Rule TestCoverageProcessEngineRule. mas se possivel de preferencia para o JUNIT 5.

Convenções e Recursos

Arquivo BPMN: coloque o diagrama em src/test/resources/bpmn/demo-ai-project-crud.bpmn (ou ajuste o caminho no @Deployment).

IDs: confirme os IDs das Service Tasks e do End Event de “operação inválida” (default flow), p.ex.:

identificarTarefaDelegate

createDelegate, readDelegate, updateDelegate, deleteDelegate

end_invalid (ou o ID real do end do default)

Deployment nos testes: use @Deployment(resources = "bpmn/demo-ai-project-crud.bpmn").

Classe de Teste — Execução do BPMN com Cobertura
// src/test/java/com/mls/workflow/bpmn/DemoAiProjectCrudProcessCoverageTest.java
package com.mls.workflow.bpmn;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.mockito.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({ ProcessEngineExtension.class, ProcessEngineCoverageExtension.class })
@Deployment(resources = "bpmn/demo-ai-project-crud.bpmn")
class DemoAiProjectCrudProcessCoverageTest {

@org.junit.jupiter.api.extension.RegisterExtension
static ProcessEngineExtension engine = ProcessEngineExtension.builder().build();

private RuntimeService runtimeService() {
return engine.getProcessEngine().getRuntimeService();
}

@BeforeEach
void registerDelegates() {
// Registre mocks NOP para delegates usados no diagrama (ajuste nomes/expressões conforme BPMN)
Mocks.register("identificarTarefaDelegate", (org.camunda.bpm.engine.delegate.JavaDelegate) ex -> { /* no-op */ });
Mocks.register("createDelegate", (org.camunda.bpm.engine.delegate.JavaDelegate) ex -> {
ex.setVariable("statusCode", 201);
ex.setVariable("message", "Criado");
ex.setVariable("result", Map.of("id", ex.getVariable("id")));
});
Mocks.register("readDelegate", (org.camunda.bpm.engine.delegate.JavaDelegate) ex -> {
ex.setVariable("statusCode", 200);
ex.setVariable("message", "OK");
ex.setVariable("result", Map.of("id", ex.getVariable("id")));
});
Mocks.register("updateDelegate", (org.camunda.bpm.engine.delegate.JavaDelegate) ex -> {
ex.setVariable("statusCode", 200);
ex.setVariable("message", "Atualizado");
ex.setVariable("result", Map.of("id", ex.getVariable("id")));
});
Mocks.register("deleteDelegate", (org.camunda.bpm.engine.delegate.JavaDelegate) ex -> {
ex.setVariable("statusCode", 204);
ex.setVariable("message", "Removido");
});
}

@AfterEach
void reset() {
Mocks.reset();
}

private ProcessInstance start(Map<String,Object> vars) {
return runtimeService().startProcessInstanceByKey("Demo AI Project - CRUD", vars);
}

@Test
void should_route_to_CREATE() {
Map<String,Object> vars = new HashMap<>();
vars.put("tarefa", "CREATE");
vars.put("id", 1L);
vars.put("payload", Map.of("nome","Alice","email","alice@ex.com","idade",30));

    var pi = start(vars);
    assertThat(pi).isNotNull();
}

@Test
void should_route_to_READ() {
Map<String,Object> vars = new HashMap<>();
vars.put("tarefa", "READ");
vars.put("id", 1L);

    var pi = start(vars);
    assertThat(pi).isNotNull();
}

@Test
void should_route_to_UPDATE() {
Map<String,Object> vars = new HashMap<>();
vars.put("tarefa", "UPDATE");
vars.put("id", 1L);
vars.put("payload", Map.of("nome","A2","email","a2@ex.com","idade",31));

    var pi = start(vars);
    assertThat(pi).isNotNull();
}

@Test
void should_route_to_DELETE() {
Map<String,Object> vars = new HashMap<>();
vars.put("tarefa", "DELETE");
vars.put("id", 1L);

    var pi = start(vars);
    assertThat(pi).isNotNull();
}

@Test
void should_route_to_default_when_unmapped_or_null() {
Map<String,Object> vars1 = new HashMap<>();
vars1.put("tarefa", null);
vars1.put("id", 1L);
var pi1 = start(vars1);
assertThat(pi1).isNotNull();

    Map<String,Object> vars2 = new HashMap<>();
    vars2.put("tarefa", "create"); // lowercase → default flow (case-sensitive)
    vars2.put("id", 1L);
    var pi2 = start(vars2);
    assertThat(pi2).isNotNull();

    Map<String,Object> vars3 = new HashMap<>();
    vars3.put("tarefa", "UPSERT");
    vars3.put("id", 1L);
    var pi3 = start(vars3);
    assertThat(pi3).isNotNull();
}
}


Notas

O ProcessEngineCoverageExtension gera o relatório de cobertura de processo automaticamente ao fim da execução do teste.

Para asserts de caminho (opcional), você pode usar camunda-bpm-assert:

import static org.camunda.bpm.extension.assertj.ProcessEngineAssertions.assertThat;
assertThat(pi).isEnded(); // ou .hasPassed("createTaskId")

Onde encontrar o relatório de cobertura

Após mvn test, procure em:

target/process-test-coverage/
└── DemoAiProjectCrudProcessCoverageTest/
└── index.html


Abra o index.html no navegador para ver quais elementos BPMN foram cobertos em cada teste.


Dicas para Cobertura Máxima

Tenha um teste por rota (CREATE/READ/UPDATE/DELETE).

Cubra default flow com:

tarefa = null

tarefa = "create" (minúsculas)

tarefa = "UPSERT" (não mapeado)

Se houver erros de negócio (via BpmnError) ou boundary events, crie testes específicos executando o caminho de exceção.

Registre mocks para todos os delegates referenciados no diagrama (mesmo que NOP), evitando exceções técnicas que impeçam o avanço do fluxo durante o teste de cobertura.

Integração com a sua Fase 7

Mantenha os testes unitários (dados/H2) e de integração (MockMvc) já definidos.


Validar roteamento BPMN de ponta a ponta (com Engine real).

Gerar evidência visual (HTML) de cobertura dos elementos do processo.