# `crm-api`

## Overview

This project implements a **RESTful backend API** for managing **clients** (individuals or companies) and their **contracts** in the context of an insurance company. It is designed according to the technical exercise specifications provided by Api Factory.

The system allows users (e.g. counselors) to:

* Create, read, update, and delete clients.
* Manage contracts associated with those clients.
* Query active contracts and compute total active contract costs efficiently.

The API adheres to **ISO 8601 date standards**, **JSON** format, and follows **RESTful conventions**.

---

## Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.5.6
* **Database:** MongoDB (data persistence and easy JSON mapping)
* **Build Tool:** Maven
* **Validation:** Jakarta Bean Validation (JSR 380)
* **Testing:** JUnit 5 + Spring Boot Test

---

##  Features

### Client Management

* Create a **Person** or **Company** client.
* Fields:

    * **Common:** name, email, phone
    * **Person:** birthDate
    * **Company:** companyIdentifier (e.g., `aaa-123`)
* Update all fields except immutable ones (`birthDate` and `companyIdentifier`).
* Delete a client → automatically closes their contracts (sets `endDate = now`).

### Contract Management

* Create a contract with:

    * `startDate` (defaults to now if not provided)
    * `endDate` (nullable)
    * `costAmount`
    * internal `updateDate` (auto-managed, not exposed)
* Update `costAmount` → automatically refreshes `updateDate`.
* Fetch all **active contracts** (where `now < endDate`).
* Filter contracts by `updateDate`.
* Compute total **sum of all active contracts’ costAmount** for a given client (optimized endpoint).

###  Non-Functional Highlights

* Persistent storage (MongoDB).
* Input validation for dates, phone numbers, emails, and numeric fields.
* Clean, descriptive naming over excessive comments.
* Unit and integration testing coverage.

---

## Architecture & Design

The project follows a **layered architecture** ensuring maintainability and scalability:

```
controller → service → repository → database
```

* **Controller:** Handles REST endpoints and validation.
* **Service:** Contains business logic (updates, filtering, date validation, cost sum computation).
* **Repository:** Spring Data MongoDB for persistence.
* **Model/DTOs:** Separate data objects for domain and API exposure.

**Rationale:**

* MongoDB fits naturally with JSON and evolving schemas.
* Layered design isolates business logic and promotes clean testing.
* Using `OffsetDateTime` for consistent ISO 8601 timestamps with UTC offsets.

---

## Setup & Run Instructions

### Prerequisites

* Java 17+
* Maven 3.9+
* Docker (optional, for MongoDB)

### Run Locally

#### 1️⃣ Start MongoDB

You can either run MongoDB locally or use Docker:

```bash
docker run -d --name mongo -p 27017:27017 mongo:latest
```

#### 2️⃣ Configure the Application

Edit `src/main/resources/application.yml` if needed:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/api_factory_db
```

#### 3️⃣ Build & Run

```bash
mvn clean spring-boot:run
```

The app will start at **[http://localhost:8080](http://localhost:8080)**.

#### 4️⃣ Run Tests

```bash
mvn test
```

---

## Example API Endpoints

### Create a Client

```bash
POST /api/clients
Content-Type: application/json

{
  "type": "PERSON",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+41790000000",
  "birthDate": "1990-05-20"
}
```

### Create a Contract

```bash
POST /api/clients/{clientId}/contracts
{
  "costAmount": 1200.50,
  "startDate": "2025-01-01T00:00:00Z"
}
```

### Get Active Contracts

```bash
GET /api/clients/{clientId}/contracts?active=true
```

### Total Active Contract Cost

```bash
GET /api/clients/{clientId}/contracts/total
```

---

## Proof of Correctness

* Validation ensures data integrity (email, phone, ISO date).
* Tests cover CRUD operations, contract updates, and total sum calculations.
* End-to-end tested with Postman collection and integration tests.

---

## Future Improvements

* Add pagination and sorting for client/contract listings.
* Implement caching for total-cost endpoint.
* Add OpenAPI/Swagger documentation.
* Add authentication & role-based access control.

---
