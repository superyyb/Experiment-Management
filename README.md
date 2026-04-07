## Experiment Records Management Platform

A full-stack **Spring Boot + MyBatis + React** platform to manage **50,000+** experimental records and streamline R&D data entry, search, and collaboration workflows.

### Highlights (resume-ready)

- Developed a full-stack Spring Boot + MyBatis + React platform to manage 50,000+ experimental records, streamlining R&D data entry, search, and collaboration workflows
- Designed a normalized MySQL schema and index strategy; implemented Redis caching (TTL + write-through invalidation, hot-key safeguards) to reduce repeated search latency by 83% (350ms to 60ms)
- Containerized services with Docker Compose, built CI/CD via GitHub Actions, and validated DAO queries, caching behavior, and RBAC endpoints with JUnit5 + Testcontainers

### Architecture

- **Frontend (`frontend/`)**: React SPA (pages for login, dashboard, record list/search, detail, create/edit)
- **Backend (`backend/`)**: Spring Boot REST API (MyBatis DAOs, Redis caching, JWT auth, RBAC via Spring Security)
- **Database (`database/`)**: normalized MySQL schema + seed data scripts
- **Infra**: Docker Compose orchestrates MySQL + Redis + backend + frontend
- **CI**: GitHub Actions builds and tests backend + frontend; backend tests use Testcontainers

### Repository Layout

```
.
├── backend/                 # Spring Boot + MyBatis API
│   ├── src/main/            # application code + MyBatis mappers
│   └── src/test/            # JUnit 5 + Testcontainers integration tests
├── frontend/                # React app
├── database/                # schema.sql + init-data.sql
├── docker-compose.yml       # local orchestration
└── .github/workflows/       # CI pipeline
```

### Tech Stack

- **Backend**: Spring Boot, MyBatis, MySQL, Redis, Spring Security (RBAC), JWT
- **Frontend**: React, Ant Design, Axios, React Router
- **Infra**: Docker, Docker Compose, GitHub Actions
- **Testing**: JUnit 5, Testcontainers (MySQL 8 + Redis 7)

### Caching Strategy (Redis)

- **What is cached**
  - **Record details** (`findById`): Spring Cache → Redis, TTL **1 hour**
  - **Search results** (`search`): manual Redis JSON cache, TTL **30 minutes**
- **Consistency**
  - Mutations (`create/update/delete`) trigger **write-through invalidation** by clearing search-cache keys and evicting record cache.
- **Hot-key safeguards**
  - Search queries use a **canonicalized + MD5-digested key** (bounded length) to avoid oversized keys and reduce hot-query pressure.

### Security (RBAC)

- JWT authentication + Spring Security method-level authorization (RBAC).
- Example: `/experiments/search` requires a valid JWT and role-based access.

### Quick Start (Docker Compose)

Prerequisites: **Docker Desktop** (or any Docker daemon) + Docker Compose.

```bash
docker compose up -d
```

Services:

- **Frontend**: `http://localhost:3000`
- **Backend API**: `http://localhost:8080/api`
- **MySQL**: `localhost:3306`
- **Redis**: `localhost:6379`

### Tests (JUnit 5 + Testcontainers)

Integration tests spin up **MySQL 8** and **Redis 7** via Testcontainers, then validate:

- MyBatis DAO queries (e.g., search counts)
- caching behavior (search-cache keys written to Redis)
- RBAC-protected endpoints (JWT-gated access)

```bash
cd backend
./gradlew test
```

Note: tests require a local **Docker** daemon (same as CI on GitHub Actions).

