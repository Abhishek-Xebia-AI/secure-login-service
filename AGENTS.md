# AGENTS.md — Secure Login Service

## 1. Stack

| Technology | Role |
|---|---|
| **Java 21** | Primary language (use virtual threads where applicable) |
| **Spring Boot 3.x** | Application framework, auto-configuration, embedded Tomcat |
| **Spring Security 6.x** | Authentication filter chain, password encoding, security context |
| **Spring Data JPA** | Repository layer, entity management |
| **Spring Mail (Jakarta Mail)** | Email verification code dispatch |
| **JJWT (io.jsonwebtoken) 0.12.x** | JWT creation, signing, and validation |
| **PostgreSQL** | Primary relational data store |
| **Flyway** | Database schema migrations |
| **Lombok** | Boilerplate reduction (getters, builders, constructors) |
| **MapStruct** | DTO ↔ entity mapping |
| **JUnit 5 + Mockito** | Unit and slice testing |
| **Testcontainers** | Integration tests with real PostgreSQL container |
| **WireMock** | Stub external HTTP/SMTP dependencies in tests |
| **Gradle (Kotlin DSL)** | Build tool and dependency management |
| **Docker / Docker Compose** | Containerised runtime and local dev environment |
| **GitHub Actions** | CI pipeline |

---

## 2. Project Structure

```
secure-login-service/
├── AGENTS.md                          # This file
├── tasks.md                           # Agent-generated task tracker (created before coding)
├── build.gradle.kts                   # Gradle build script (Kotlin DSL)
├── settings.gradle.kts                # Project name declaration
├── gradle/
│   └── libs.versions.toml             # Version catalogue for all dependencies
├── Dockerfile                         # Multi-stage production image
├── docker-compose.yml                 # Local dev stack (app + postgres + mailhog)
├── docker-compose.test.yml            # Integration test overrides
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions pipeline
├── src/
│   ├── main/
│   │   ├── java/com/company/login/
│   │   │   ├── SecureLoginApplication.java          # @SpringBootApplication entry point
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java              # SecurityFilterChain, CORS, CSRF rules
│   │   │   │   ├── JwtConfig.java                   # JWT secret, expiry @ConfigurationProperties
│   │   │   │   ├── MailConfig.java                  # JavaMailSender bean configuration
│   │   │   │   └── FlywayConfig.java                # Flyway baseline settings (if needed)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java              # POST /auth/login, POST /auth/verify-email
│   │   │   │   └── advice/
│   │   │   │       └── GlobalExceptionHandler.java  # @RestControllerAdvice, ProblemDetail responses
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequest.java            # email + password fields, Bean Validation
│   │   │   │   │   └── EmailVerificationRequest.java# email + OTP code fields
│   │   │   │   └── response/
│   │   │   │       ├── LoginResponse.java           # accessToken, tokenType, expiresIn
│   │   │   │       └── MessageResponse.java         # Generic message wrapper
│   │   │   ├── entity/
│   │   │   │   ├── User.java                        # @Entity: id, email, passwordHash, verified, etc.
│   │   │   │   └── EmailVerificationToken.java      # @Entity: token, user FK, expiresAt, used flag
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java              # JpaRepository<User, UUID>
│   │   │   │   └── EmailVerificationTokenRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java                 # Orchestrates login + verification flow
│   │   │   │   ├── JwtService.java                  # Token generation, parsing, validation
│   │   │   │   ├── EmailVerificationService.java    # OTP generation, send, validate
│   │   │   │   └── UserDetailsServiceImpl.java      # Implements Spring UserDetailsService
│   │   │   ├── security/
│   │   │   │   ├── JwtAuthenticationFilter.java     # OncePerRequestFilter — validates Bearer token
│   │   │   │   └── SecurityUser.java                # UserDetails adapter wrapping User entity
│   │   │   ├── exception/
│   │   │   │   ├── InvalidCredentialsException.java
│   │   │   │   ├── EmailNotVerifiedException.java
│   │   │   │   ├── TokenExpiredException.java
│   │   │   │   └── UserNotFoundException.java
│   │   │   └── util/
│   │   │       ├── OtpGenerator.java                # Secure random OTP generation
│   │   │       └── DateTimeUtils.java               # UTC instant helpers
│   │   └── resources/
│   │       ├── application.yml                      # Base configuration (references env vars)
│   │       ├── application-local.yml                # Local dev overrides
│   │       ├── application-test.yml                 # Test profile config
│   │       └── db/migration/
│   │           ├── V1__create_users_table.sql
│   │           ├── V2__create_email_verification_tokens_table.sql
│   │           └── V3__add_indexes.sql
│   └── test/
│       ├── java/com/company/login/
│       │   ├── controller/
│       │   │   └── AuthControllerTest.java          # @WebMvcTest slice tests
│       │   ├── service/
│       │   │   ├── AuthServiceTest.java             # Pure unit tests with Mockito
│       │   │   ├── JwtServiceTest.java
│       │   │   └── EmailVerificationServiceTest.java
│       │   ├── repository/
│       │   │   └── UserRepositoryTest.java          # @DataJpaTest with Testcontainers
│       │   ├── integration/
│       │   │   └── AuthFlowIntegrationTest.java     # @SpringBootTest full flow
│       │   └── util/
│       │       └── TestFixtures.java                # Shared test data builders
│       └── resources/
│           └── application-test.yml                 # Testcontainers datasource overrides
```

---

## 3. Required Workflow

The agent **must** follow these steps in order. Do not skip or reorder.

### Step 1 — Read Specifications
- Parse all story-level spec documents provided in context.
- Identify every endpoint, business rule, validation constraint, error scenario, and email flow.
- Note any security requirements (token expiry windows, OTP length/expiry, rate-limiting rules).

### Step 2 — Create `tasks.md`
- Create `tasks.md` in the project root **before writing any source code**.
- Structure it as a checklist with sections: `Setup`, `Entities & Migrations`, `Repository`, `Service`, `Security`, `Controller`, `Tests`, `Docker`, `CI`.
- Each task must be a single, verifiable action (e.g., `[ ] Implement JwtService#generateToken`).
- Tick tasks off (`[x]`) as they are completed.

### Step 3 — Scaffold & Implement
Follow this implementation order to respect dependency direction:

1. `build.gradle.kts` + `libs.versions.toml` — declare all dependencies.
2. `application.yml` — externalise every secret/URL as an environment variable.
3. Flyway migration SQL files — define schema before entities.
4. Entity classes → Repository interfaces.
5. `JwtConfig` + `MailConfig` + `SecurityConfig`.
6. `JwtService` → `EmailVerificationService` → `UserDetailsServiceImpl` → `AuthService`.
7. `JwtAuthenticationFilter`.
8. DTOs (use Bean Validation annotations: `@NotBlank`, `@Email`, `@Size`).
9. `AuthController` + `GlobalExceptionHandler`.
10. Exception classes.

### Step 4 — Write Tests
- Write tests **alongside** each implementation class, not at the end.
- Unit tests first, then slice tests, then integration tests.
- Every public method on a service class must have at least one happy-path and one failure-path test.

### Step 5 — Validate
Run the following commands and fix all failures before marking the task complete:

```bash
./gradlew clean test                   # All tests must pass
./gradlew jacocoTestReport             # Coverage report generated
./gradlew jacocoTestCoverageVerification  # Must meet 90% threshold
./gradlew spotlessCheck                # Code style must pass
./gradlew dependencyCheckAnalyze       # No critical CVEs
docker build -t secure-login-service . # Image must build successfully
```

---

## 4. Coding Conventions

### Naming
| Artefact | Convention | Example |
|---|---|---|
| Classes | `PascalCase` | `AuthService`, `JwtAuthenticationFilter` |
| Methods | `camelCase`, verb-first | `generateToken()`, `verifyEmailOtp()` |
| Constants | `UPPER_SNAKE_CASE` | `TOKEN_PREFIX`, `OTP_EXPIRY_MINUTES` |
| Database tables | `snake_case`, plural | `users`, `email_verification_tokens` |
| Database columns | `snake_case` | `password_hash`, `expires_at` |
| Environment variables | `UPPER_SNAKE_CASE` | `JWT_SECRET`, `MAIL_HOST` |
| Test classes | Mirror source + `Test` suffix | `AuthServiceTest` |

### Architecture Patterns
- **Layered architecture**: Controller → Service → Repository. No layer may skip a level.
- **No business logic in controllers.** Controllers validate input and delegate to services only.
- **No JPA entities in controller responses.** Always map to DTOs via MapStruct.
- **Services are `@Transactional` at the method level**, not class level. Mark read-only methods `@Transactional(readOnly = true)`.
- **Interfaces for services are not required** unless multiple implementations exist. Prefer concrete classes.
- **Secrets never in source code.** All sensitive values come from environment variables referenced in `application.yml` via `${ENV_VAR}`.
- Use `UUID` as the primary key type for all entities. Annotate with `@GeneratedValue(strategy = GenerationType.UUID)`.
- All timestamps stored as `Instant` in UTC. Use `DateTimeUtils` helpers consistently.
- Passwords encoded with `BCryptPasswordEncoder` (strength 12). Never log or return password fields.
- OTPs generated using `SecureRandom`. Minimum 6 digits. Stored as BCrypt hash, never plaintext.
- JWT secret must be at least 256 bits (32 bytes). Validate on startup with `@PostConstruct`.

### Style
- Use Lombok `@Builder`, `@Value` (for immutable DTOs), `@RequiredArgsConstructor` on services.
- No field injection (`@Autowired` on fields). Use constructor injection exclusively.
- Apply `spotless` with `googleJavaFormat` plugin. Configuration lives in `build.gradle.kts`.
- All public API methods must have Javadoc.
- Keep methods under 30 lines. Extract private helpers if needed.

---

## 5. Testing

### Test Layers

#### Unit Tests (`src/test/java/.../service/`, `.../util/`)
- Use JUnit 5 (`@ExtendWith(MockitoExtension.class)`).
- Mock all dependencies with `@Mock` / `@InjectMocks`.
- No Spring context loaded.
- Cover: happy path, invalid credentials, expired OTP, already-verified user, malformed JWT, null inputs.

#### Web Layer Slice Tests (`src/test/java/.../controller/`)
- Use `@WebMvcTest(AuthController.class)`.
- Mock the service layer with `@MockBean`.
- Use `MockMvc` to assert HTTP status codes, response body structure, and validation error messages.
- Test all 4xx and 5xx response shapes via `GlobalExceptionHandler`.

#### Repository Slice Tests (`src/test/java/.../repository/`)
- Use `@DataJpaTest` + `@AutoConfigureTestDatabase(replace = NONE)`.
- Use Testcontainers `PostgreSQLContainer` via a shared `AbstractIntegrationTest` base class.
- Test custom query methods, unique constraint violations, and cascade behaviour.

#### Integration Tests (`src/test/java/.../integration/`)
- Use `@SpringBootTest(webEnvironment = RANDOM_PORT)`.
- Use Testcontainers for PostgreSQL.
- Use WireMock or MailHog (via docker-compose.test.yml) to capture outbound emails.
- Test complete flows: register → send OTP → verify email → login → receive JWT.

### Coverage Requirements
```kotlin
// build.gradle.kts
jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.90".toBigDecimal()  // 90% instruction coverage
            }
        }
    }
}
```
- Exclusions allowed only for: generated MapStruct classes, Lombok-generated code, `*Application.java`, `*Config.java` (configuration beans).
- Coverage report published as CI artefact on every run.

### Running Tests
```bash
# All tests
./gradlew clean test

# Unit tests only
./gradlew test --tests "com.company.login.service.*"

# Integration tests only
./gradlew test --tests "com.company.login.integration.*"

# Coverage report (HTML at build/reports/jacoco/test/html/index.html)
./gradlew jacocoTestReport
```

---

## 6. Docker & CI

### Dockerfile
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace
COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN ./gradlew dependencies --no-daemon          # Cache dependency layer
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:+ZGenerational", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]
```

### docker-compose.yml (local dev)
```yaml
services:
  app:
    build: .
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: local
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: securelogin
      DB_USER: appuser
      DB_PASSWORD: secret
      JWT_SECRET: ${JWT_SECRET}          # Must be set in .env file
      JWT_EXPIRY_SECONDS: 3600
      MAIL_HOST: mailhog
      MAIL_PORT: 1025
    depends_on:
      postgres: { condition: service_healthy }

  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: securelogin
      POSTGRES_USER: appuser
      POSTGRES_PASSWORD: secret
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U appuser -d securelogin"]
      interval: 5s
      timeout: 5s
      retries: 5
    volumes:
      -