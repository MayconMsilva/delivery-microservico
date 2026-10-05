# Customer Service

Microsserviço responsável pela gestão de dados de cliente do Delivery Beta.

> Nota: a pasta deste serviço no repositório está nomeada `curstomer-service` por um erro de digitação original do projeto. O nome lógico do serviço, suas variáveis de ambiente e banco de dados usam a grafia correta (`customer-service`, `customer_db`).

## Responsabilidades

- Cadastro de cliente, vinculado ao usuário autenticado (via `userId` extraído do token)
- Consulta de um cliente específico
- Atualização dos dados de um cliente

## Autorização

Todos os endpoints exigem um JWT válido — não há endpoint público neste serviço. Além da autenticação, há uma regra de **autorização a nível de dado**: um usuário com role `CUSTOMER` só pode acessar ou alterar o **próprio** registro; `ADMIN` pode acessar qualquer um.

## Tecnologias

Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security, JJWT 0.12.6, PostgreSQL.

## Porta

`8082`

## Banco de dados

`customer_db` (PostgreSQL) — isolado do `auth_db`, sem chave estrangeira entre eles. A ligação entre um `Customer` e seu `User` é feita por referência lógica (`userId`), validada em tempo de execução pelo JWT.

## Endpoints

Todos exigem o header `Authorization: Bearer <token>`.

### `POST /customers`

Cadastra um cliente para o usuário autenticado. Um usuário só pode ter um cliente associado.

**Request:**
```json
{
  "name": "Nome do Cliente",
  "phone": "11999999999",
  "address": "Rua Exemplo, 123"
}
```

**Response `200`:**
```json
{
  "id": 1,
  "name": "Nome do Cliente",
  "phone": "11999999999",
  "address": "Rua Exemplo, 123"
}
```

**Response `409`** (usuário já possui um cliente cadastrado):
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Usuário já possui um cliente cadastrado"
}
```

### `GET /customers/{id}`

Consulta um cliente pelo id. `CUSTOMER` só acessa o próprio registro.

**Response `200`:** dados do cliente.
**Response `403`:** usuário autenticado, mas sem permissão para ver este registro.
**Response `404`:** cliente não encontrado.

### `PUT /customers/{id}`

Atualiza os dados de um cliente. Mesma regra de autorização do `GET`.

**Request:**
```json
{
  "name": "Novo Nome",
  "phone": "11888888888",
  "address": "Novo Endereço"
}
```

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o PostgreSQL (`customer_db`) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Mesma chave secreta usada no Auth Service |

## Como rodar isoladamente

```bash
cd curstomer-service
./mvnw spring-boot:run
```

Requer um PostgreSQL acessível com o banco `customer_db` criado, e o Auth Service disponível para gerar tokens válidos.