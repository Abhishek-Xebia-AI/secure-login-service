# Secure Login Service

A production-ready, stateless authentication microservice built with **Java 17**, **Spring Boot 3**, and **JWT**. It follows **hexagonal architecture** (ports & adapters) to keep the domain logic fully decoupled from infrastructure concerns.

---

## Features

| Feature | Details |
|---|---|
| **Secure Login** | Validates credentials, checks email-verification status, issues a signed JWT |
| **Email Verification** | Token-based email verification flow |
| **Health Check** | `GET /api/v1/health` — suitable for liveness/readiness probes |
| **Stateless** | No server-side sessions; every request is authenticated via JWT |
| **Hexagonal Architecture** | Domain → Application → Adapter layers; zero framework leakage into the domain |

---

## Technology Stack

- **Java 17** + **Spring Boot 3.2**
- **Spring Security** (stateless, JWT filter)
- **Spring Data JPA** + **PostgreSQL** (Flyway migrations)
- **JJWT 0.12** for JWT signing/validation
- **Lombok** for boilerplate reduction
- **H2** (in-memory, test scope only)

---

## Project Structure

```
src/main/java/com/securelogin/
├── SecureLoginServiceApplication.java      # Entry point
│
├── domain/                                 # Pure domain — no framework deps
│   ├── model/                              # Entities: User, EmailVerificationToken
│   ├── exception/                          # Domain exceptions
│   └── port/
│       ├── inbound/                        # Use-case interfaces (LoginUseCase, VerifyEmailUseCase)
│       └── outbound/                       # Repository & service interfaces
│
├── application/
│   └── service/                            # Use-case implementations (LoginService, EmailVerificationService)
│
├── adapter/
│   ├── inbound/
│   │   └── rest/                           # REST controllers + DTOs + GlobalExceptionHandler
│   └── outbound/
│       ├── persistence/                    # JPA entities + Spring Data repos + adapters
│       ├── jwt/                            # JwtAdapter (JJWT)
│       └── email/                          # SmtpEmailAdapter (Spring Mail)
│
└── infrastructure/
    └── config/                             # SecurityConfig, etc.

src/main/resources/
├── application.yml
└── db/migration/                           # Flyway SQL migrations
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose (optional, for local PostgreSQL)
- A running PostgreSQL instance

### 1. Configure environment

```bash
cp .env.example .env
# Edit .env and fill in DB credentials, JWT secret, SMTP settings
```

### 2. Run locally

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:8080` by default.

### 3. Run with Docker

```bash
# Build image
docker build -t secure-login-service:latest .

# Run (pass env vars)
docker run --env-file .env -p 8080:8080 secure-login-service:latest
```

---

## API Reference

### Health Check

```
GET /api/v1/health
```

**Response 200**
```json
{ "status": "UP" }
```

---

### Login

```
POST /api/v1/auth/login
Content-Type: application/json
```

**Request body**
```json
{
  "email": "user@example.com",
  "password": "s3cr3t"
}
```

**Response 200**
```json
{
  "accessToken": "<JWT>",
  "tokenType": "Bearer"
}
```

**Error responses**

| Status | Reason |
|---|---|
| `400` | Validation failure (blank/invalid email, blank password) |
| `401` | Invalid credentials or email not verified |

---

### Verify Email

```
GET /api/v1/auth/verify-email?token=<verification-token>
```

**Response 200** — email successfully verified  
**Response 400** — token not found, expired, or already used

---

## Running Tests

```bash
./mvnw test
```

Tests use an in-memory H2 database and the `test` Spring profile — no external services required.

---

## Environment Variables Reference

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8080` | HTTP port |
| `DB_URL` | `jdbc:postgresql://localhost:5432/securelogin` | JDBC URL |
| `DB_USERNAME` | `securelogin` | DB username |
| `DB_PASSWORD` | — | DB password |
| `JWT_SECRET` | — | HS256 signing secret (≥ 32 chars) |
| `JWT_EXPIRATION_MS` | `3600000` | Token TTL in ms (1 hour) |
| `SMTP_HOST` | — | SMTP server hostname |
| `SMTP_PORT` | `587` | SMTP port |
| `SMTP_USERNAME` | — | SMTP username |
| `SMTP_PASSWORD` | — | SMTP password |
| `EMAIL_FROM` | `noreply@securelogin.com` | Sender address |
| `APP_BASE_URL` | `http://localhost:8080` | Used in verification email links |

---

## Architecture Notes

This service follows the **Hexagonal Architecture** (Ports & Adapters) pattern:

- **Domain layer** — pure Java, zero framework dependencies. Contains entities and port interfaces.
- **Application layer** — orchestrates use-cases by calling outbound ports. No HTTP or JPA knowledge.
- **Adapter layer** — bridges the domain to the outside world (REST, JPA, SMTP, JWT).
- **Infrastructure layer** — Spring configuration (Security, etc.).

This separation ensures the business logic is independently testable and the adapters are swappable.

---

## License

MIT
