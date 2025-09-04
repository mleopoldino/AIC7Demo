# GEMINI Code Companion Report

## 🚀 Project Overview

This is a Java project built with Spring Boot and Maven, demonstrating the integration of the Camunda BPM workflow engine. The project serves as a foundation for creating intelligent, automated business processes with AI capabilities, featuring a complete CRUD API orchestrated through BPMN workflows.

### Key Technologies:
- **Java 21**
- **Spring Boot 3.3.0**
- **Camunda BPM 7.23.0**
- **H2 Database** (file-based)
- **Maven** for dependency management and build automation
- **Lombok** to reduce boilerplate code

## 🔧 API Endpoints

### API V1 (Recomendado)
A API V1 segue um padrão RESTful padrão para operações de CRUD.

| Ação   | Método | URL                         | Body        | Sucesso | Observações |
|--------|--------|-----------------------------|-------------|---------|-------------|
| CREATE | POST   | `/api/v1/cadastro`          | CreateDto   | **201** | `Location: /api/v1/cadastro/{id}` + body com registro |
| READ   | GET    | `/api/v1/cadastro/{id}`   | —           | **200** | Body com registro |
| UPDATE | PUT    | `/api/v1/cadastro/{id}`   | UpdateDto   | **200** | Body com registro atualizado |
| DELETE | DELETE | `/api/v1/cadastro/{id}`   | —           | **204** | Sem body |

#### Exemplos cURL
**CREATE**
```bash
curl -X POST http://localhost:8081/api/v1/cadastro \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana","email":"ana@ex.com","idade":25}'
```

**READ**
```bash
curl http://localhost:8081/api/v1/cadastro/1
```

**UPDATE**
```bash
curl -X PUT http://localhost:8081/api/v1/cadastro/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"novo@ex.com"}'
```

**DELETE**
```bash
curl -X DELETE http://localhost:8081/api/v1/cadastro/1
```

---

### Endpoint Legado (Obsoleto)
- **POST /api/cadastro/process** - Orquestra operações CRUD através de um único endpoint BPMN. **Este endpoint está obsoleto e será removido em versões futuras.**

#### Usage Examples (Legacy):
```bash
# CREATE operation
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "CREATE", "payload": {"nome": "John", "email": "john@test.com", "idade": 30}}'

# READ operation  
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "READ", "id": 1}'
```

## ⚠️ Important Technical Notes

### JSON Binding Requirements
- **ProcessRequestDto (Legacy)**: `@JsonProperty("tarefa")`, `@JsonProperty("id")`, `@JsonProperty("payload")`
- **PayloadDto (Legacy)**: `@JsonProperty("nome")`, `@JsonProperty("email")`, `@JsonProperty("idade")`
- **CreateRequestDto (V1)**: Standard bean properties.
- **UpdateRequestDto (V1)**: Standard bean properties.

### Validation Features
- Bean Validation active on all DTOs.
- Custom conditional validation (`@AtLeastOneField`) for V1 update endpoint.
- Proper error handling with structured responses