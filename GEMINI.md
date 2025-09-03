# GEMINI Code Companion Report

## 🚀 Project Overview

This is a Java project built with Spring Boot and Maven, demonstrating the integration of the Camunda BPM workflow engine. The project serves as a foundation for creating intelligent, automated business processes with AI capabilities.

### Key Technologies:
- **Java 21**
- **Spring Boot 3.4.4**
- **Camunda BPM 7.23.0**
- **H2 Database** (file-based)
- **Maven** for dependency management and build automation
- **Lombok** to reduce boilerplate code

The application exposes a web interface for managing and monitoring business processes, as well as a REST API for programmatic interaction.

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
- **Web Interface:** [http://localhost:8080](http://localhost:8080)
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
