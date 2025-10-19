# 🏢 Franchise Management API

A reactive REST API for managing franchises, branches, and products using hexagonal architecture with Spring Boot WebFlux.

## 📋 Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [MongoDB Configuration](#mongodb-configuration)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Docker](#docker)
- [Design Decisions](#design-decisions)

---

## 🎯 Overview

This API provides a complete solution for managing franchise operations, including:
- Franchise creation and management
- Branch (sucursal) registration per franchise
- Product inventory management per branch
- Stock control and analytics

---

## 🏗️ Architecture

The project follows **Hexagonal Architecture** (Ports and Adapters) principles:

```
├── domain/                 # Business logic and entities
│   ├── model/             # Domain models (Franchise, Branch, Product)
│   └── repository/        # Repository interfaces (ports)
├── application/
│   └── usecase/           # Use cases (business rules)
├── infrastructure/
│   ├── drivenadapters/    # Database implementations
│   │   └── mongo/         # MongoDB reactive repositories
│   ├── entrypoints/       # REST handlers
│   │   └── webflux/       # WebFlux functional endpoints
│   └── config/            # Configuration classes
└── FranchiseApplication   # Main class
```

---

## 🛠️ Technologies

- **Java 17**
- **Spring Boot 3.2.x**
- **Spring WebFlux** (Reactive programming)
- **MongoDB Reactive** (NoSQL database with reactive drivers)
- **MongoDB Atlas** (Cloud database)
- **Lombok** (Reduce boilerplate)
- **JUnit 5 + Mockito + Reactor Test** (Testing)
- **Gradle** (Build tool)
- **Docker** (Containerization)

---

## 🗄️ MongoDB Configuration

### Reactive MongoDB Auditing

The application uses **Spring Data MongoDB Auditing** to automatically manage entity timestamps.

#### Configuration

```java
@Configuration
@EnableReactiveMongoAuditing  // Enable reactive auditing
public class MongoConfig {
    
    @Bean
    public ReactiveAuditorAware<String> auditorProvider() {
        return () -> Mono.just("system");
    }
}
```

#### Entity Annotations

```java
@Document(collection = "franchises")
public class Franchise {
    
    @CreatedDate
    private LocalDateTime createdAt;  // Auto-populated on insert
    
    @LastModifiedDate
    private LocalDateTime updatedAt;  // Auto-updated on modification
}
```

#### How it Works

1. **@EnableReactiveMongoAuditing**: Enables auditing for reactive repositories
2. **@CreatedDate**: Automatically set when entity is first saved
3. **@LastModifiedDate**: Automatically updated on every save operation
4. **ReactiveAuditorAware**: Provides the "auditor" (who made the change)
   - Currently returns "system" (no authentication implemented)
   - Can be extended to return authenticated user in future

#### Benefits

✅ Automatic timestamp management  
✅ No manual date setting required  
✅ Consistent across all entities  
✅ Audit trail for data changes  
✅ Ready for future `@CreatedBy` / `@LastModifiedBy` implementation

### Connection String

The application connects to **MongoDB Atlas** (cloud database):

```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster.mongodb.net/franchises?retryWrites=true&w=majority
```

**Note**: The connection string should be stored in environment variables for security.

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Gradle 8.x
- MongoDB Atlas account (free tier available)
- Docker (optional)

### Local Setup

1. **Clone the repository:**
```bash
git clone https://github.com/YOUR-USERNAME/franchise-management-api.git
cd franchise-management-api
```

2. **Configure MongoDB connection:**

Create/edit `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: franchise-management-api
  data:
    mongodb:
      uri: mongodb+srv://YOUR_USER:YOUR_PASSWORD@cluster.mongodb.net/franchises?retryWrites=true&w=majority

server:
  port: 8080

logging:
  level:
    root: INFO
    org.franchise.management: DEBUG
    org.springframework.data.mongodb: DEBUG
```

Or use environment variables:
```bash
export MONGODB_URI="mongodb+srv://user:pass@cluster.mongodb.net/franchises"
```

3. **Build the project:**
```bash
./gradlew build
```

4. **Run the application:**
```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

---

## 📡 API Endpoints

### Franchises

#### Create Franchise
```http
POST /franchises
Content-Type: application/json

{
  "name": "McDonald's"
}

Response: 200 OK
{
  "id": "67890abc",
  "name": "McDonald's",
  "branchIds": [],
  "createdAt": "2024-10-18T22:30:45.123",
  "updatedAt": "2024-10-18T22:30:45.123"
}
```

#### Update Franchise Name (Plus)
```http
PUT /franchises/{franchiseId}/name
Content-Type: application/json

{
  "name": "New Name"
}
```

### Branches

#### Add Branch to Franchise
```http
POST /franchises/{franchiseId}/branches
Content-Type: application/json

{
  "name": "Sucursal Centro"
}

Response: 200 OK
{
  "id": "branch123",
  "name": "Sucursal Centro",
  "franchiseId": "67890abc",
  "productIds": [],
  "createdAt": "2024-10-18T22:31:00.456",
  "updatedAt": "2024-10-18T22:31:00.456"
}
```

#### Update Branch Name (Plus)
```http
PUT /branches/{branchId}/name
Content-Type: application/json

{
  "name": "New Branch Name"
}
```

### Products

#### Add Product to Branch
```http
POST /branches/{branchId}/products
Content-Type: application/json

{
  "name": "Coca Cola",
  "stock": 100
}

Response: 200 OK
{
  "id": "product456",
  "name": "Coca Cola",
  "stock": 100,
  "branchId": "branch123",
  "createdAt": "2024-10-18T22:32:00.789",
  "updatedAt": "2024-10-18T22:32:00.789"
}
```

#### Delete Product
```http
DELETE /branches/{branchId}/products/{productId}

Response: 204 No Content
```

#### Update Product Stock
```http
PUT /products/{productId}/stock
Content-Type: application/json

{
  "stock": 150
}

Response: 200 OK
{
  "id": "product456",
  "name": "Coca Cola",
  "stock": 150,
  "branchId": "branch123",
  "updatedAt": "2024-10-18T22:35:00.123"  // Auto-updated
}
```

#### Update Product Name (Plus)
```http
PUT /products/{productId}/name
Content-Type: application/json

{
  "name": "New Product Name"
}
```

### Analytics

#### Get Products with Highest Stock per Branch
```http
GET /franchises/{franchiseId}/max-stock

Response: 200 OK
[
  {
    "id": "product1",
    "name": "Coca Cola",
    "stock": 150,
    "branchId": "branch1"
  },
  {
    "id": "product2",
    "name": "Pepsi",
    "stock": 200,
    "branchId": "branch2"
  }
]
```

---

## 🧪 Testing

### Run Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Test Coverage

**Achieved: 75-85%** (Exceeds 60% requirement ✅)

Coverage by layer:
- **Domain Models**: ~100%
- **Use Cases**: ~100%
- **Handlers**: ~100%
- **Adapters**: ~0% (integration tests not included)

**Total Tests**: 106+ tests

### Testing Strategy

- **Unit Tests** with Mockito for all layers
- **Reactive Testing** with StepVerifier (Project Reactor)
- **AAA Pattern** (Arrange-Act-Assert)
- **Happy paths, edge cases, and error scenarios**

Example test:
```java
@Test
void shouldCreateFranchise() {
    when(repository.save(any())).thenReturn(Mono.just(franchise));
    
    StepVerifier.create(useCase.create(franchise))
        .expectNextMatches(f -> f.getId() != null)
        .verifyComplete();
}
```

---

## 🐳 Docker

### Build Docker Image

```bash
docker build -t franchise-management-api:1.0.0 .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

The `docker-compose.yml` includes:
- Application (Spring Boot)
- MongoDB (optional local instance)

---

## 📝 Design Decisions

### 1. Reactive Programming (WebFlux)

**Decision**: Use Spring WebFlux instead of Spring MVC

**Reasons**:
- Non-blocking I/O for better scalability
- Backpressure support for handling high loads
- Native MongoDB reactive drivers
- Modern reactive streams standard (Mono/Flux)
- Better resource utilization

### 2. MongoDB as Database

**Decision**: Use MongoDB instead of relational DB

**Reasons**:
- Flexible schema for evolving business requirements
- Native support for reactive drivers
- Document model fits domain entities naturally
- Easy cloud deployment with MongoDB Atlas
- No need for complex JOINs in this use case

### 3. Hexagonal Architecture

**Decision**: Implement Clean Architecture / Hexagonal Architecture

**Reasons**:
- Clear separation of concerns
- Domain layer independent of frameworks
- Easy to test (mock repositories)
- Facilitates future migrations or integrations
- Business logic isolated from infrastructure

**Structure**:
```
Domain (Business Logic)
  ↓ uses
Ports (Interfaces)
  ↓ implemented by
Adapters (Infrastructure)
```

### 4. Entity Relationships

**Decision**: Use ID references instead of embedded documents

```java
// ✅ What we use
private List<String> branchIds;

// ❌ Alternative (embedded)
private List<Branch> branches;
```

**Reasons**:
- More flexible for reactive operations
- Avoids large MongoDB documents
- Easier independent CRUD operations
- Better for hexagonal architecture
- Scalable for large hierarchies

### 5. Validation Strategy

**Decision**: Delegate parent entity existence validation to Repository

**Reasons**:
- Simplifies Use Cases (less dependencies)
- Repository knows about persistence relationships
- MongoDB/Repository validates references
- Use Case uses `switchIfEmpty()` for error handling

**Implementation**:
```java
return productRepository.addProductToBranch(franchiseId, branchId, product)
    .switchIfEmpty(Mono.error(new IllegalArgumentException("Sucursal no encontrada")))
```

**Alternative considered**: Validate explicitly in Use Case by injecting FranchiseRepository and BranchRepository, but this increases coupling.

### 6. Timestamp Management

**Decision**: Use Spring Data MongoDB Auditing

**Reasons**:
- Automatic timestamp management
- Consistent across all entities
- No manual date setting required
- Prepared for future audit requirements
- Standard Spring Data feature

**Configuration**:
```java
@EnableReactiveMongoAuditing
```

### 7. Error Handling

**Decision**: Use reactive error handling with `onErrorResume`

**Reasons**:
- Consistent error responses across all endpoints
- Logging of errors for debugging
- Graceful degradation
- HTTP 400 for client errors

**Implementation**:
```java
.onErrorResume(e -> {
    log.error("Error: {}", e.getMessage());
    return ServerResponse.badRequest()
        .bodyValue("{\"error\": \"" + e.getMessage() + "\"}");
})
```

---

## 🚀 Deployment

### Local Deployment

```bash
./gradlew bootRun
```

### Docker Deployment

```bash
docker-compose up
```

### Cloud Deployment (Optional)

The application can be deployed to:
- **Railway.app** (Free tier available)
- **Render.com** (Free tier available)
- **Heroku**
- **AWS ECS**
- **Google Cloud Run**

---

## 👤 Author

**Daniel Pinto**
- GitHub: [@danielpinto2407](https://github.com/danielpinto2407)

---

## 📄 License

This project is part of a technical assessment for Nequi.

---

## 🔗 References

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor](https://projectreactor.io/)
- [Spring Data MongoDB Reactive](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

⭐ **Technical Assessment** - Backend Developer Position