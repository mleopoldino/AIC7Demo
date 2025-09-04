# Plano de Refatoração — API CRUD Orquestrada por BPMN (Opção A)
**Versão:** 1.0  
**Data:** 2025-09-04  
**Autor:** Marcelo / NTConsult  
**Contexto:** Correção definitiva do problema de desserialização/validação do campo `tarefa` removendo-o do contrato público e definindo-o **internamente** no controller conforme o endpoint acionado.

---

## 0) Sumário Executivo
- **Problema:** Erros intermitentes de desserialização/validação do campo `tarefa` no endpoint genérico `/api/cadastro/process`.
- **Decisão:** Adotar **Opção A** no BPMN: **manter um único processo** com gateway exclusivo (CREATE/READ/UPDATE/DELETE), **definindo `tarefa` no Controller** conforme o endpoint acionado. O cliente **não envia** `tarefa`.
- **Benefícios:** Contratos REST claros, menor acoplamento com o BPMN, testes mais simples, eliminação da ambiguidade do payload.
- **Compatibilidade:** Endpoint legado pode ser mantido com `@Deprecated` e roteamento interno para os novos endpoints por um período de transição.

---

## 1) Arquitetura & Impactos (Opção A)
- **BPMN permanece** com gateway exclusivo e service tasks:
    - `$ {identificarTarefaDelegate}`, `$ {createDelegate}`, `$ {readDelegate}`, `$ {updateDelegate}`, `$ {deleteDelegate}`.
- **Controller define variáveis** do processo por ação:
    - `tarefa`: **"CREATE" | "READ" | "UPDATE" | "DELETE"**.
    - Demais variáveis: `id` (quando aplicável) e `payload` (CreateDto/UpdateDto).
- **Default flow** continua como fallback interno (não acionável via API pública).

---

## 2) Novos Contratos REST (V1)
**Base path:** `/api/v1/cadastro`

| Ação   | Método | URL                         | Body        | Sucesso | Observações |
|--------|--------|-----------------------------|-------------|---------|-------------|
| CREATE | POST   | `/api/v1/cadastro`          | CreateDto   | **201** | `Location: /api/v1/cadastro/{id}` + body com registro |
| READ   | GET    | `/api/v1/cadastro/{id}`   | —           | **200** | Body com registro |
| UPDATE | PUT    | `/api/v1/cadastro/{id}`   | UpdateDto   | **200** | Body com registro atualizado |
| DELETE | DELETE | `/api/v1/cadastro/{id}`   | —           | **204** | Sem body |

### 2.1 DTOs
**CreateDto (requer todos os campos)**
```json
{
  "nome": "string",
  "email": "string@dominio.com",
  "idade": 30
}
```

**UpdateDto (pelo menos 1 campo presente)**
```json
{
  "nome": "string (opcional)",
  "email": "string@dominio.com (opcional)",
  "idade": 30
}
```

### 2.2 Exemplos cURL
**CREATE**
```bash
curl -X POST http://localhost:8081/api/v1/cadastro   -H "Content-Type: application/json"   -d '{"nome":"Ana","email":"ana@ex.com","idade":25}'
```

**READ**
```bash
curl http://localhost:8081/api/v1/cadastro/1
```

**UPDATE**
```bash
curl -X PUT http://localhost:8081/api/v1/cadastro/1   -H "Content-Type: application/json"   -d '{"email":"novo@ex.com"}'
```

**DELETE**
```bash
curl -X DELETE http://localhost:8081/api/v1/cadastro/1
```

---

## 3) Validações & DTOs (Bean Validation)
- **CreateDto**
    - `@NotBlank nome`
    - `@NotBlank @Email email`
    - `@NotNull @Min(0) idade`
- **UpdateDto**
    - **Constraint customizada** `@AtLeastOneField` (nome, email, idade).
    - Quando presente: `@Email` para email, `@Min(0)` para idade.
- **Mensagens** centralizadas no `messages.properties` (opcional).
- **GlobalExceptionHandler** padroniza payload de erro:  
  `{"timestamp","status","error","message","path","errors":[...]}`

---

## 4) Controller (CadastroControllerV1) — Fluxo por Ação
> O controller **nunca** lê `tarefa` do cliente. Ele **seta** `tarefa` internamente e inicia o processo.

### 4.1 CREATE (POST /api/v1/cadastro)
- **Vars de processo:** `tarefa="CREATE"`, `payload=CreateDto`, `businessKey=UUID` (ou e-mail).
- **Retorno:** **201 Created**. Header `Location` e body com registro criado.

### 4.2 READ (GET /api/v1/cadastro/{id})
- **Vars:** `tarefa="READ"`, `id` (Long).
- **Retorno:** **200 OK** com registro; **404** se não encontrado.

### 4.3 UPDATE (PUT /api/v1/cadastro/{id})
- **Vars:** `tarefa="UPDATE"`, `id` (Long), `payload=UpdateDto`.
- **Retorno:** **200 OK** com registro atualizado; **404** se não encontrado; **400** se body inválido.

### 4.4 DELETE (DELETE /api/v1/cadastro/{id})
- **Vars:** `tarefa="DELETE"`, `id` (Long).
- **Retorno:** **204 No Content**; **404** se não encontrado.

---

## 5) Mapeamento Controller → Variáveis de Processo
| Endpoint | Variáveis enviadas ao processo |
|----------|--------------------------------|
| POST /api/v1/cadastro | `tarefa="CREATE"`, `payload: CreateDto`, `businessKey: UUID (ou email)` |
| GET /api/v1/cadastro/{id} | `tarefa="READ"`, `id: Long` |
| PUT /api/v1/cadastro/{id} | `tarefa="UPDATE"`, `id: Long`, `payload: UpdateDto` |
| DELETE /api/v1/cadastro/{id} | `tarefa="DELETE"`, `id: Long` |

> **Dica:** logar as variáveis com `processInstanceId` e `businessKey` no início de cada chamada.

---

## 6) Delegates — Ajustes Pontuais
- **IdentificarTarefaDelegate**: mantém a verificação de presença de `tarefa` (agora **sempre** setada pelo controller).
- **CreateDelegate**: recebe `payload (CreateDto)` e persiste (retorna `statusCode=201`, `result` com DTO criado).
- **ReadDelegate**: recebe `id`, retorna `result` ou `statusCode=404`.
- **UpdateDelegate**: recebe `id` + `payload (UpdateDto)`; retorna atualizado ou `404`.
- **DeleteDelegate**: recebe `id`; remove e retorna `204`/`404`.

> **Regra:** Delegates não confiam em dados vindos do cliente sem validação. Controller já aplica validação prévia, mas revalide o essencial no service.

---

## 7) BPMN — O que fica e o que muda
- **Fica:** um único processo com gateway exclusivo por `tarefa` + default flow.
- **Muda:** a **origem** de `tarefa` — agora **sempre** do Controller.
- **Boa prática:** nomear sequências de fluxo (ex.: `toCreate`, `toRead` etc.) para facilitar cobertura de testes e leitura.

---

## 8) Serviço de Dados (CadastroService)
- CRUD permanece. Ajuste exceções claras:
    - `EntityNotFoundException` ↔ **404**
    - `IllegalStateException` (ou BusinessException) ↔ **422** (quando for regra de negócio)
- Retornar DTOs coerentes (mapstruct opcional).

---

## 9) Swagger/OpenAPI
- **Adicionar tag** `Cadastro V1` e documentar os 4 endpoints.
- **Schemas:** `CreateDto`, `UpdateDto`, `CadastroDto`.
- **Exemplos** de requests/responses (201/200/204/400/404).
- **Endpoint legado** `/api/cadastro/process` marcado como **`deprecated: true`** com aviso de migração.

---

## 10) Tratamento de Erros (GlobalExceptionHandler)
- Padronizar respostas JSON e status conforme seção 3.
- Logar `correlationId`/`businessKey` quando aplicável.
- Garantir que erros de validação listem `field → message`.

---

## 11) Checklist **Simplificado** (execução rápida)
1. Criar **DTOs** `CreateDto` e `UpdateDto` + validator `@AtLeastOneField`.
2. Implementar **CadastroControllerV1** (POST/GET/PUT/DELETE) definindo `tarefa` internamente.
3. Ajustar leitura de variáveis nos **delegates** (se necessário) para os novos tipos de DTO.
4. Atualizar **Swagger** com endpoints v1 e marcar legado como `deprecated`.
5. Revisar **GlobalExceptionHandler** para payload de erro uniforme.
6. Escrever **testes de integração** (MockMvc) por endpoint: happy/erro/404.
7. Escrever **testes unitários** de validators e serviço.
8. Executar **mvn clean test** e revisar **cobertura**; rodar a aplicação e fazer **smoke cURL**.
9. Comunicar timeline de **depreciação** do endpoint legado.
10. Atualizar **README.md / PLAN.md / CLAUDE.md**.

---

## 12) Testes (mínimo recomendado)
### 12.1 Integração (MockMvc)
- **Create**: 201 + `Location` + body; 400 (validação).
- **Read**: 200 existente; 404 inexistente.
- **Update**: 200 com 1+ campos; 400 sem campos; 404 inexistente.
- **Delete**: 204; 404 inexistente.
- **Asserções**: `runtimeService.startProcessInstanceByKey` chamado com `tarefa` correta (não vir do body).

### 12.2 Unitários
- `@AtLeastOneField` (cenários válidos/invalidos).
- `CadastroService` CRUD com H2 (`@DataJpaTest`).

### 12.3 BPMN (opcional/sugerido)
- `camunda-bpm-assert` para cobrir rotas CREATE/READ/UPDATE/DELETE (default não exposto pela API).

---

## 13) Migração & Depreciação
- Manter `/api/cadastro/process` com `@Deprecated` e **roteamento interno** para V1.
- Logar **WARN** com instruções de migração.
- Planejar remoção em **90 dias** (atualizar docs e comunicar consumidores).

---

## 14) Observabilidade & Segurança
- **Logs** com `processInstanceId`, `businessKey`, `activityId`, `action`.
- **Actuator** habilitado (`/health`, `/metrics`).
- Planejar **Spring Security** com escopos por ação (`cadastro:read`, `cadastro:write`).

---

## 15) Execução & Smoke Test
```bash
mvn clean package
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"
# CREATE
curl -X POST http://localhost:8081/api/v1/cadastro -H "Content-Type: application/json" -d '{"nome":"Ana","email":"ana@ex.com","idade":25}'
# READ
curl http://localhost:8081/api/v1/cadastro/1
# UPDATE
curl -X PUT http://localhost:8081/api/v1/cadastro/1 -H "Content-Type: application/json" -d '{"email":"novo@ex.com"}'
# DELETE
curl -X DELETE http://localhost:8081/api/v1/cadastro/1
```

---

## 16) Notas Finais
- Este plano **elimina a dependência** do campo `tarefa` no contrato público, encerrando a causa-raiz do erro.
- A orquestração BPMN é **preservada** e estabilizada pelo **controle no Controller**.
- Os testes tornam-se **mais previsíveis** e fáceis de manter.
