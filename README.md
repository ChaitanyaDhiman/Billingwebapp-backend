# Billing Web App Backend

Spring Boot backend for managing a gaming center’s products, console sessions, orders, and billing. It exposes a REST API secured with JWT, persists data in PostgreSQL via Spring Data JPA, and pushes real-time notifications to connected clients over STOMP/WebSocket.

## Features
- **Role-based authentication** – Admin, prepaid, and postpaid user roles backed by Spring Security + JWT tokens (`/api/auth/signing`, `/api/auth/signup`).
- **Inventory & catalog** – CRUD for gaming systems and products plus stock adjustments (`/api/systems`, `/api/products`).
- **Session lifecycle** – Admins can start/stop console sessions, track durations, and automatically compute hourly charges.
- **Orders & billing** – Attach product orders to sessions/users, generate bills, accept payments, and enforce balance rules.
- **Real-time alerts** – STOMP endpoint `/ws` publishes low-balance and session notifications to topics such as `/topic/user/{id}/notifications`.
- **Operational guardrails** – Global exception handling, validation annotations, scheduled prepaid balance checks, and a `DataLoader` seeding default accounts.

## Technology Stack
- Java 17, Spring Boot 3.5
- Modules: Spring Web, Data JPA, Validation, Security, WebSocket/STOMP
- PostgreSQL driver, ModelMapper, JJWT, Lombok
- Build: Maven (`mvn` or `./mvnw`)

## Project Layout
```
src/main/java/com/gamingcenter/billingwebapp/
├─ controller/      # REST endpoints (auth, users, sessions, orders, bills, products, systems)
├─ service/         # Business logic, billing calculations, websocket notifications
├─ security/        # JWT utilities, filters, access checks (@securityService expressions)
├─ model/ & dto/    # Persistence entities and API-facing DTOs
├─ repository/      # Spring Data repositories
├─ config/          # WebSocket/STOMP configuration
└─ util/            # Startup data loader for default users
```

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+ (or use the included `./mvnw`)
- PostgreSQL 14+ running locally

### 1. Clone & install deps
```bash
git clone <repo>
cd Billingwebapp-backend
mvn clean install
```

### 2. Configure environment
Copy `src/main/resources/application.properties` or override via environment variables:

| Property | Description | Default |
| --- | --- | --- |
| `spring.datasource.url` | JDBC URL to PostgreSQL | `jdbc:postgresql://localhost:5432/gaming_center_db` |
| `spring.datasource.username` / `password` | DB credentials | `postgres` / `admin` |
| `spring.jpa.hibernate.ddl-auto` | Schema strategy | `update` |
| `app.jwt.secret` | Base64 secret for signing JWTs | sample value in repo |
| `app.jwt.expiration-ms` | Token lifetime | `86400000` (24h) |

> Tip: For local dev you can export variables, e.g. `SPRING_DATASOURCE_URL`, `APP_JWT_SECRET`, to avoid editing the tracked file.

### 3. Run database migrations
The project relies on JPA auto-DDL. Create the `gaming_center_db` database manually:
```bash
createdb gaming_center_db
```

### 4. Start the application
```bash
mvn spring-boot:run
# or
./mvnw spring-boot:run
```
The API listens on `http://localhost:8080`.

### 5. Seeded accounts
A `CommandLineRunner` seeds default users if missing:

| Username | Password | Role | Notes |
| --- | --- | --- | --- |
| `admin` | `admin` | ADMIN | Full access |
| `prepaid` | `password` | PREPAID | Starts with 100 balance |
| `postpaid` | `password` | POSTPAID | Starts with 100 balance |

Update or remove this behavior in `util/DataLoader.java` for production deployments.

## Running Tests
```bash
mvn test
```
Integration and unit tests live under `src/test/java`. Add coverage as you extend controllers/services.

## API Highlights

| Area | Endpoint(s) | Roles |
| --- | --- | --- |
| Auth | `POST /api/auth/signing`, `POST /api/auth/signup` | Public |
| Users | `GET /api/users`, `GET /api/users/{id}`, `POST /api/users/{id}/topup` | Admin / owner |
| Systems | `GET/POST/PUT/DELETE /api/systems`, `GET /api/systems/available` | Admin |
| Sessions | `POST /api/sessions/start`, `POST /api/sessions/{id}/end`, `GET /api/sessions/active` | Admin |
| Products | `GET /api/products`, `POST /api/products`, `PUT /api/products/{id}/stock` | Admin, users (read) |
| Orders | `POST /api/orders`, `GET /api/orders/{id}`, `/user/{userId}`, `/session/{sessionId}` | Admin / owner |
| Bills | `POST /api/bills/generate/{sessionId}`, `POST /api/bills/pay`, `GET /api/bills/{id}` | Admin / owner |

All protected endpoints require the `Authorization: Bearer <token>` header.

### WebSocket Notifications
- STOMP endpoint: `ws://localhost:8080/ws` (`SockJS` fallback enabled)
- Client destination prefix: `/app`
- Server topics: `/topic/**` (e.g., `/topic/user/{userId}/notifications`)
- Used by `SessionService.checkPrepaidSessionExpiry()` to notify prepaid users and auto-end depleted sessions.

## Common Commands
- `mvn spring-boot:run` – Start app with hot reload (devtools)
- `mvn clean verify` – Full build + tests
- `mvn dependency:tree` – Inspect dependencies

## Deployment Notes
- Set strong JWT secrets and disable `ddl-auto=update` in production.
- Frontend CORS origins are currently pinned to `http://localhost:3000`; adjust in controllers or configure a global CorsFilter.
- Consider replacing in-memory broker with a scalable message broker if you need durable WebSocket messaging, and move scheduled tasks to a dedicated worker for horizontal scalability.

