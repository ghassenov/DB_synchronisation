# Distributed Database Synchronization with RabbitMQ

## Assignment Overview

This project implements a distributed application for synchronizing product sales data between **two Branch Offices (BO)** and one **Head Office (HO)**. The context is a network with limited internet connectivity (only 1–2 hours per day). The solution uses **RabbitMQ** as a reliable message broker to ensure data consistency even when the HO is temporarily offline.

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

### What We Built

- **`ho-backend`** : Spring Boot application that:
  - Consumes sales messages from a RabbitMQ queue.
  - Stores consolidated sales in `ho_db` with deduplication (`(bo_id, local_sale_id)` unique constraint).
  - Exposes a REST API (`/api/sales`) and a simple frontend dashboard to view all sales.
- **`bo-producer-1`** and **`bo-producer-2`** : Two identical Spring Boot applications (different configs) that:
  - Periodically scan their local `product_sales` table for unsynchronized rows (`synced = false`).
  - Send each new sale as a JSON message to a RabbitMQ exchange.
  - Mark the row as `synced = true` after successful publication.
- **RabbitMQ** acts as the message broker with a durable queue and persistent messages.


## Prerequisites

- Java 21
- Maven 3.9+ (or use each module's `./mvnw`)
- PostgreSQL 14+
- RabbitMQ 3.12+

## Setup and Run

### 1) Prepare Environment Files

For each module, copy `.env.example` to `.env` and edit values:

```bash
cp bo-producer-1/.env.example bo-producer-1/.env
cp bo-producer-2/.env.example bo-producer-2/.env
cp ho-backend/.env.example ho-backend/.env
```

Update at least:

- `DB_PASSWORD`
- `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` (if not using default guest/guest)
- Any host/port overrides for your machine

### 2) Start PostgreSQL and RabbitMQ

You can use local services or Docker.

Example RabbitMQ with Docker:

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```

RabbitMQ Management UI: `http://localhost:15672`.

### 3) Create Databases

Create the 3 databases used by the modules:

```sql
CREATE DATABASE bo1_db;
CREATE DATABASE bo2_db;
CREATE DATABASE ho_db;
```

### 4) Run the Applications (3 terminals)

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
- If HO API is implemented, verify endpoints under: `http://localhost:8080/api/sales`
- Use RabbitMQ UI to check queues/exchanges and message flow.

## Notes for the Assignment Deliverable

- The infrastructure, module split, and configuration are in place.
- The next implementation milestone is adding:
  - BO-side `product_sales` entity/repository + scheduler for unsynced rows
  - RabbitMQ publisher with persistent messages
  - HO-side message consumer + consolidated sales entity with unique `(bo_id, local_sale_id)`
  - REST API and dashboard integration


