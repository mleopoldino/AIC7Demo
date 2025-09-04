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

The application exposes a web interface for managing and monitoring business processes, as well as a REST API for programmatic interaction. The main feature is a CRUD endpoint that orchestrates database operations through BPMN process execution.

## ⚙️ Building and Running

### Prerequisites:
- Java 21 or higher
- Maven 3.6+

### Build Commands:
- **Compile the project:**
  ```bash
  mvn clean compile
  ```
- **Run the application:**
  ```bash
  mvn spring-boot:run
  ```
- **Package the application into a JAR file:**
  ```bash
  mvn package
  ```

### Testing:
- **Run tests:**
  ```bash
  mvn test
  ```
- **Generate a test coverage report:**
  ```bash
  mvn test jacoco:report
  ```

### Accessing the Application:
- **Camunda Welcome:** [http://localhost:8081/camunda/app/welcome/default/](http://localhost:8081/camunda/app/welcome/default/)
- **Camunda Tasklist:** [http://localhost:8081/camunda/app/tasklist/default/](http://localhost:8081/camunda/app/tasklist/default/)
- **Camunda Cockpit:** [http://localhost:8081/camunda/app/cockpit/default/](http://localhost:8081/camunda/app/cockpit/default/)
- **Camunda Admin:** [http://localhost:8081/camunda/app/admin/default/](http://localhost:8081/camunda/app/admin/default/)
- **Swagger UI (API Docs):** [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **H2 Console (Database):** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)
- **Camunda REST API:** [http://localhost:8081/engine-rest](http://localhost:8081/engine-rest)
- **Admin Credentials:** `demo` / `demo`

##  conventions Development Conventions

### Project Structure:
- **`src/main/java/com/mls/workflow`**: Main application source code.
  - **`Application.java`**: Spring Boot entry point.
  - **`camunda/delegate`**: Implementations of Camunda's `JavaDelegate` for service tasks.
  - **`camunda/external`**: External task workers.
  - **`camunda/handler`**: Process event handlers.
  - **`core/service`**: Reusable business logic.
  - **`core/dto`**: Data Transfer Objects.
- **`src/main/resources`**: Application resources.
  - **`application.yaml`**: Main application configuration.
  - **`bpmn/`**: BPMN 2.0 process definitions.
  - **`dmn/`**: DMN 1.3 decision models.
  - **`form/`**: Camunda Forms.

### Adding New Features:
1.  **Create or update BPMN processes** in the `src/main/resources/bpmn/` directory.
2.  **Implement `JavaDelegate` classes** in the `com.mls.workflow.camunda.delegate` package for custom logic in service tasks.
3.  **Define new services** in the `com.mls.workflow.core.service` package for reusable business logic.
4.  **Add new REST endpoints** in a new `controller` package to expose functionality via the API.

## 🔧 API Endpoints

### Main CRUD Endpoint
- **POST /api/cadastro/process** - Orchestrates CRUD operations through BPMN

### Usage Examples:
```bash
# CREATE operation
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "CREATE", "payload": {"nome": "John", "email": "john@test.com", "idade": 30}}'

# READ operation  
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "READ", "id": 1}'

# UPDATE operation
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "UPDATE", "id": 1, "payload": {"nome": "John Silva", "email": "john.silva@test.com", "idade": 35}}'

# DELETE operation
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "DELETE", "id": 1}'
```

### Response Format:
```json
{
  "processInstanceId": "uuid-string",
  "businessKey": "entity-id"
}
```

## ⚠️ Important Technical Notes

### JSON Binding Requirements
The project uses `@JsonProperty` annotations for proper JSON-to-DTO mapping:
- **ProcessRequestDto**: `@JsonProperty("tarefa")`, `@JsonProperty("id")`, `@JsonProperty("payload")`
- **PayloadDto**: `@JsonProperty("nome")`, `@JsonProperty("email")`, `@JsonProperty("idade")`

### Validation Features
- Bean Validation active on all DTOs
- Custom conditional validation (`@ValidProcessRequest`)
- Proper error handling with structured responses

### Database Schema
H2 Database with table **AIC_CADASTRO**:
- ID (auto-increment, primary key)
- NOME (string, not null)
- EMAIL (string, not null) 
- IDADE (integer)
