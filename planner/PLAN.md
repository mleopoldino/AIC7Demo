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

## Fase 0 — Preparação do ambiente
- Verificar toolchain (JDK 21, Maven, IntelliJ).
- Confirmar `spring-boot 3.4.4` + `camunda 7.23` no `pom.xml`.
- Configurar Gemini CLI (API key).
- Garantir `CLAUDE.md` e `GEMINI.md` no repositório.
- `mvn clean package` deve rodar sem erros.

- Swagger / OpenAPI
  - Dependência: `org.springdoc:springdoc-openapi-starter-webmvc-ui`.
  - URL de acesso: `/swagger-ui/index.html`.
  - Incluir schemas e exemplos de requests/responses para os principais cenários (202, 4xx, 5xx).

- Validação
  - Adotar `spring-boot-starter-validation`.
  - Anotações recomendadas:
      - `@NotBlank` em `payload.nome` e `payload.email`.
      - `@Email` em `payload.email`.
      - `@Min(0)` em `payload.idade`.

- Process Key
  - Confirmar que o atributo `id` do processo BPMN não contém espaços.
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
  - Adicionar logs contendo `correlationId` e `processInstanceId` em cada delegate e nas entr

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

## Fase 7 — Testes
- **Unitários**: camada de dados com H2.
- **Integração**: endpoint REST via MockMvc.
- **BPMN**: simular rotas com `camunda-bpm-assert-scenario`.
- **Casos especiais**:
    - `tarefa=null` → default flow.
    - `tarefa="create"` (case mismatch) → default flow.
    - `tarefa="UPSERT"` → default flow.

---

## Fase 8 — Observabilidade
- Ativar Actuator (`/health`, `/metrics`).
- Incluir logs com `processInstanceId`.
- Documentar retries em jobs Camunda.

---

## Fase 9 — Smoke Test
- Subir aplicação com `mvn spring-boot:run`.
- Executar cURLs de CREATE/READ/UPDATE/DELETE.
- Executar chamada inválida (`tarefa=UPSERT`).
- Validar no Cockpit e no H2 console.

---

## Fase 10 — Documentação
- Atualizar `README.md` com:
    - Como rodar.
    - Endpoints REST + exemplos.
    - Swagger UI URL (`/swagger-ui.html` ou `/swagger-ui/index.html`).
    - Links úteis (Cockpit, H2 console).
- Atualizar `PLAN.md` (este arquivo) com progresso.
- Atualizar `CLAUDE.md` e `GEMINI.md` com instruções de uso.

---

## Definição de Pronto (DoD)
- Endpoint REST documentado e funcional.
- BPMN validado com rotas + default flow.
- Tabela **AIC_CADASTRO** criada automaticamente.
- Testes executáveis e casos cobertos.
- Observabilidade mínima (Actuator + logs).
- Documentação publicada (`README.md`, `PLAN.md`, Swagger UI).  
