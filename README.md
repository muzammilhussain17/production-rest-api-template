# Production REST API Template

A robust, cloud-ready Spring Boot 3 REST API template designed for production use. It establishes consistent error handling, response formatting, and documentation, ensuring frictionless integration for frontend teams and simplified production debugging.

## Features

- **Resource-Oriented Design**: Proper HTTP semantics and structured RESTful endpoints.
- **Global Exception Handling**: Centralized `@RestControllerAdvice` emitting strict **RFC 7807** Problem Details.
- **Cursor-Based Pagination**: Optimized pagination for highly scalable dataset fetching without the deep-offset penalty.
- **Correlation IDs integrated**: Intercepter-injected `X-Correlation-ID` added to the MDC context for distributed tracing and response headers.
- **Rate Limiting**: Built-in IP-based rate limiting via **Bucket4j**.
- **Automated API Documentation**: **OpenAPI 3** specification generated directly from code annotations via Springdoc.
- **Security & Reliability**: 
  - Centralized request validation (`@Valid`, Hibernate Validator).
  - SQL injection prevention via Spring Data JPA.
  - Connection pooling (HikariCP) and stateless design.

## Architecture

```text
┌─────────────────────────────────────────────────────────────────┐
│                        API STRUCTURE                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   Request ──▶ Filter Chain ──▶ Controller ──▶ Service ──▶ Repo  │
│      │            │                │              │             │
│      │      ┌─────▼─────┐    ┌─────▼─────┐        │             │
│      │      │  Logging  │    │ Validation│        │             │
│      │      │  Auth     │    │ @Valid    │        │             │
│      │      │  RateLimit│    └───────────┘        │             │
│      │      └───────────┘                         │             │
│      │                                            │             │
│      │   ┌────────────────────────────────────────┘             │
│      │   │                                                      │
│      │   ▼                                                      │
│      │  Exception? ──▶ GlobalExceptionHandler ──▶ RFC 7807      │
│      │                                                          │
│      ▼                                                          │
│   Response: { data, pagination, _links }                        │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Component Flow (Mermaid)

```mermaid
graph TD
    Client((Client)) -->|Request| FilterChain[Filter Chain]
    
    subgraph Core Architecture
        FilterChain -->|X-Correlation-ID| Controller[Controller Layer]
        Controller -->|DTO Validation| Service[Service Layer]
        Service -->|Domain Logic| Repo[Repository Layer]
        
        FilterChain -.-> Filters[CorrelationIdFilter, RateLimitInterceptor]
        Controller -.-> Validation[Jakarta Annotations]
        
        Repo -->|JPA/Hibernate| DB[(PostgreSQL)]
        
        Controller -- Exception Thrown --> GlobalExceptionHandler
        Service -- Exception Thrown --> GlobalExceptionHandler
        Repo -- Exception Thrown --> GlobalExceptionHandler
        
        GlobalExceptionHandler[GlobalExceptionHandler] -.-> RFC7807[RFC 7807 Problem Detail]
    end
    
    Controller -- Success --> Response[JSON: { data, nextCursor, hasNext }]
    RFC7807 --> ProblemResponse[JSON Problem Detail]
    
    Response --> Client
    ProblemResponse --> Client
```

## Tech Stack
- **Java 21 / 17 LTS**
- **Spring Boot 3.4.x** (Web, Data JPA, Validation, Actuator)
- **PostgreSQL** (Production DB) & **H2** (In-Memory Test DB)
- **Springdoc OpenAPI** (Swagger UI)
- **Bucket4j** (Rate Limiting)
- **JUnit 5 & MockMvc** (Testing)

---

## Getting Started

### Prerequisites
- JDK 17 or 21 installed (`JAVA_HOME` configured).
- PostgreSQL database accessible (Default assumes `localhost:5432`).

### 1. Configure the Database
In `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:postgres}
    username: ${DB_USER:postgres}
    password: ${DB_PASS:postgres}
```

### 2. Build and Test
The project includes a generic embedded H2 configuration specifically for decoupled testing.
Run unit and integration tests using the Maven wrapper:
```bash
# Windows
.\mvnw.cmd clean test

# Linux/macOS
./mvnw clean test
```

### 3. Run Locally
Execute the Spring Boot Application locally:
```bash
.\mvnw.cmd spring-boot:run
```

You can optionally override database properties inline:
```bash
# Note: Syntax varies by OS (this is Bash syntax)
DB_HOST=127.0.0.1 DB_PASS=my_local_pw ./mvnw spring-boot:run
```

### 4. Explore the API
Once running (defaults to port `8080`), you can access the automatically generated Swagger interactive documentation at:
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

The sample `Item` resource operates under the endpoints `/api/v1/items`. Try hitting an endpoint with invalid validation data to witness the RFC 7807 response schema mapped alongside your requested Correlation ID.
