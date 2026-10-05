# Auth Service

Microsserviço responsável pela autenticação e emissão de tokens JWT do Delivery Beta.

## Responsabilidades

- Cadastro de usuário (senha armazenada como hash BCrypt)
- Login, com geração de JWT assinado (HMAC-SHA256)
- Validação de credenciais com resposta genérica para email inexistente ou senha incorreta, prevenindo *user enumeration*

## Tecnologias

Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security, JJWT 0.12.6, PostgreSQL, BCrypt.

## Porta

`8081`

## Banco de dados

`auth_db` (PostgreSQL)

## Endpoints

### `POST /auth/register`

Cadastra um novo usuário. Endpoint público. A role é sempre `CUSTOMER` — não é possível se autopromover a `ADMIN` por este endpoint.

**Request:**
```json
{
  "email": "usuario@teste.com",
  "password": "senha12345",
  "name": "Nome do Usuário"
}
```

**Response `200`:**
```json
{
  "id": 1,
  "email": "usuario@teste.com",
  "name": "Nome do Usuário"
}
```

### `POST /auth/login`

Autentica o usuário e retorna o token JWT. Endpoint público.

**Request:**
```json
{
  "email": "usuario@teste.com",
  "password": "senha12345"
}
```

**Response `200`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600
}
```

**Response `401`** (email inexistente OU senha incorreta — mesma mensagem para os dois casos):
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Email ou senha inválidos"
}
```

## O que vai dentro do token

```json
{
  "sub": "1",
  "email": "usuario@teste.com",
  "role": "CUSTOMER",
  "iat": 1234567890,
  "exp": 1234571490
}
```

Esse token deve ser enviado no header `Authorization: Bearer <token>` para acessar os demais microsserviços do sistema.

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexão com o PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave secreta usada para assinar e validar o token (mínimo 32 bytes) — deve ser **idêntica** em todos os microsserviços |

## Como rodar isoladamente

```bash
cd auth-service
./mvnw spring-boot:run
```

Requer um PostgreSQL acessível com o banco `auth_db` criado.