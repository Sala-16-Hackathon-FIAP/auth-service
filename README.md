# auth-service

Authentication service for the FIAP-X video processing platform. Handles user registration, login, and JWT token generation/validation.

![FIAP-X platform architecture](docs/architecture.png)

> High-level architecture of the FIAP-X platform — microservices, choreographed saga over RabbitMQ, database-per-service (RDS), object storage (S3), running on EKS and provisioned with Terraform.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.5.0 | Framework |
| Spring Security | 6.x | Security and access control |
| Spring Data JPA | 3.x | Persistence |
| PostgreSQL | 16 | Database |
| Flyway | 11.8.2 | Database migrations |
| JJWT | 0.12.6 | JWT token generation and validation |
| JaCoCo | 0.8.14 | Test coverage (minimum 80%) |
| New Relic | 8.15.0 | Monitoring/APM |
| SpringDoc OpenAPI | 2.8.8 | Swagger documentation |
| Docker | - | Containerization (multi-stage build) |
| Kubernetes | - | Orchestration (EKS deploy) |

---

## Architecture

The project follows **Hexagonal Architecture (Ports & Adapters)**:

```
src/main/java/br/com/fiapx/auth/
├── domain/              # Domain entities and exceptions
│   ├── model/           # User, UserRole
│   └── exception/       # InvalidCredentials, UserAlreadyExists, UserNotFound
├── application/         # Use cases and services
│   ├── port/input/      # AuthUseCase (interface)
│   ├── port/output/     # UserRepositoryPort (interface)
│   └── service/         # AuthService, JwtService
└── infrastructure/      # External adapters
    ├── config/          # OpenAPI, DataInitializer
    ├── persistence/     # JPA entities, repository adapter
    ├── rest/            # Controllers, DTOs, exception handler
    ├── security/        # SecurityConfig
    └── monitoring/      # New Relic tracker
```

---

## APIs

### POST `/api/v1/auth/login`

Authenticates a user and returns a JWT token.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass@123"
}
```

**Response (200):**
```json
{
  "bearerToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

**Errors:** `401` invalid credentials | `400` validation error

---

### POST `/api/v1/auth/register`

Registers a new user.

**Request:**
```json
{
  "email": "new@example.com",
  "username": "newuser",
  "password": "SecurePass@123",
  "role": "USER"
}
```

**Response (201):**
```json
{
  "id": "uuid",
  "email": "new@example.com",
  "username": "newuser",
  "role": "USER",
  "createdAt": "2026-07-05T10:00:00"
}
```

**Errors:** `409` email/username already exists | `400` validation error

---

### Other endpoints

| Endpoint | Description |
|---|---|
| `GET /actuator/health` | Health check |
| `GET /actuator/info` | Application info |
| `GET /actuator/metrics` | Metrics |
| `GET /actuator/prometheus` | Prometheus-format metrics |
| `GET /swagger-ui.html` | Interactive API documentation (Swagger UI) |
| `GET /api-docs` | OpenAPI spec (JSON) |

---

## Running Locally

### Prerequisites

- Java 21
- Maven 3.9+
- Docker (for PostgreSQL)

### 1. Start the database

```bash
docker compose up -d
```

This starts PostgreSQL on port **5433** with:
- Database: `fiapx_auth`
- User: `fiapx`
- Password: `fiapx123`

### 2. Run the application

**Via Maven:**
```bash
mvn spring-boot:run
```

**Via IDE (IntelliJ):**
- Run the `AuthServiceApplication` class
- No extra configuration needed (defaults in `application.yml` point to localhost:5433)

The application starts on port **8080**.

### 3. Swagger UI (local)

Once the application is running, access the interactive API documentation at:

> **http://localhost:8080/swagger-ui.html**

### 4. Default user

On first startup, an admin user is created automatically:
- **Email:** `useradmin@email.com`
- **Password:** `Admin@12345`

### 5. Test with curl

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"useradmin@email.com","password":"Admin@12345"}'

# Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@test.com","username":"testuser","password":"Test@1234","role":"USER"}'

# Health check
curl http://localhost:8080/actuator/health
```

---

## Tests

```bash
mvn clean verify
```

Runs unit and integration tests (H2 in-memory) and validates coverage >= 80% via JaCoCo.

---

## Environment Variables

### Application (runtime)

| Variable | Description | Default (local) |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5433/fiapx_auth` |
| `DB_USERNAME` | Database user | `fiapx` |
| `DB_PASSWORD` | Database password | `fiapx123` |
| `JWT_SECRET` | Secret key for signing JWT tokens (Base64) | embedded dev key |
| `JWT_EXPIRATION_MS` | Token expiration in milliseconds | `86400000` (24h) |

---

## CI/CD — GitHub Actions

The pipeline (`.github/workflows/ci.yml`) runs on push/PR to `main`:

1. **Build & Test** — compile, run tests, validate coverage
2. **SonarCloud** — code quality analysis (push to main only)
3. **Docker** — build and push image to GHCR
4. **Deploy** — apply to EKS cluster via `kubectl`

### Required GitHub Secrets (Settings → Secrets and variables → Actions)

| Secret | Description |
|---|---|
| `SONAR_TOKEN` | SonarCloud token for quality analysis |
| `AWS_ACCESS_KEY_ID` | AWS credential for EKS deploy |
| `AWS_SECRET_ACCESS_KEY` | AWS credential for EKS deploy |
| `AWS_SESSION_TOKEN` | AWS session token (if using temporary credentials) |
| `AUTH_DB_URL` | PostgreSQL JDBC URL on AWS (e.g. `jdbc:postgresql://host:5432/fiapx_auth`) |
| `AUTH_DB_USERNAME` | Database user on AWS |
| `AUTH_DB_PASSWORD` | Database password on AWS |
| `JWT_SECRET` | Production JWT secret key (Base64, min 32 bytes) |
| `NEW_RELIC_LICENSE_KEY` | New Relic license key for APM |

### Pipeline environment variables (already configured in workflow)

| Variable | Value |
|---|---|
| `AWS_REGION` | `us-east-1` |
| `EKS_CLUSTER` | `fiapx-cluster` |
| `SERVICE_NAME` | `auth-service` |
| `SERVICE_PORT` | `8080` |

---

## Docker

The image uses a **multi-stage build**:
- **Stage 1 (build):** Maven + JDK 21 — compiles the project
- **Stage 2 (runtime):** JRE 21 — lightweight final image with the JAR and New Relic agent

The image is published to GitHub Container Registry (GHCR):
```
ghcr.io/<org>/fiapx-auth-service:latest
```

---

## Kubernetes

Manifests in `k8s/`:
- `deployment.yaml` — 2 replicas, liveness/readiness probes, resource limits
- `service.yaml` — ClusterIP on port 8080

Kubernetes secrets are automatically created by the pipeline from GitHub Secrets.

---

## Acknowledgments

This project was developed with the assistance of [Claude](https://claude.com/claude-code) (Anthropic) as an AI pair-programming tool for code implementation, debugging, and documentation.
