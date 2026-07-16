
## Smart Slotting Warehouse Management System

An Enterprise Warehouse Management System designed based on enterprise-grade operational standards such as multi-dimensional storage location management, inventory tracking by SKU/Lot, and the optimization of inbound and outbound workflows. 

## HOW TO RUN

### 1. Prerequisites
- [JDK 21](https://jdk.java.net/21/)
- [Docker & Docker Compose](https://www.docker.com/) (Used to run PostgreSQL)
- Gradle (Provided via the `gradlew` wrapper)

### 2. Start the Database
Use Docker to quickly start PostgreSQL ```bash
docker-compose up -d

---

## Tech Stack

- **Backend:** Java, Spring Boot 3, Spring Data JPA, Hibernate.
- **Database:** PostgreSQL 18.
- **Database Migration:** Flyway (Strict version control of DB structure).
- **API Documentation:** SpringDoc OpenAPI (Swagger UI).
- **Tooling:** Gradle, Lombok, Docker (For DB containers).

## 🚀 Core Modules

The system is organized into distinct functional modules (Logical Domains):

- **[Master Data]**: Centralized management of Product catalogs, SKUs, Units of Measure, and warehouse spatial structures (Warehouse/Zone/Location).
- **[Inventory]**: Real-time inventory tracking and logging of stock movement history.
- **[Inbound]**: Management of inbound workflows (PO, GRN) and putaway strategies.
- **[Outbound]**: Management of outbound workflows (SO) and picking strategies (FIFO/FEFO).

---

## 🏗️ Architecture & Design Documentation

Details regarding system design, trade-offs, and business workflows:

* 📂 `01-architecture`: Overall architectural decisions.
* 📂 `02-database`: ERD diagrams and database design patterns.
* 📂 `03-business`: Warehouse business processes (Inbound/Outbound workflows).
* 📂 `04-api`: API design standards and error handling.

---


