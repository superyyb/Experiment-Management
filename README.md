## Experiment Records Management Platform

🚀 Full-stack experiment management platform handling 50,000+ records with Redis caching (83% latency reduction), RBAC security, and Dockerized deployment.
👉 Built with Spring Boot + MyBatis + React

### Highlights

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

### Caching (Redis)

- Cached record details (TTL 1h) and search results (TTL 30m)
- Write-through invalidation on mutations (create/update/delete)
- Canonicalized + hashed keys to prevent hot-key issues

### Security

- JWT authentication + Spring Security RBAC
- Protected endpoints (e.g., `/experiments/search`) require valid tokens

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

### Testing

- Integration tests using JUnit 5 + Testcontainers (MySQL 8 + Redis 7)
- Covers DAO queries, caching behavior, and RBAC endpoints

```bash
cd backend
./gradlew test
```

Note: tests require a local **Docker** daemon (same as CI on GitHub Actions).

