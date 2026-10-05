# Delivery Beta

Sistema de delivery construído como projeto de estudo de arquitetura de microsserviços, utilizando Java, Spring Boot, Docker, JWT e mensageria assíncrona com RabbitMQ.

O objetivo do projeto é demonstrar, na prática, os principais conceitos de uma arquitetura distribuída: autenticação stateless, autorização em múltiplas camadas, banco de dados isolado por serviço (*database per service*), comunicação síncrona (REST) e assíncrona (eventos), e orquestração via containers.

## Arquitetura

```
                 ┌──────────────────┐
                 │   Cliente HTTP   │
                 └────────┬─────────┘
                          │
     ┌───────────┬────────┴─────────┬───────────┐
     ▼           ▼                  ▼           ▼
┌─────────┐ ┌──────────┐      ┌──────────┐ ┌──────────┐
│  Auth   │ │ Customer │      │  Order   │ │ Delivery │
│ Service │ │ Service  │      │ Service  │ │ Service  │
└────┬────┘ └────┬─────┘      └────┬─────┘ └────┬─────┘
     │           │                 │             ▲
     ▼           ▼                 │             │
 auth_db    customer_db            │        order.created
                                   ▼             │
                              order_db      ┌─────┴──────┐
                                   │         │  RabbitMQ  │
                                   └────────▶│  (evento)  │
                                             └────────────┘
                                                   │
                                                   ▼
                                             delivery_db
```

Cada microsserviço é um projeto Spring Boot independente, com seu próprio banco PostgreSQL. A comunicação entre o Order Service e o Delivery Service acontece de forma assíncrona: ao criar um pedido, um evento `OrderCreated` é publicado no RabbitMQ e consumido automaticamente pelo Delivery Service, que cria a entrega correspondente.

## Microsserviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| [auth-service](./auth-service) | 8081 | Cadastro, login, geração e validação de JWT |
| [customer-service](./curstomer-service) | 8082 | Cadastro, consulta e atualização de clientes |
| [order-service](./order-service) | 8083 | Criação e consulta de pedidos; publica eventos |
| [delivery-service](./delivery-service) | 8084 | Consome eventos de pedido e gerencia entregas |

Cada serviço tem seu próprio README com detalhes específicos de endpoints e regras de negócio.

## Tecnologias

- **Java 21** (LTS) + **Spring Boot 4.1**
- **Spring Web**, **Spring Data JPA**, **Spring Security**
- **JWT** (JJWT 0.12.6) — autenticação stateless, assinatura HMAC-SHA256
- **PostgreSQL 16** — um banco por serviço
- **RabbitMQ** — mensageria assíncrona (Direct Exchange)
- **Docker** + **Docker Compose** — orquestração de todo o ambiente
- **BCrypt** — hashing de senha

## Fluxo de autenticação

1. O cliente se autentica no Auth Service (`/auth/login`) e recebe um JWT assinado, contendo `sub` (id do usuário), `role` e expiração.
2. Esse mesmo token é enviado no header `Authorization: Bearer <token>` para qualquer outro serviço.
3. Cada microsserviço valida o token de forma independente, usando a mesma chave secreta compartilhada (stateless — nenhuma chamada de volta ao Auth Service é necessária).
4. Além da validação de assinatura, cada serviço aplica uma regra de autorização a nível de dado: um usuário `CUSTOMER` só acessa os próprios registros; `ADMIN` acessa qualquer um.

## Fluxo de criação de pedido e entrega

1. Cliente autenticado chama `POST /orders` no Order Service.
2. O pedido é salvo no banco `order_db`, com o total calculado a partir dos itens.
3. O Order Service publica o evento `OrderCreated` (routing key `order.created`) no RabbitMQ.
4. O Delivery Service, que mantém um consumidor conectado à fila `order.created.queue`, recebe o evento automaticamente e cria a entrega correspondente, com status inicial `PENDING`.
5. Se o RabbitMQ estiver indisponível no momento da publicação, o pedido ainda é criado normalmente — a falha de publicação é registrada em log, sem bloquear a resposta ao cliente.

## Como executar

Pré-requisito: Docker e Docker Compose instalados.

1. Copie o arquivo de exemplo de variáveis de ambiente e ajuste os valores se necessário:
   ```bash
   cp .env.example .env
   ```

2. Suba todo o ambiente:
   ```bash
   docker compose up --build
   ```

3. Os serviços ficam disponíveis em:
    - Auth Service: `http://localhost:8081`
    - Customer Service: `http://localhost:8082`
    - Order Service: `http://localhost:8083`
    - Delivery Service: `http://localhost:8084`
    - Painel do RabbitMQ: `http://localhost:15672` (usuário/senha: `guest`/`guest`)

4. Para encerrar:
   ```bash
   docker compose down
   ```

## Como testar

Cada README de serviço traz exemplos de requisições (curl/Insomnia) para seus endpoints. Um fluxo mínimo de ponta a ponta:

```
1. POST /auth/register  → cadastra um usuário
2. POST /auth/login      → retorna o token JWT
3. POST /customers        → cria o cliente (com o token)
4. POST /orders            → cria um pedido (dispara o evento OrderCreated)
5. GET /deliveries/{orderId} → confirma que a entrega foi criada automaticamente
```

## Estrutura do repositório

```
delivery/
├── auth-service/
├── curstomer-service/
├── order-service/
├── delivery-service/
├── docker-compose.yml
├── init-db.sql
├── .env.example
└── README.md
```

## Decisões de arquitetura

- **Database per service**: cada serviço tem seu próprio banco, sem chaves estrangeiras entre eles. Referências entre serviços (ex: `userId`, `customerId`) são lógicas, validadas via JWT ou via evento, nunca via FK de banco.
- **JWT validado em múltiplas camadas**: cada serviço valida o token de forma independente, em vez de confiar cegamente em uma única camada de borda — defesa em profundidade.
- **Publicação de evento não bloqueante**: uma falha no RabbitMQ não impede a criação de um pedido, evitando acoplar a disponibilidade de um serviço à de outro.
- **Consumo idempotente**: o Delivery Service verifica se já existe uma entrega para o `orderId` antes de criar, evitando duplicação em caso de reprocessamento de mensagem.

## Próximos passos (Delivery V2)

- OAuth2 / OpenID Connect
- Circuit Breaker e retries (Resilience4j)
- Dead Letter Queue e garantias de entrega mais robustas (outbox pattern)
- Observabilidade (logs estruturados, métricas, tracing)
- API Gateway
- Testes de integração com Testcontainers
- Deploy em Kubernetes

