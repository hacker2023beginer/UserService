# User Service

User Service stores and manages user profiles and payment cards. It also validates users for other services and protects user-related operations with JWT-based authorization.

Repository: https://github.com/hacker2023beginer/UserService

## Tech Stack

- Java
- Spring Boot 3.5.3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis cache
- Liquibase
- MapStruct
- JJWT
- JUnit, Mockito, Testcontainers
- Docker
- Kubernetes manifests

## Responsibilities

- User CRUD operations.
- User activation and deactivation.
- Payment card CRUD operations.
- User/card ownership checks.
- User validation by `userId` and `email`.
- JWT authentication and method-level authorization.
- PostgreSQL schema management with Liquibase.
- Redis-backed caching.

## Configuration

Default port:

```properties
server.port=8082
```

Required environment variables:
| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `REDIS_HOST` | Redis host |
| `REDIS_PORT` | Redis port |
| `JWT_SECRET` | JWT signing secret |

Liquibase changelog: spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml

### Security
Public endpoints:
| Method | Endpoint |
|---|---|
| POST | `/users` |
| DELETE | `/users/rollback/**` |
| GET | `/users/validate` |
| ANY | `/actuator/**` |

All other endpoints require a valid JWT.
Role and ownership rules are enforced with @PreAuthorize, for example:
- users can access their own profile;
- admins can access all users;
- card access is checked by ownership;
- administrative actions require ADMIN.

## API Reference

### Users
| Method | Endpoint | Description |
|---|---|---|
| POST | `/users` | Create user |
| GET | `/users/{id}` | Get user by id |
| GET | `/users/by-email?email={email}` | Get user by email |
| GET | `/users` | Get paginated users with optional `name` and `surname` filters |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Delete user |
| DELETE | `/users/rollback/{id}` | Roll back user creation |
| PATCH | `/users/{id}/activate` | Activate user |
| PATCH | `/users/{id}/deactivate` | Deactivate user |
| GET | `/users/{id}/cards` | Get all cards for user |
| GET | `/users/validate?userId={id}&email={email}` | Validate user identity |

User payload:
```
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "birthDate": "1995-05-20",
  "email": "john@example.com",
  "active": true
}
```

### Payment Cards
| Method | Endpoint | Description |
|---|---|---|
| POST | `/cards` | Create card |
| GET | `/cards/{id}` | Get card by id |
| PUT | `/cards/{id}` | Update card |
| DELETE | `/cards/{id}` | Delete card |
| PATCH | `/cards/{id}/activate` | Activate card |
| PATCH | `/cards/{id}/deactivate` | Deactivate card |

Card payload:
```
{
  "id": 1,
  "number": "1234567812345678",
  "holder": "John Doe",
  "expirationDate": "2030-12-31",
  "active": true,
  "userId": 1
}
```

## Run Locally
Start PostgreSQL and Redis, then provide environment variables and run: ./mvnw spring-boot:run

Docker: docker build -t user-service . && docker run -p 8082:8082 user-service

Tests: ./mvnw test
