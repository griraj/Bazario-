# Bazario API – Sprint 1
## User Management & Product Catalog

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.11) |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| Documentation | SpringDoc OpenAPI 3 / Swagger UI |
| Testing | JUnit 5, Mockito, Spring Boot Test |
| Build | Maven 3 |

---

## User Stories Implemented

| ID | Story | Status |
|---|---|---|
| US-01 | Customer registration | ✅ |
| US-02 | Secure login (JWT) | ✅ |
| US-03 | Vendor storefront registration | ✅ |
| US-04 | Vendor adds product listing | ✅ |
| US-05 | Browse products by category | ✅ |
| US-06 | Search products by name | ✅ |
| US-07 | Admin views all vendors | ✅ |
| US-08 | Vendor edits product details | ✅ |

---

## Running Locally

### Prerequisites
- Docker & Docker Compose
- Java 17+, Maven 3.8+ (for building without Docker)

### With Docker Compose
```bash
docker compose up --build
```
The API will be available at `http://localhost:8080/api/v1`.

### Without Docker
```bash
# Start a local PostgreSQL instance (or update application.properties)
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

---

## API Documentation

Swagger UI is available at:
```
http://localhost:8080/api/v1/swagger-ui.html
```
OpenAPI JSON spec:
```
http://localhost:8080/api/v1/docs
```

---

## Endpoints Quick Reference

### Authentication
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | US-01: Register customer account |
| POST | `/auth/login` | Public | US-02: Login and receive JWT |

### Vendors
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/vendors/storefront` | VENDOR | US-03: Register storefront |
| GET | `/vendors?page=0&size=20` | ADMIN | US-07: List all vendors |

### Products
| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/products` | VENDOR | US-04: Create product listing |
| GET | `/products/categories/{id}` | Public | US-05: Browse by category |
| GET | `/products/search?q=laptop` | Public | US-06: Search by name |
| GET | `/products/{id}` | Public | Get single product |
| PATCH | `/products/{id}` | VENDOR | US-08: Edit product details |

### Categories
| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/categories` | Public | List all categories |

---

## Security

- All endpoints except registration, login, and public product browsing require a valid JWT in the `Authorization: Bearer <token>` header.
- Role-based access control is enforced via `@PreAuthorize` annotations using Spring Security method security.
- Passwords are hashed with BCrypt (strength 12).
- JWT secret **must** be overridden via the `BAZARIO_JWT_SECRET` environment variable in production.

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `BAZARIO_DB_URL` | `jdbc:postgresql://localhost:5432/bazario` | JDBC URL |
| `BAZARIO_DB_USER` | `bazario` | DB username |
| `BAZARIO_DB_PASS` | `bazario` | DB password |
| `BAZARIO_JWT_SECRET` | *(insecure default)* | **Change in production** |
| `BAZARIO_JWT_EXPIRY_MS` | `86400000` (24 h) | Token TTL |

---

## Project Structure

```
src/
├── main/java/com/bazario/
│   ├── BazarioApplication.java
│   ├── config/          # Security & OpenAPI config
│   ├── controller/      # REST controllers
│   ├── dto/
│   │   ├── request/     # Validated inbound DTOs
│   │   └── response/    # Outbound DTOs
│   ├── entity/          # JPA entities & enums
│   ├── exception/       # Domain exceptions & global handler
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT service & filter
│   └── service/         # Business logic (interface + impl)
├── main/resources/
│   ├── application.properties
│   └── db/migration/    # Flyway SQL migrations
└── test/java/com/bazario/
    ├── controller/      # MockMvc integration tests
    ├── security/        # JWT unit tests
    └── service/         # Service layer unit tests
```
