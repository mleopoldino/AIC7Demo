# 🧪 Plano de Testes — Projeto Workflow

## 🎯 Objetivo
Cobrir os seguintes tipos de testes:
- **Unitários (dados/H2):** validar repositórios, CRUD e constraints.
- **Integração (REST/MockMvc):** validar payloads, códigos de status e acionamento do `GlobalExceptionHandler`.
- **Casos especiais no endpoint:**
    - `tarefa = null`
    - `tarefa = "create"` (lowercase)
    - `tarefa = "UPSERT"`  
      Todos devem cair no **fluxo default**, mas o controller ainda retorna `202 Accepted`.

---

## 1) Testes Unitários (Dados com H2)

### Configuração
- Usar `@DataJpaTest` com banco em memória **H2**.
- Adicionar `application-test.yml` em `src/test/resources` com datasource configurado para H2.
- Criar `schema.sql` e `data.sql` em `src/test/resources` para inicializar as tabelas e registros de teste.

### Cenários a Cobrir
1. **Buscar por ID existente** → deve retornar registro válido.
2. **Criar novo registro** → persistir e recuperar.
3. **Atualizar registro existente** → confirmar alterações salvas.
4. **Deletar registro** → garantir que foi removido.
5. **Constraint violada** (ex.: `nome` ou `email` nulos) → exceção lançada.

### Organização
- `src/test/java/com/mls/workflow/repository/AicCadastroRepositoryTest.java`
- `src/test/java/com/mls/workflow/repository/PrdCatProdutoRepositoryTest.java` (se aplicável).

---

## 2) Testes de Integração (REST/MockMvc)

### Configuração
- Usar:
  ```java
  @WebMvcTest(ProcessController.class)
  @Import(GlobalExceptionHandler.class)
  @AutoConfigureMockMvc(addFilters = false)
  ```
- Mockar `RuntimeService` com `@MockBean`.
- Sempre usar `.contentType(MediaType.APPLICATION_JSON)`.

### Cenários a Cobrir

#### 2.1 Payload Inválido
- **Entrada:** JSON com campos obrigatórios nulos ou inválidos.
- **Saída esperada:** `400 Bad Request`.
- **Verificações:**
    - JSON de erro contém `message`, `status`, `errors`.
    - `runtimeService` **não chamado**.

#### 2.2 Payload Válido
- **Entrada:** JSON com todos os campos válidos.
- **Saída esperada:** `202 Accepted`.
- **Verificações:**
    - `runtimeService.startProcessInstanceByKey(...)` chamado.
    - Resposta contém `id` e `businessKey`.

#### 2.3 Casos Especiais
- **`tarefa = null`**
    - **Entrada:** JSON sem valor para `tarefa`.
    - **Saída esperada:** `202 Accepted`.
    - **Verificação:** variável `tarefa` enviada como `null`.

- **`tarefa = "create"` (lowercase)**
    - **Entrada:** JSON com `"tarefa": "create"`.
    - **Saída esperada:** `202 Accepted`.
    - **Verificação:** variável não corresponde às rotas válidas (fluxo default).

- **`tarefa = "UPSERT"`**
    - **Entrada:** JSON com `"tarefa": "UPSERT"`.
    - **Saída esperada:** `202 Accepted`.
    - **Verificação:** tratado como default.

---

## 3) Organização do Código de Testes
- `src/test/java/com/mls/workflow/repository/*RepositoryTest.java` → testes unitários (dados/H2).
- `src/test/java/com/mls/workflow/controller/ProcessControllerTest.java` → testes de integração (REST/MockMvc).

---

## 4) Limites
- ClaudeCode ou Gemini **não deve alterar** código de produção além de adicionar anotações de validação em DTOs, se faltarem.
- ClaudeCode ou Gemini **não deve alterar** lógica do `ProcessController`, apenas testar o comportamento.
- ClaudeCode ou Gemini **deve mockar** o `RuntimeService` em todos os testes REST.
- **Não implementar** testes de BPMN neste escopo.

---

## 5) Tabela de Casos de Teste (REST)

| Cenário             | Entrada JSON (exemplo)                                                                 | Status Esperado | Verificações                                                   |
|---------------------|-----------------------------------------------------------------------------------------|-----------------|---------------------------------------------------------------|
| Payload inválido    | `{ "tarefa": "", "id": null, "payload": { "nome": "", "email": "bad", "idade": 999 }}` | 400             | JSON de erro retornado, `runtimeService` não chamado          |
| Payload válido      | `{ "tarefa": "CREATE", "id": 123, "payload": { "nome": "João", "email": "joao@ex.com", "idade": 30 }}` | 202 | `runtimeService` chamado, resposta contém `id` e `businessKey`|
| `tarefa = null`     | `{ "tarefa": null, "id": 1, "payload": { "nome": "A", "email": "a@ex.com", "idade": 20 }}` | 202 | Variável `tarefa` enviada como `null`                        |
| `tarefa = "create"` | `{ "tarefa": "create", "id": 1, "payload": { "nome": "A", "email": "a@ex.com", "idade": 20 }}` | 202 | Variável não corresponde às rotas válidas (default flow)      |
| `tarefa = "UPSERT"` | `{ "tarefa": "UPSERT", "id": 1, "payload": { "nome": "A", "email": "a@ex.com", "idade": 20 }}` | 202 | Variável tratada como default                                |
