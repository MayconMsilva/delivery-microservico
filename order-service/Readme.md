# Order Service

Microsserviço responsável pela criação e consulta de pedidos do Delivery Beta, e pela publicação de eventos de domínio.

## Responsabilidades

- Criação de pedidos com múltiplos itens, calculando o total no servidor
- Consulta de pedidos
- Publicação assíncrona do evento `OrderCreated` no RabbitMQ a cada pedido criado

## Autorização

Todos os endpoints exigem um JWT válido. `CUSTOMER` só acessa os próprios pedidos; `ADMIN` acessa qualquer um.

## Tecnologias

Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security, JJWT 0.12.6, PostgreSQL, Spring AMQP (RabbitMQ).

## Porta

`8083`

## Banco de dados

`order_db` (PostgreSQL)

## Endpoints

Todos exigem o header `Authorization: Bearer <token>`.

### `POST /orders`

Cria um pedido para o usuário autenticado.

**Request:**
```json
{
  "items": [
    { "productName": "Pizza Grande", "quantity": 2, "price": 45.90 },
    { "productName": "Refrigerante 2L", "quantity": 1, "price": 12.00 }
  ]
}
```

**Response `200`:**
```json
{
  "id": 1,
  "status": "CREATED",
  "total": 103.80,
  "createdAt": "2026-01-01T12:00:00Z"
}
```

**Response `400`** (lista de itens vazia ou item inválido): mensagem específica do campo que falhou na validação.

### `GET /orders/{id}`

Consulta um pedido pelo id. `CUSTOMER` só acessa o próprio pedido.

## Mensageria

Ao criar um pedido com sucesso, o Order Service publica o evento `OrderCreated` na exchange `delivery.exchange`, com routing key `order.created`, roteado para a fila `order.created.queue`.

**Payload do evento:**
```json
{
  "orderId": 1,
  "customerId": 1,
  "createdAt": "2026-01-01T12:00:00Z"
}
```

Se o RabbitMQ estiver indisponível no momento da publicação, o pedido ainda é criado e a resposta `200` é devolvida normalmente — a falha de publicação é apenas registrada em log, para não acoplar a disponibilidade da criação de pedidos à disponibilidade do broker de mensagens.

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o PostgreSQL (`order_db`) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Mesma chave secreta usada no Auth Service |
| `SPRING_RABBITMQ_HOST` | Host do RabbitMQ |
| `SPRING_RABBITMQ_PORT` | Porta do RabbitMQ (padrão `5672`) |

## Como rodar isoladamente

```bash
cd order-service
./mvnw spring-boot:run
```

Requer um PostgreSQL acessível com o banco `order_db` criado, um RabbitMQ disponível, e o Auth Service para gerar tokens válidos.