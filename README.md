# AIC7Demo

Projeto de demonstração de workflow Camunda BPM integrado com IA, construído com Spring Boot para gerenciamento automatizado de processos de negócio.

## 📋 Descrição do Projeto

Este projeto demonstra a integração entre **Camunda BPM 7.23.0** e **Spring Boot 3.4.4** para criação de workflows inteligentes. A aplicação fornece uma base sólida para desenvolvimento de processos automatizados com capacidades de IA, incluindo interface web administrativa e uma API RESTful para gerenciamento de processos.

## 🚀 Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 3.4.4** - Framework principal
- **Camunda BPM 7.23.0** - Engine de workflow e BPMN
- **H2 Database** - Banco de dados em arquivo para persistência
- **Maven** - Gerenciador de dependências e build
- **Lombok** - Redução de boilerplate de código

## 📁 Estrutura do Projeto

```
AIC7Demo/
├── README.md                           # Documentação do projeto
├── CLAUDE.md                          # Instruções para Claude Code
├── pom.xml                            # Configuração Maven
├── camunda-h2-database.mv.db          # Banco H2 (gerado automaticamente)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mls/
│       │           └── workflow/
│       │               ├── Application.java           # Classe principal
│       │               ├── camunda/                   # Componentes Camunda
│       │               │   ├── delegate/             # JavaDelegate implementations
│       │               ├── config/                   # Configurações Spring
│       │               └── core/                     # Lógica de negócio
│       │                   ├── dto/                  # Data Transfer Objects
│       │                   ├── service/              # Serviços de negócio
│       │                   └── validation/           # Validadores customizados
│       └── resources/
│           ├── application.yaml                      # Configuração da aplicação
│           ├── bpmn/                                # Processos BPMN
│           │   └── process.bpmn                     # Processo de demonstração
└── target/                                          # Artefatos de build (ignorar)
```

## ⚙️ Configuração e Instalação

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6+
- Git

### Instalação

1. **Clone o repositório:**
   ```bash
   git clone <repository-url>
   cd AIC7Demo
   ```

2. **Compile o projeto:**
   ```bash
   mvn clean compile
   ```

3. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a aplicação:**
   - Interface Web: http://localhost:8081
   - Credenciais: `demo/demo`

## 🔧 Comandos Úteis

### Build e Execução
```bash
# Compilar
mvn compile

# Executar aplicação
mvn spring-boot:run

# Empacotar JAR
mvn package

# Limpar e reconstruir
mvn clean package
```

### Testes
```bash
# Executar testes
mvn test

# Relatório de coverage
mvn test jacoco:report
```

## 🌐 URLs Úteis

Após iniciar a aplicação com `mvn spring-boot:run`, os seguintes endpoints estarão disponíveis:

### Interfaces Web
- **🎛️ Camunda Cockpit:** http://localhost:8081 (usuário: `demo` / senha: `demo`)
- **📋 Camunda Tasklist:** http://localhost:8081/camunda/app/tasklist
- **⚙️ Camunda Admin:** http://localhost:8081/camunda/app/admin
- **🗃️ H2 Database Console:** http://localhost:8081/h2-console
  - **JDBC URL:** `jdbc:h2:file:./data/camunda-db`
  - **User:** `sa` (sem senha)

### APIs e Documentação
- **📖 Swagger UI:** http://localhost:8081/swagger-ui/index.html
- **📄 OpenAPI Docs (JSON):** http://localhost:8081/v3/api-docs
- **❤️ Health Check:** http://localhost:8081/actuator/health
- **📊 Metrics:** http://localhost:8081/actuator/metrics

## 🔌 API REST - Endpoints V1 (Recomendado)

A API V1 segue um padrão RESTful padrão para operações de CRUD, oferecendo um contrato claro e previsível.

### **`POST /api/v1/cadastro`**
Cria um novo registro.
- **Body:** `CreateRequestDto`
- **Sucesso:** `201 Created` com o header `Location` e o corpo do registro criado.

```bash
curl -X POST http://localhost:8081/api/v1/cadastro \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana","email":"ana@ex.com","idade":25}'
```

### **`GET /api/v1/cadastro/{id}`**
Busca um registro pelo ID.
- **Sucesso:** `200 OK` com o corpo do registro.
- **Erro:** `404 Not Found`.

```bash
curl http://localhost:8081/api/v1/cadastro/1
```

### **`PUT /api/v1/cadastro/{id}`**
Atualiza um registro existente. Pelo menos um campo deve ser fornecido.
- **Body:** `UpdateRequestDto`
- **Sucesso:** `200 OK` com o corpo do registro atualizado.
- **Erro:** `404 Not Found`.

```bash
curl -X PUT http://localhost:8081/api/v1/cadastro/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"novo@ex.com"}'
```

### **`DELETE /api/v1/cadastro/{id}`**
Deleta um registro.
- **Sucesso:** `204 No Content`.
- **Erro:** `404 Not Found`.

```bash
curl -X DELETE http://localhost:8081/api/v1/cadastro/1
```

---

## 🔌 API REST - Endpoint Legado (Obsoleto)

### POST /api/cadastro/process
**Descrição:** **(OBSOLETO)** Inicia uma instância do processo BPMN "Demo AI Project - CRUD". **Utilize a API V1 para novas implementações.**

**Content-Type:** `application/json`

**Resposta:** `202 Accepted` com `processInstanceId` e `businessKey`

#### Exemplos de Uso

**1. Operação CREATE:**
```bash
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{
    "tarefa": "CREATE",
    "payload": {
      "nome": "João Silva",
      "email": "joao@exemplo.com",
      "idade": 30
    }
  }'
```

**2. Operação READ:**
```bash
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{
    "tarefa": "READ",
    "id": 1
  }'
```

## 📝 Próximos Passos

- [ ] Implementar integração com APIs de IA
- [ ] Adicionar mais testes de integração para cobrir casos de borda
- [ ] Configurar profiles para diferentes ambientes
- [ ] Implementar monitoramento e métricas com Micrometer

## 🤝 Contribuindo

1. Faça fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📜 Licença

Este projeto é uma demonstração e está disponível para fins educacionais e de desenvolvimento.