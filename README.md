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
   - Interface Web: http://localhost:8080
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
- **Localização:** `./camunda-h2-database`
- **URL:** `jdbc:h2:file:./camunda-h2-database`
- **Console H2:** Disponível em http://localhost:8080/h2-console (se habilitado)

## 🔐 Credenciais Padrão

- **Usuário Admin:** `demo`
- **Senha:** `demo`
- **Acesso:** Camunda Cockpit, Tasklist e Admin

## 📊 Funcionalidades

### Camunda BPM
- ✅ Engine de workflow completo
- ✅ Interface web administrativa (Cockpit, Tasklist, Admin)
- ✅ API REST para automação
- ✅ Suporte a BPMN 2.0, DMN 1.3
- ✅ Histórico de processos com TTL de 180 dias

### Spring Boot
- ✅ Auto-configuração do Camunda
- ✅ Injeção de dependências
- ✅ Configuração via YAML
- ✅ Profiles de ambiente
- ✅ Logging estruturado

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
