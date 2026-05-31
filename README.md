# Smart Card Authorizer

Microserviço de autorização de transações de cartão de crédito/débito, simulando as principais operações financeiras: compra à vista, compra parcelada, saque e pagamento.

---

## Desafios Atendidos

| Requisito | Status |
|-----------|--------|
| Java 22 Records para todos os DTOs (Request/Response) | IMPLEMENTADO |
| Constructor Injection em todas as classes (sem `@Autowired` em campo) | IMPLEMENTADO |
| JavaDoc 100% em classes públicas, métodos e records | IMPLEMENTADO |
| HTTP status correto: `201 CREATED` para POST com `Location` header | IMPLEMENTADO |
| `@RestControllerAdvice` centralizado com tipos de exceção de domínio | IMPLEMENTADO |
| Exceções de domínio (`AccountNotFoundException`, `OperationTypeNotFoundException`, `InvalidTransactionException`) | IMPLEMENTADO |
| Imutabilidade: `TransactionsRequest.withAmount()` substitui mutação por novo record | IMPLEMENTADO |
| `LocalDateTime` (Java) substituindo Joda-Time | IMPLEMENTADO |
| Credenciais via variáveis de ambiente (sem hardcode no repositório) | IMPLEMENTADO |
| `@Service` correto em implementações (removido de interfaces e substituído `@Repository` errado) | IMPLEMENTADO |
| Sem wildcards nos imports — todos explícitos e organizados | IMPLEMENTADO |
| Constantes com construtor privado (utility class) | IMPLEMENTADO |
| Cobertura de testes unitários: happy path, edge cases e erros | IMPLEMENTADO |
| Refactoring do `docker-compose.yml` com variáveis de ambiente | IMPLEMENTADO |

---

## Stack Tecnológica

| Tecnologia | Versão | Papel |
|-----------|--------|-------|
| Java | 22 | Linguagem principal (Records, Pattern Matching) |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Data MongoDB | 3.2.x | Persistência de dados |
| Spring AMQP (RabbitMQ) | 3.2.x | Comunicação assíncrona por eventos |
| Spring Validation | 3.2.x | Validação de entrada (`@Valid`, `@NotBlank`) |
| Lombok | latest | Apenas `@Slf4j` — sem `@Data`/`@Builder` em records |
| JUnit 5 + Mockito | latest | Testes unitários |
| JaCoCo | 0.8.11 | Cobertura de código (mínimo 80%) |
| MongoDB | latest | Banco de dados NoSQL |
| RabbitMQ | 3.8+ | Message broker para eventos de domínio |
| Docker & Docker Compose | latest | Infraestrutura local |

---

## Arquitetura

```
src/main/java/io/github/emersondll/transactions/
├── controller/          # Camada de apresentação (REST endpoints)
│   ├── AccountController.java
│   ├── TransactionsController.java
│   ├── Handler.java     # @RestControllerAdvice global
│   └── BaseController.java
├── service/             # Contratos de negócio (interfaces)
│   ├── AccountService.java
│   ├── TransactionsService.java
│   ├── OperationsTypeService.java
│   └── RabbitMqService.java
│   └── impl/            # Implementações de negócio
│       ├── AccountServiceImpl.java
│       ├── TransactionsServiceImpl.java
│       ├── OperationsTypeServiceImpl.java
│       └── RabbitMqServiceImpl.java
├── repository/          # Camada de persistência (MongoDB)
├── document/            # Entidades MongoDB
├── model/
│   ├── request/         # Records de entrada (AccountRequest, TransactionsRequest)
│   └── response/        # Records de saída (AccountResponse, AccountDetailResponse, ...)
├── mapper/              # Conversão entre camadas
├── exception/           # Exceções de domínio
└── config/              # Configurações (MongoDB DDL, RabbitMQ)
```

**Fluxo:** `Controller → Service → Repository → MongoDB`  
**Eventos:** `Service → RabbitMqService → RabbitMQ queues`

---

## Como Executar Localmente

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 22 instalado (verificar com `java -version`)
- Maven 3.9+ instalado

### 1. Subir a infraestrutura (MongoDB + RabbitMQ)

```bash
cd src/main/resources
docker-compose up -d
```

Isso sobe:
- MongoDB na porta `27017`
- RabbitMQ na porta `5672` (management em `15672`)

### 2. Configurar variáveis de ambiente (opcional para dev local)

O `application.properties` já possui fallbacks para desenvolvimento local:
- MongoDB: `mongodb://localhost:27017/transactions`
- RabbitMQ: `amqp://localhost:5672` com usuário `admin`/`admin`

Para ambiente customizado, exporte as variáveis antes de rodar:

```bash
export MONGO_URI=mongodb://localhost:27017/transactions
export RABBITMQ_ADDRESSES=amqp://localhost:5672
export RABBITMQ_USERNAME=admin
export RABBITMQ_PASSWORD=admin
```

### 3. Compilar e executar

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### 4. Executar os testes

```bash
./mvnw test
```

Para relatório de cobertura JaCoCo:

```bash
./mvnw test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

---

## Endpoints da API

Base URL: `http://localhost:8080/digital/transactions/v1`

### Contas

#### Criar conta

```http
POST /accounts
Content-Type: application/json

{
  "documentNumber": "64771015058"
}
```

**Resposta:** `201 CREATED`
```json
{
  "accountId": "fdd7960b-ce3e-454a-8dc7-52b3d8665fbe"
}
```

#### Buscar conta

```http
GET /accounts/{accountId}
```

**Resposta:** `200 OK`
```json
{
  "accountId": "fdd7960b-ce3e-454a-8dc7-52b3d8665fbe",
  "documentNumber": "64771015058"
}
```

### Transações

#### Registrar transação

```http
POST /transactions
Content-Type: application/json

{
  "accountId": "fdd7960b-ce3e-454a-8dc7-52b3d8665fbe",
  "operationTypeId": "1",
  "amount": 123.45
}
```

Tipos de operação: `1` = Compra à vista, `2` = Compra parcelada, `3` = Saque, `4` = Pagamento

**Resposta:** `201 CREATED`
```json
{
  "transactionsId": "...",
  "accountId": "...",
  "operationTypeId": "1",
  "amount": -123.45,
  "eventDate": "2026-05-31T10:00:00"
}
```

#### Consultar saldo

```http
GET /transactions/balance/{documentNumber}
```

**Resposta:** `200 OK`
```json
{
  "amount": -246.90
}
```

### Códigos de Resposta

| Código | Situação |
|--------|---------|
| `200 OK` | Consulta realizada com sucesso |
| `201 CREATED` | Recurso criado com sucesso |
| `400 BAD REQUEST` | Campos obrigatórios ausentes ou inválidos |
| `404 NOT FOUND` | Conta ou tipo de operação não encontrado |
| `500 INTERNAL SERVER ERROR` | Erro inesperado no servidor |

---

## Exemplo via cURL

```bash
# Criar conta
curl -s -X POST http://localhost:8080/digital/transactions/v1/accounts \
  -H 'Content-Type: application/json' \
  -d '{"documentNumber": "64771015058"}'

# Buscar conta
curl -s http://localhost:8080/digital/transactions/v1/accounts/{accountId}

# Registrar transação
curl -s -X POST http://localhost:8080/digital/transactions/v1/transactions \
  -H 'Content-Type: application/json' \
  -d '{"accountId": "{accountId}", "operationTypeId": "1", "amount": 150.00}'

# Consultar saldo
curl -s http://localhost:8080/digital/transactions/v1/transactions/balance/64771015058
```

---

## Demonstração

<p align="center">
  <img title="Demo" src="src/main/resources/gif/Demo.gif" width="800px">
</p>

---

## Desenvolvedor

| Campo | Informação                                                                |
|-------|---------------------------------------------------------------------------|
| **Nome** | Emerson Lima                                                              |
| **GitHub** | [github.com/Emersondll](https://github.com/Emersondll)                    |
| **LinkedIn** | [linkedin.com/in/emersondll](https://www.linkedin.com/in/stackdeveloper/) |

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/stackdeveloper/)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Emersondll)
