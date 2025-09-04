# CRUD orquestrado por BPMN (Camunda 7 + Spring Boot + H2)

## Escopo
- Expor um endpoint REST que inicia o processo **Demo AI Project - CRUD**.
- O processo BPMN possui:
    - Service Task de identificação: `${identificarTarefaDelegate}`
    - Gateway exclusivo com condições `${tarefa == "CREATE"}`, `${tarefa == "READ"}`, `${tarefa == "UPDATE"}`, `${tarefa == "DELETE"}`
    - Service Tasks: `${createDelegate}`, `${readDelegate}`, `${updateDelegate}`, `${deleteDelegate}`
    - Fluxo default → operação inválida.
- Banco de dados: **H2**, tabela **AIC_CADASTRO** (ID, NOME, EMAIL, IDADE).
- Variáveis de processo:
    - Entrada: `tarefa`, `id`, `payload{nome,email,idade}`
    - Saída: `result`, `statusCode`, `message`
- Documentação de API: **Swagger/OpenAPI** integrado ao projeto.

---

## Fase 0 — Preparação do ambiente ✅ **COMPLETA**
- ✅ Verificar toolchain (JDK 21, Maven configurados).
- ✅ Confirmar `spring-boot 3.3.0` + `camunda 7.23` no `pom.xml`.
- ✅ `CLAUDE.md` presente no repositório.
- ✅ `mvn clean package` roda sem erros.

- Swagger / OpenAPI
  - Dependência: `org.springdoc:springdoc-openapi-starter-webmvc-ui`.
  - URL de acesso: `/swagger-ui/index.html`.
  - incluir no escopo um lembrete para habilitar o endpoint de docs JSON (/v3/api-docs), que é o que alimenta o Swagger UI.
  - Incluir schemas e exemplos de requests/responses para os principais cenários (202, 4xx, 5xx).

- Validação
  - Adotar `spring-boot-starter-validation`.
  - Anotações recomendadas:
      - `@NotBlank` em `payload.nome` e `payload.email`.
      - `@Email` em `payload.email`.
      - `@Min(0)` em `payload.idade`.

- Process Key
  - Confirmar que o atributo `id` 
  - Acrescente businessKey com o `id` 
  - reforçar a diferença entre id (usado no startProcessInstanceByKey) e name (visível no Cockpit).
  - Se necessário, renomear para `demo-ai-project-crud` e manter o atributo `name` como **"Demo AI Project - CRUD"**.
 
- DB & Scripts
  - Configurar H2 como **file-based** para persistência entre reinícios em ambiente de dev.
  - Adicionar `data.sql` para *seed* de registros em desenvolvimento.
  - Colocar uso de **Flyway/Liquibase** como *tech debt* para versões futuras.

- Transação & Erros
  - Garantir transação (`@Transactional`) na camada de serviço.
  - Definir claramente quando lançar **BPMN Error** (erros de negócio) e quando deixar como erro técnico (boundary event).

- Testes
  - Definir matriz de casos de teste para cada operação CRUD (incluindo cenários inválidos).
  - Estabelecer critério de aceite de cobertura mínima e cenários obrigatórios.

- Observabilidade
  - Adicionar logs contendo `correlationId` e `processInstanceId` em cada delegate e nas entradas e saidas das tarefas
  - Adicione o BusinessKey

## Fase 1 — Banco de dados
- Criar `schema.sql` em `src/main/resources` para a tabela **AIC_CADASTRO**:
    - `ID` (auto-incremento, PK), `NOME`, `EMAIL`, `IDADE`.
- Configurar `spring.sql.init.mode=always` no `application.yaml`.
- Validar no H2 console (`/h2-console`) que a tabela é criada.

---

## Fase 2 — BPMN (Demo AI Project - CRUD)
- Confirmar `id="Demo AI Project - CRUD"` no elemento `<process>`.
- Gateway exclusivo:
    - CREATE → `${tarefa == "CREATE"}`
    - READ → `${tarefa == "READ"}`
    - UPDATE → `${tarefa == "UPDATE"}`
    - DELETE → `${tarefa == "DELETE"}`
- Fluxo default: operação inválida (gera `statusCode=400`, `message="Operação inválida"`).
- Service Tasks com delegates:
    - `${identificarTarefaDelegate}`
    - `${createDelegate}`
    - `${readDelegate}`
    - `${updateDelegate}`
    - `${deleteDelegate}`

---

## Fase 3 — Contrato do endpoint
- Rota: `POST /api/cadastro/process`.
- Payload esperado:
  ```json
  {
    "tarefa": "CREATE",
    "id": 1,
    "payload": {
      "nome": "string",
      "email": "string",
      "idade": 30
    }
  }
  ```
- Resposta imediata: **202 Accepted**, body com `processInstanceId` e `businessKey` (opcional).
- Documentar no **Swagger/OpenAPI**.

---

## Fase 4 — Serviço de dados
- Operações:
    - `create(CadastroDto) → CadastroDto`
    - `read(Long id) → Optional<CadastroDto>`
    - `update(Long id, CadastroDto) → Optional<CadastroDto>`
    - `delete(Long id) → boolean`
- Regras de negócio:
    - `404` para não encontrado.
    - `409` para conflito (se aplicável).

---

## Fase 5 — Delegates (contratos)
- **identificarTarefaDelegate**: valida `tarefa` e garante variável disponível para o gateway.
- **createDelegate**: recebe `payload`, salva em AIC_CADASTRO, retorna `statusCode=201`.
- **readDelegate**: recebe `id`, retorna registro ou `statusCode=404`.
- **updateDelegate**: recebe `id` + `payload`, atualiza registro ou retorna `404`.
- **deleteDelegate**: recebe `id`, remove registro ou retorna `404`.

Saídas padrão de todos: `result`, `statusCode`, `message`.

---

## Fase 6 — Controller
- Montar DTO de entrada (`tarefa`, `id`, `payload`).
- Montar variáveis para o processo.
- Iniciar processo via:
  ```java
  runtimeService.startProcessInstanceByKey("Demo AI Project - CRUD", vars);
  ```
- Retornar 202 com `processInstanceId`.
- Validar que `tarefa` é obrigatório; `id` exigido em READ/UPDATE/DELETE; `payload` exigido em CREATE/UPDATE.
- Gerar documentação automática no Swagger.

---

## Fase 7 — Testes ✅ **COMPLETA**
- ✅ **Unitários**: camada de dados com H2.
- ✅ **Integração**: endpoint REST via MockMvc.
- ✅ **JSON Binding**: testes específicos para @JsonProperty annotations.
- ✅ **Validação**: testes Bean Validation para DTOs.
- ✅ **Casos especiais**:
    - ✅ `tarefa=null` → erro 400 com validação.
    - ✅ `tarefa="create"` (case mismatch) → erro 400 com validação.
    - ✅ `tarefa="UPSERT"` → erro 400 com validação.
- ✅ **Correção de bugs**: ProcessControllerIntegrationTest corrigido.

---

## Fase 8 — Observabilidade ✅ **COMPLETA**
- ✅ Ativar Actuator (`/health`, `/metrics`).
- ✅ Incluir logs com `processInstanceId`, `businessKey` e `activityId` em todos os delegates.
- ✅ Documentar retries em jobs Camunda (R3/PT1M configurado no BPMN).

---

## Fase 9 — Smoke Test ✅ **COMPLETA**
- ✅ Aplicação inicia com `mvn spring-boot:run` na porta 8081.
- ✅ Swagger/OpenAPI disponível em `/swagger-ui/index.html`.
- ✅ Exemplos CRUD implementados no controller com documentação completa.
- ✅ URLs úteis validadas: Cockpit, H2 Console, Health, Metrics.

---

## Fase 10 — Documentação ✅ **COMPLETA**
- ✅ Atualizar `README.md` com:
    - ✅ Como rodar a aplicação.
    - ✅ Endpoints REST com exemplos completos de cURL para CRUD.
    - ✅ Swagger UI URL (`/swagger-ui/index.html`) e OpenAPI docs.
    - ✅ Links úteis (Cockpit, H2 console, Health, Metrics).
- ✅ Atualizar `PLAN.md` (este arquivo) com progresso das fases.
- ✅ Atualizar `CLAUDE.md` com instruções de uso atualizadas.

---

## Fase 11A — Testes dos Delegates
- Criar testes unitários para cada delegate (`identificarTarefaDelegate`, `createDelegate`, `readDelegate`, `updateDelegate`, `deleteDelegate`).
- Usar `camunda-bpm-mockito` para mockar `DelegateExecution`.
- Validar entradas (variáveis de processo) e saídas (`result`, `statusCode`, `message`).
- Casos obrigatórios:
    - Happy path (dados válidos).
    - Dados inválidos → `statusCode=400` ou `BpmnError`.
    - IDs inexistentes → `statusCode=404`.
    - Cenários especiais para `tarefa=null`, `tarefa="create"`, `tarefa="UPSERT"` no `IdentificarTarefaDelegate`.

---

## Fase 11B — Testes do Diagrama BPMN + Relatório de Cobertura
- Usar `camunda-bpm-assert` e `camunda-bpm-mockito-scenario` para simular rotas.
- Adicionar `camunda-process-test-coverage-junit5` para gerar relatório HTML.

### Casos obrigatórios
- Rota CREATE → atinge `createDelegate`.
- Rota READ → atinge `readDelegate`.
- Rota UPDATE → atinge `updateDelegate`.
- Rota DELETE → atinge `deleteDelegate`.
- Default flow (tarefa nula, minúscula ou não mapeada) → atinge end de operação inválida.

### Relatório
- Após `mvn test`, abrir `target/process-test-coverage/index.html`.
- Validar que **todos os elementos BPMN** foram cobertos.

---

## ⚡ Correções Críticas Aplicadas (Setembro 2024)

### 🐛 **Problema Original**
- **Issue:** Campo `tarefa` retornando erro "Operation type (tarefa) cannot be blank" mesmo sendo enviado corretamente no JSON
- **Causa:** Falta de `@JsonProperty` annotations nos DTOs para mapeamento JSON → DTO
- **Impacto:** Endpoint principal `/api/cadastro/process` não funcionava

### 🔧 **Correções Implementadas**
1. **✅ JSON Binding Fix:**
   - Adicionado `@JsonProperty("tarefa")` em ProcessRequestDto
   - Adicionado `@JsonProperty("id")` e `@JsonProperty("payload")` em ProcessRequestDto  
   - Adicionado `@JsonProperty` para todos os campos em PayloadDto (nome, email, idade)

2. **✅ Testes Corrigidos:**
   - Corrigido ProcessControllerIntegrationTest (removido mocks incorretos)
   - Adicionado ProcessRequestValidationTest com testes de JSON deserialization
   - Adicionado PayloadDtoValidationTest para validação completa

3. **✅ Validação Habilitada:**
   - Reativado `@ValidProcessRequest` em ProcessRequestDto
   - Validação customizada funcionando corretamente

### 🎯 **Resultados Validados**
- ✅ `curl CREATE` → Status 202 com processInstanceId
- ✅ `curl READ` → Status 202 com businessKey  
- ✅ `curl` sem tarefa → Status 400 com erro correto
- ✅ `curl` com tarefa inválida → Status 400 com validação
- ✅ Todos os testes unitários passando
- ✅ Aplicação rodando corretamente na porta 8081

---

## Definição de Pronto (DoD)
- ✅ Endpoint REST documentado e funcional.
- ✅ BPMN validado com rotas + default flow.
- ✅ Tabela **AIC_CADASTRO** criada automaticamente.
- ✅ Testes executáveis e casos cobertos.
- ✅ Observabilidade mínima (Actuator + logs).
- ✅ Documentação publicada (`README.md`, `PLAN.md`, Swagger UI).

---

## 📊 Status de Implementação

### ✅ Fases Completas (0-10) + Correções Críticas
- **Fase 0:** Preparação do ambiente 
- **Fase 1:** Banco de dados H2 com schema.sql
- **Fase 2:** BPMN processo CRUD com gateway exclusivo
- **Fase 3:** Contrato endpoint REST com DTOs
- **Fase 4:** Serviço de dados CadastroService
- **Fase 5:** Delegates para todas operações CRUD
- **Fase 6:** Controller com validações e Swagger
- **Fase 7:** Testes unitários e integração + **Correção JSON Binding**
- **Fase 8:** Observabilidade com Actuator e logs
- **Fase 9:** Smoke test e documentação Swagger
- **Fase 10:** Documentação completa README.md

### 🔄 Fases Pendentes
- **Fase 11A:** Testes unitários específicos de delegates
- **Fase 11B:** Testes BPMN e relatório de cobertura

### 🎯 Funcionalidades Principais Implementadas
- 🌐 **API REST:** POST /api/cadastro/process (CREATE, READ, UPDATE, DELETE)
- 📋 **BPMN:** Processo orquestrado com gateway exclusivo + default flow  
- 🗄️ **Database:** H2 file-based com tabela AIC_CADASTRO
- 📖 **Documentação:** Swagger UI completo com exemplos
- 🔍 **Observabilidade:** Logs detalhados + Actuator endpoints
- ✅ **Validação:** Bean Validation + validação condicional customizada
- 🧪 **Testes:** Unitários e integração implementados
- 🔧 **JSON Binding:** @JsonProperty annotations corretas para mapeamento DTO
- 🛠️ **Correções:** Bugs de testes de integração resolvidos  
