# Distributed Database Synchronization with RabbitMQ

## Assignment Overview

This project is a distributed application (BO1 + BO2 + HO) meant to synchronize product sales data between **two Branch Offices (BO)** and one **Head Office (HO)**. The context is a network with limited internet connectivity (only 1–2 hours per day). The design uses **RabbitMQ** as a reliable message broker so data can be queued while the HO is temporarily offline.

### Business Scenario

- Two physically separated sales branches (BO1, BO2) manage their own PostgreSQL databases.
- Each branch maintains a `product_sales` table with daily sales records.
- The Head Office must consolidate all sales data from both branches into a central database.
- Synchronization is **initiated by each branch** (push model) using RabbitMQ queues.
- Messages are persisted in RabbitMQ so no data is lost if the HO is unreachable.

### Technical Constraints (from the assignment)

- Use **Java** with JDBC (here we use Spring Boot + Spring Data JPA).
- Use **RabbitMQ** for asynchronous messaging.
- Use **PostgreSQL** as the database (original spec allowed MySQL, but we chose PostgreSQL).
- Run two independent producer processes (one per BO) and one consumer process (HO).

## Repository Layout

- **`ho-backend/`**: Spring Boot application (HO consumer + API placeholder)
- **`bo-producer-1/`**: Spring Boot application (BO1 producer placeholder)
- **`bo-producer-2/`**: Spring Boot application (BO2 producer placeholder)
- **`docker-compose.yml`**: Local infrastructure (PostgreSQL + RabbitMQ + Adminer)

## Current Status

The repository currently provides:

- Dockerized infrastructure for **PostgreSQL**, **RabbitMQ**, and **Adminer**.
- Three Spring Boot modules with configuration already wired for:
  - PostgreSQL connection (via `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)
  - RabbitMQ connection (via `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`)
  - Messaging parameters (exchange / routing-key / queue)

What is **not** implemented yet (still to be added for the full assignment):

- BO side: `product_sales` entity/repository + a scheduler to publish unsynced rows
- HO side: queue consumer + consolidated sales entity + deduplication logic
- REST API + dashboard for viewing consolidated sales

## Prerequisites

- Java 21
- Maven 3.9+ (or use each module's `./mvnw`)
- Docker + Docker Compose (recommended for PostgreSQL/RabbitMQ/Adminer)

## Setup and Run

### 1) Environment Files

This repo uses `.env` files for configuration:

- **Root `.env`**: used by `docker-compose.yml` (PostgreSQL/RabbitMQ/Adminer settings)
- **Module `.env`** (inside each module folder): used by Spring Boot at runtime

Defaults already exist in the repo, but you can regenerate them from the examples:

```bash
cp .env.example .env
cp bo-producer-1/.env.example bo-producer-1/.env
cp bo-producer-2/.env.example bo-producer-2/.env
cp ho-backend/.env.example ho-backend/.env
```

Minimal values to review:

- Root `.env`: `POSTGRES_USER`, `POSTGRES_PASSWORD`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- Module `.env`: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `RABBITMQ_HOST`, `RABBITMQ_PORT`

### 2) Start Infrastructure (PostgreSQL + RabbitMQ + Adminer)

Start services using Docker Compose:

```bash
docker compose up -d postgres rabbitmq adminer
```

Useful URLs/ports (defaults):

- RabbitMQ Management UI: http://localhost:15672 (default `guest/guest`)
- Adminer UI: http://localhost:8085
- PostgreSQL: localhost:5432

### 3) Create Databases

Create the 3 databases used by the modules (`bo1_db`, `bo2_db`, `ho_db`).

If you use Docker Compose PostgreSQL, you can run:

```bash
docker exec -it postgres psql -U postgres -c "CREATE DATABASE bo1_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE bo2_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE ho_db;"
```

If you changed the PostgreSQL user in the root `.env`, replace `postgres` accordingly.

Or create them from Adminer.

### 4) Run the Applications (3 terminals)

Note: choose **one** run mode:

- **Mode A (recommended for infra only):** run `postgres`/`rabbitmq`/`adminer` with Docker Compose, then run the 3 Spring Boot apps locally with `./mvnw spring-boot:run`.
- **Mode B (all-in-Docker):** run the apps using `docker compose up -d --build`.

If you already started the `ho-backend` container (Mode B), port `8080` is already taken. Either stop the container:

```bash
docker compose stop ho-backend
```

…or change the local port by editing `ho-backend/.env` (`SERVER_PORT`) before running.

Start HO backend:

```bash
cd ho-backend
./mvnw spring-boot:run
```

Start BO producer 1:

```bash
cd bo-producer-1
./mvnw spring-boot:run
```

Start BO producer 2:

```bash
cd bo-producer-2
./mvnw spring-boot:run
```

Default ports:

- HO backend: `8080`
- BO producer 1: `8081`
- BO producer 2: `8082`

### 5) Verify Services Are Up

- Check logs in each terminal for successful Spring Boot startup.

Since the producer/consumer logic and API are not implemented yet, validation at this stage is mainly:

- Spring Boot starts without errors
- Each service connects to PostgreSQL and RabbitMQ using its `.env`
- RabbitMQ UI is reachable and shows the declared exchange/queue once you implement the messaging configuration

## Notes

- If you want to run the apps inside Docker as well, `docker-compose.yml` already defines `ho-backend`, `bo-producer-1`, and `bo-producer-2` services. Make sure the environment variables provided to those containers match what the Spring Boot `application.properties` expects (the modules read `DB_URL/DB_USERNAME/DB_PASSWORD` from `.env`).
