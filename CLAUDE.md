# CLAUDE.md

Este arquivo fornece orientação ao Claude Code (claude.ai/code) quando trabalha com código neste repositório.

## Visão Geral do Projeto

Este é um projeto de demonstração de workflow Camunda BPM construído com Spring Boot 3.4.4 e Camunda BPM 7.23.0. O projeto demonstra integração de IA com gerenciamento de processos de negócio.

## Stack Tecnológica

### Linguagem e Plataforma
- **Java**: 21 (source e target compatibility)
- **Build Tool**: Maven 3.x
- **Framework**: Spring Boot 3.4.4

### Dependências Principais
- **Camunda BPM**: 7.23.0 (Spring Boot Starter REST e Web App)
- **Camunda Spin**: Plugin para processamento de formatos de dados
- **Database**: H2 Database (em arquivo para persistência de workflow)
- **Logging**: SLF4J 2.0.13 (API)
- **Utilitários**: Lombok 1.18.32 (annotations para redução de boilerplate)

### Dependências de Gerenciamento
```xml
- Spring Boot Dependencies BOM: 3.4.4
- Camunda BOM: 7.23.0
```

## Estrutura do Projeto

### Árvore de Diretórios
```
AIC7Demo/
├── README.md
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── mls/
│       │           └── workflow/
│       │               ├── Application.java (Classe principal)
│       │               ├── camunda/
│       │               │   ├── delegate/     (JavaDelegate implementations)
│       │               │   ├── external/     (External Task Workers)
│       │               │   └── handler/      (Event handlers)
│       │               ├── config/           (Configurações Spring)
│       │               └── core/
│       │                   ├── dto/          (Data Transfer Objects)
│       │                   ├── exception/    (Custom exceptions)
│       │                   ├── service/      (Lógica de negócio)
│       │                   └── util/         (Utilitários)
│       └── resources/
│           ├── application.yaml (Configuração da aplicação)
│           ├── bpmn/
│           │   └── process.bpmn (Definição do processo)
│           ├── dmn/             (Decision Model Notation files)
│           └── form/            (Camunda Forms)
├── camunda-h2-database.mv.db   (Arquivo de banco H2)
└── target/                     (Artefatos de build - ignorar)
```

### Organização de Pacotes
- **`com.mls.workflow`**: Pacote raiz da aplicação
- **`com.mls.workflow.camunda`**: Componentes específicos do Camunda
  - `delegate`: Implementações de JavaDelegate para Service Tasks
  - `external`: Workers para External Tasks
  - `handler`: Manipuladores de eventos do processo
- **`com.mls.workflow.config`**: Configurações Spring e Camunda
- **`com.mls.workflow.core`**: Lógica de negócio core
  - `dto`: Objetos de transferência de dados
  - `exception`: Exceções customizadas
  - `service`: Serviços de negócio
  - `util`: Classes utilitárias

## Comandos de Desenvolvimento

### Build e Execução
```bash
# Compilar o projeto
mvn compile

# Executar a aplicação (porta 8081)
mvn spring-boot:run

# Empacotar a aplicação
mvn package

# Limpar e reconstruir
mvn clean package
```

### Testes
```bash
# Executar testes unitários e de integração
mvn test

# Executar testes com coverage (se configurado)
mvn test jacoco:report
```

### URLs Importantes (após mvn spring-boot:run)
```bash
# Aplicação principal
http://localhost:8081

# Swagger UI - Documentação da API
http://localhost:8081/swagger-ui/index.html

# H2 Database Console (JDBC URL: jdbc:h2:file:./data/camunda-db, user: sa)
http://localhost:8081/h2-console

# Camunda Cockpit (usuário: demo/demo)
http://localhost:8081

# Health Check
http://localhost:8081/actuator/health

# Metrics
http://localhost:8081/actuator/metrics
```

## Configuração do Camunda

### Credenciais de Acesso
- **Admin User**: demo/demo (configurado em application.yaml)
- **Interface Web**: Disponível em http://localhost:8080 quando executando
- **REST API**: Habilitada via camunda-bpm-spring-boot-starter-rest

### Banco de Dados
- **Tipo**: H2 Database (baseado em arquivo)
- **Localização**: `./camunda-h2-database`
- **URL**: jdbc:h2:file:./camunda-h2-database

### Detalhes do Workflow
O projeto inclui um processo BPMN simples (`aic7demo-process`) com:
- Evento de início
- Uma tarefa de usuário "Break Point 1" atribuída ao usuário "demo"
- Evento de fim
- TTL de histórico de 180 dias

## Convenções de Código

### Estrutura de Classes
- Utilize Lombok para reduzir boilerplate (`@Data`, `@Service`, `@Component`)
- Prefira injeção de dependência via construtor
- Mantenha classes de configuração no pacote `config`
- Organize delegates e handlers no pacote `camunda`

### Padrões de Naming
- Classes: PascalCase (ex: `ProcessDelegate`)
- Métodos/variáveis: camelCase (ex: `executeProcess`)
- Constantes: UPPER_SNAKE_CASE (ex: `PROCESS_ID`)
- Pacotes: lowercase com separadores por ponto

### Logging
- Utilize SLF4J para logging
- Configure níveis apropriados em application.yaml
- Use placeholders para performance: `log.info("Processing {}", processId)`

## Orientações para Novos Contribuidores

### Primeiros Passos
1. Certifique-se de ter Java 21 instalado
2. Clone o repositório e execute `mvn clean compile`
3. Execute `mvn spring-boot:run` para iniciar a aplicação
4. Acesse http://localhost:8080 com credenciais demo/demo

### Adicionando Novos Processos
1. Crie arquivos .bpmn em `src/main/resources/bpmn/`
2. Implemente delegates necessários em `com.mls.workflow.camunda.delegate`
3. Configure beans de configuração se necessário

### Trabalhando com External Tasks
1. Implemente workers em `com.mls.workflow.camunda.external`
2. Configure topics e polling adequadamente
3. Teste com o Camunda Admin

### Estrutura de Services
1. Implemente lógica de negócio em `com.mls.workflow.core.service`
2. Use DTOs para transferência de dados
3. Implemente tratamento de exceções customizadas

### Debugging
- Use as ferramentas do Camunda Admin para visualizar instâncias
- Configure breakpoints em delegates para debugging
- Monitore logs para troubleshooting

## API REST Implementada

### Endpoint Principal
- **POST /api/cadastro/process** - Inicia processo BPMN CRUD

### Exemplo de Uso
```bash
# CREATE - Criar novo registro
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "CREATE", "payload": {"nome": "João", "email": "joao@teste.com", "idade": 30}}'

# READ - Buscar registro por ID  
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "READ", "id": 1}'

# UPDATE - Atualizar registro
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "UPDATE", "id": 1, "payload": {"nome": "João Silva", "email": "joao.silva@teste.com", "idade": 35}}'

# DELETE - Deletar registro
curl -X POST http://localhost:8081/api/cadastro/process \
  -H "Content-Type: application/json" \
  -d '{"tarefa": "DELETE", "id": 1}'
```

### Resposta Padrão (202 Accepted)
```json
{
  "processInstanceId": "12345678-1234-1234-1234-123456789012",
  "businessKey": "1"
}
```

## Arquitetura da Aplicação

### Fluxo de Dados CRUD via BPMN
1. **Entrada**: REST API recebe requisição CRUD
2. **Orquestração**: Processo BPMN roteia via gateway exclusivo
3. **Processamento**: Delegates executam operação específica (CREATE/READ/UPDATE/DELETE)
4. **Persistência**: H2 Database (tabela AIC_CADASTRO)
5. **Saída**: Resultado retornado via variáveis de processo

### Componentes Principais
- **ProcessController**: Endpoint REST que inicia processo BPMN
- **CadastroService**: Lógica de negócio para operações CRUD
- **Delegates**: Bridge entre BPMN e serviços Java
- **DTOs**: Transferência de dados (ProcessRequestDto, CadastroDto, etc.)
- **Global Exception Handler**: Tratamento centralizado de erros

### Integrações
- **Spring Boot 3.3.0**: Framework principal
- **Camunda BPM 7.23.0**: Engine de workflow BPMN
- **H2 Database**: Persistência file-based
- **Swagger/OpenAPI**: Documentação automática da API
- **Spring Boot Actuator**: Observabilidade (health, metrics)

### Pontos de Extensão
- Adicione delegates em `camunda.delegate` para novas service tasks
- Implemente listeners em `camunda.handler` para eventos
- Configure external task workers em `camunda.external`  
- Adicione serviços de negócio em `core.service`
- Estenda validações em `core.validation`