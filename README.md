# Distributed Sales Synchronization (BO1, BO2, HO)

This project contains three Spring Boot applications:

1. BO1 server (`bo-producer-1`)
2. BO2 server (`bo-producer-2`)
3. HO backend (`ho-backend`)

Each BO app is now a standalone web server with:

- local database CRUD API (`/api/sales`)
- local frontend at `/`
- direct RabbitMQ publish on each CRUD operation

The HO app provides:

- RabbitMQ consumer that applies BO changes to `consolidated_sales`
- consolidated database CRUD API (`/api/sales`)
- consolidated frontend at `/`

## How Synchronization Works

1. User inserts/updates/deletes a sale in BO1 or BO2 UI.
2. BO writes the local change to `product_sales`.
3. BO immediately publishes a RabbitMQ event (`UPSERT` or `DELETE`).
4. HO consumes the message:
- `UPSERT`: create or update row in `consolidated_sales`
- `DELETE`: remove row from `consolidated_sales`

This means create, update, and delete operations from BO are synced to HO.

## Prerequisites

- Java 21+
- Docker + Docker Compose
- Optional: `psql` CLI (or use Adminer)

## Environment Setup

If needed, regenerate `.env` files:

```bash
cp .env.example .env
cp bo-producer-1/.env.example bo-producer-1/.env
cp bo-producer-2/.env.example bo-producer-2/.env
cp ho-backend/.env.example ho-backend/.env
```

Minimum values to set:

- root `.env`: `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`
- module `.env`: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `RABBITMQ_HOST`, `RABBITMQ_PORT`

Default module ports:

- HO: `8080`
- BO1: `8081`
- BO2: `8082`

## Start Infrastructure

From repository root:

```bash
docker compose up -d postgres rabbitmq adminer
```

Useful endpoints:

- RabbitMQ Management: http://localhost:15672
- Adminer: http://localhost:8085
- PostgreSQL: localhost:5432

## Create Databases

Create BO/HO databases once:

```bash
docker exec -it postgres psql -U postgres -c "CREATE DATABASE bo1_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE bo2_db;"
docker exec -it postgres psql -U postgres -c "CREATE DATABASE ho_db;"
```

If databases already exist, PostgreSQL will report that and you can continue.

## Run All Applications

Open 3 terminals.

Terminal 1:

```bash
cd ho-backend
./mvnw spring-boot:run
```

Terminal 2:

```bash
cd bo-producer-1
./mvnw spring-boot:run
```

Terminal 3:

```bash
cd bo-producer-2
./mvnw spring-boot:run
```

## Frontends and APIs

- BO1 UI: http://localhost:8081
- BO2 UI: http://localhost:8082
- HO UI: http://localhost:8080

All services expose CRUD API at `/api/sales`.

BO API examples:

- `GET /api/sales`
- `POST /api/sales`
- `PUT /api/sales/{id}`
- `DELETE /api/sales/{id}`

HO API examples:

- `GET /api/sales`
- `POST /api/sales`
- `PUT /api/sales/{id}`
- `DELETE /api/sales/{id}`

## Quick End-to-End Check

1. Open BO1 UI and create a row.
2. Open HO UI and confirm the row appears.
3. Update the same BO1 row and confirm HO updates.
4. Delete the BO1 row and confirm HO deletes it.

Repeat from BO2 to validate both producers.

## Build Validation

All three modules were validated with:

```bash
./mvnw -q -DskipTests package
```

inside each module directory.
