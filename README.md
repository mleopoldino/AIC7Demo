# AIC7Demo

Projeto de demonstração de workflow Camunda BPM integrado com IA, construído com Spring Boot para gerenciamento automatizado de processos de negócio.

## 📋 Descrição do Projeto

Este projeto demonstra a integração entre **Camunda BPM 7.23.0** e **Spring Boot 3.4.4** para criação de workflows inteligentes. A aplicação fornece uma base sólida para desenvolvimento de processos automatizados com capacidades de IA, incluindo interface web administrativa e API REST para gerenciamento de processos.

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
│       │               │   ├── external/             # External Task Workers
│       │               │   └── handler/              # Event handlers
│       │               ├── config/                   # Configurações Spring
│       │               └── core/                     # Lógica de negócio
│       │                   ├── dto/                  # Data Transfer Objects
│       │                   ├── exception/            # Exceções customizadas
│       │                   ├── service/              # Serviços de negócio
│       │                   └── util/                 # Utilitários
│       └── resources/
│           ├── application.yaml                      # Configuração da aplicação
│           ├── bpmn/                                # Processos BPMN
│           │   └── process.bpmn                     # Processo de demonstração
│           ├── dmn/                                 # Decision Model Notation
│           └── form/                                # Camunda Forms
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

## 🗄️ Configuração do Banco de Dados

O projeto utiliza **H2 Database** configurado para persistir em arquivo:

- **Tipo:** H2 File Database
- **Localização:** `./data/camunda-db`
- **URL:** `jdbc:h2:file:./data/camunda-db`
- **Console H2:** Disponível em http://localhost:8081/h2-console (se habilitado)

## 📊 Observabilidade

### Spring Boot Actuator

Os endpoints do Spring Boot Actuator estão habilitados para monitoramento da aplicação:

- **Health Check:** `http://localhost:8081/actuator/health`
- **Métricas:** `http://localhost:8081/actuator/metrics`

### Camunda Job Retries

As Service Tasks no processo BPMN estão configuradas com um mecanismo de retry automático para aumentar a robustez e resiliência. Em caso de falha transitória, as tarefas serão automaticamente re-tentadas antes de serem marcadas como falhas permanentes.

- **Estratégia:** `R3/PT1M` (3 retries com intervalo de 1 minuto entre cada tentativa).
- **Configuração:** Aplicado a todas as Service Tasks no `process.bpmn` via atributos `camunda:asyncBefore="true"` e `camunda:failedJobRetryTimeCycle="R3/PT1M"`.

## 🔐 Credenciais Padrão

- **Usuário Admin:** `demo`
- **Senha:** `demo`
- **Acesso:** Camunda Cockpit, Tasklist e Admin

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

## 🔌 API REST - Endpoints

### POST /api/cadastro/process
**Descrição:** Inicia uma instância do processo BPMN "Demo AI Project - CRUD"

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

**3. Operação UPDATE:**
```bash
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{
    "tarefa": "UPDATE",
    "id": 1,
    "payload": {
      "nome": "João Santos",
      "email": "joao.santos@exemplo.com",
      "idade": 35
    }
  }'
```

**4. Operação DELETE:**
```bash
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{
    "tarefa": "DELETE",
    "id": 1
  }'
```

**5. Operação Inválida (testa default flow):**
```bash
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{
    "tarefa": "UPSERT",
    "id": 999
  }'
```

#### Resposta de Sucesso (202)
```json
{
  "processInstanceId": "12345678-1234-1234-1234-123456789012",
  "businessKey": "1"
}
```

#### Resposta de Erro (400)
```json
{
  "timestamp": "2025-09-04T01:00:00.000",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "path": "/api/cadastro/process",
  "errors": {
    "tarefa": "Operation type (tarefa) cannot be blank"
  }
}
```

## 📊 Funcionalidades Implementadas

### CRUD Orquestrado via BPMN
- ✅ **Processo BPMN:** Demo AI Project - CRUD com gateway exclusivo
- ✅ **Service Tasks:** CREATE, READ, UPDATE, DELETE com delegates
- ✅ **Banco H2:** Tabela AIC_CADASTRO (ID, NOME, EMAIL, IDADE)
- ✅ **Validação:** Bean Validation com anotações customizadas
- ✅ **Default Flow:** Tratamento de operações inválidas

### Camunda BPM
- ✅ Engine de workflow completo
- ✅ Interface web administrativa (Cockpit, Tasklist, Admin)
- ✅ API REST para automação
- ✅ Suporte a BPMN 2.0, DMN 1.3
- ✅ Histórico de processos com TTL de 180 dias
- ✅ Job retries configurados (R3/PT1M)

### Spring Boot + Observabilidade
- ✅ Auto-configuração do Camunda
- ✅ Swagger/OpenAPI integrado
- ✅ Spring Boot Actuator (health, metrics)
- ✅ Logs com processInstanceId, businessKey e correlationId
- ✅ Global Exception Handler para tratamento de erros
- ✅ Configuração via YAML

## 🛠️ Desenvolvimento

### Adicionando Novos Processos
1. Crie arquivos `.bpmn` em `src/main/resources/bpmn/`
2. Implemente delegates em `com.mls.workflow.camunda.delegate`
3. Configure beans necessários

### Estrutura de Código
- **Delegates:** Implementações de lógica para Service Tasks
- **External Workers:** Workers para External Tasks
- **Handlers:** Manipuladores de eventos do processo
- **Services:** Lógica de negócio reutilizável

## 📝 Próximos Passos

- [ ] Implementar integração com APIs de IA
- [ ] Adicionar testes unitários e de integração
- [ ] Configurar profiles para diferentes ambientes
- [ ] Documentar processos BPMN existentes
- [ ] Implementar monitoramento e métricas

## 🤝 Contribuindo

1. Faça fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📜 Licença

Este projeto é uma demonstração e está disponível para fins educacionais e de desenvolvimento.
