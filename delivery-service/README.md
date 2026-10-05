# Delivery Service

Microsserviço responsável pelo gerenciamento de entregas do Delivery Beta. É o único serviço do projeto cuja criação de registro não é disparada por uma requisição HTTP, e sim pelo consumo automático de um evento assíncrono.

## Responsabilidades

- Consumir o evento `OrderCreated`, publicado pelo Order Service, e criar a entrega correspondente
- Consulta de uma entrega pelo id do pedido associado

## Autorização

O endpoint de consulta exige um JWT válido. `CUSTOMER` só acessa a entrega do próprio pedido; `ADMIN` acessa qualquer uma. Não existe endpoint de criação pública — a criação é 100% dirigida por evento.

## Tecnologias

Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security, JJWT 0.12.6, PostgreSQL, Spring AMQP (RabbitMQ).

## Porta

`8084`

## Banco de dados

`delivery_db` (PostgreSQL)

## Endpoints

### `GET /deliveries/{orderId}`

Consulta a entrega associada a um pedido. Requer `Authorization: Bearer <token>`.

**Response `200`:**
```json
{
  "orderId": 1,
  "status": "PENDING",
  "createdAt": "2026-01-01T12:00:05Z"
}
```

**Response `403`:** usuário autenticado, mas sem permissão para ver esta entrega.
**Response `404`:** nenhuma entrega encontrada para este pedido.

## Consumo de eventos

O serviço mantém um consumidor (`@RabbitListener`) permanentemente conectado à fila `order.created.queue`. Ao receber uma mensagem `OrderCreated`:

1. Verifica se já existe uma entrega para aquele `orderId` (idempotência) — se existir, a mensagem é ignorada com um log de aviso, sem criar duplicata.
2. Caso contrário, cria a entrega com status inicial `PENDING`.

Se o processamento falhar por um motivo transitório (ex: banco de dados indisponível), a exceção não é tratada internamente — ela sobe para o Spring AMQP, que mantém a mensagem na fila para nova tentativa, em vez de descartá-la.

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o PostgreSQL (`delivery_db`) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Mesma chave secreta usada no Auth Service |
| `SPRING_RABBITMQ_HOST` | Host do RabbitMQ |
| `SPRING_RABBITMQ_PORT` | Porta do RabbitMQ (padrão `5672`) |

## Como rodar isoladamente

```bash
cd delivery-service
./mvnw spring-boot:run
```

Requer um PostgreSQL acessível com o banco `delivery_db` criado, e um RabbitMQ disponível. Para testar o fluxo completo, o Order Service também precisa estar rodando e publicando eventos.