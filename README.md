# 🏢 Franchise Management API

**Prueba Técnica - Backend Webflux Developer**

API REST reactiva para la gestión de franquicias, sucursales y productos, desarrollada con Spring Boot WebFlux y arquitectura hexagonal.

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Arquitectura de Software](#-arquitectura-de-software)
- [Tecnologías](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Despliegue Local](#-instalación-y-despliegue-local)
- [API Endpoints](#-api-endpoints)
- [Operadores Reactivos Utilizados](#-operadores-reactivos-utilizados)
- [Testing y Cobertura](#-testing-y-cobertura)
- [Docker](#-docker)
- [Decisiones de Diseño](#-decisiones-de-diseño)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [GraphQl](#-GraphQl)
- [Autor](#-autor)

---

## 🎯 Descripción

Sistema de gestión de franquicias que permite:

- ✅ **Crear y gestionar franquicias** con múltiples sucursales
- ✅ **Administrar sucursales** asociadas a cada franquicia
- ✅ **Controlar inventario de productos** por sucursal
- ✅ **Modificar stock** de productos en tiempo real
- ✅ **Analítica de inventario** - productos con mayor stock por sucursal
- ✅ **Actualización de nombres** para sucursales y productos

**Modelo de Dominio:**
```
Franchise (Franquicia)
  └─ Branch (Sucursal)
      └─ Product (Producto)
          └─ Stock (Cantidad)
```

---

## 🏗️ Arquitectura de Software

### Arquitectura Hexagonal (Clean Architecture)

El proyecto implementa **Arquitectura Hexagonal** siguiendo los principios de Clean Architecture propuestos por Robert C. Martin, adaptados según las guías de [Bancolombia Scaffold Clean Architecture](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro/).

```
┌─────────────────────────────────────────────────┐
│           INFRASTRUCTURE LAYER                  │
│  ┌────────────────┐      ┌──────────────────┐  │
│  │  Entry Points  │      │ Driven Adapters  │  │
│  │   (REST API)   │      │   (MongoDB)      │  │
│  └────────┬───────┘      └────────┬─────────┘  │
│           │                       │             │
│           ↓                       ↓             │
│  ┌─────────────────────────────────────────┐   │
│  │         APPLICATION LAYER               │   │
│  │         (Use Cases)                     │   │
│  └──────────────────┬──────────────────────┘   │
│                     ↓                           │
│  ┌─────────────────────────────────────────┐   │
│  │          DOMAIN LAYER                   │   │
│  │   (Entities, Business Logic, Ports)     │   │
│  └─────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

#### Capas del Proyecto

**1. Domain Layer (Capa de Dominio)**
- `domain/model/`: Entidades de negocio (Franchise, Branch, Product)
- `domain/repository/`: Interfaces (Ports) para acceso a datos
- **Sin dependencias externas** - lógica de negocio pura

**2. Application Layer (Capa de Aplicación)**
- `application/usecase/`: Casos de uso del sistema
- Orquesta el flujo entre domain e infrastructure
- Implementa reglas de negocio complejas

**3. Infrastructure Layer (Capa de Infraestructura)**
- `infrastructure/entrypoints/`: Handlers REST (WebFlux)
- `infrastructure/drivenadapters/`: Implementaciones de repositorios (MongoDB)
- `infrastructure/config/`: Configuraciones de Spring

### Programación Reactiva

**Framework:** Spring WebFlux + Project Reactor

**Ventajas:**
- ✅ **Non-blocking I/O** - mejor escalabilidad
- ✅ **Backpressure** - manejo de flujos de datos
- ✅ **Eficiencia de recursos** - menos threads, más concurrencia
- ✅ **Compatibilidad nativa** con MongoDB Reactive Drivers

**Publishers Reactivos:**
- `Mono<T>`: 0 o 1 elemento
- `Flux<T>`: 0 a N elementos

---

## 🛠️ Tecnologías Utilizadas

### Core Technologies

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| **Java** | 17 LTS | Lenguaje base |
| **Spring Boot** | 3.2.x | Framework principal |
| **Spring WebFlux** | 3.2.x | API REST Reactiva |
| **MongoDB** | 7.x | Base de datos NoSQL |
| **MongoDB Atlas** | Cloud | Hosting de BD en la nube |
| **Project Reactor** | 3.6.x | Programación reactiva |
| **Lombok** | 1.18.x | Reducción de boilerplate |
| **Gradle** | 8.x | Gestión de dependencias |

### Testing & Quality

| Tecnología | Uso |
|-----------|-----|
| **JUnit 5** | Framework de testing |
| **Mockito** | Mocking de dependencias |
| **Reactor Test** | Testing de flujos reactivos |
| **JaCoCo** | Cobertura de código |

### DevOps & Deployment

| Tecnología | Uso |
|-----------|-----|
| **Git** | Control de versiones |
| **GitHub** | Repositorio remoto |

### Logging

| Framework | Configuración |
|-----------|--------------|
| **SLF4J** | Interfaz de logging |
| **Log4j2** | Implementación |

---

## 📦 Requisitos Previos

### Software Necesario

```bash
# Java 17 o superior
java -version
# Debería mostrar: openjdk version "17.0.x"

# Gradle 8.x (opcional - incluido en el proyecto)
./gradlew -v

# MongoDB Atlas - Cuenta gratuita
# https://www.mongodb.com/cloud/atlas/register
```

---

## 🚀 Instalación y Despliegue Local

### Opción 1: Ejecución Directa con Gradle

#### 1. Clonar el Repositorio

```bash
git clone https://github.com/danielpinto2407/franchise-management-api.git
cd franchise-management-api
```

#### 2. Configurar MongoDB Atlas

**a) Crear Cluster en MongoDB Atlas:**
1. Ir a [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Crear cuenta gratuita (Free Tier - 512 MB)
3. Crear un cluster (M0 Sandbox)
4. Crear usuario de base de datos
5. Configurar acceso desde cualquier IP (0.0.0.0/0)

**b) Obtener Connection String:**
```
mongodb+srv://<usuario>:<password>@cluster0.xxxxx.mongodb.net/franchises?retryWrites=true&w=majority
```

**c) Configurar en el Proyecto:**

Editar `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: franchise-management-api
  data:
    mongodb:
      uri: mongodb+srv://TU_USUARIO:TU_PASSWORD@cluster0.xxxxx.mongodb.net/franchises?retryWrites=true&w=majority

server:
  port: 8080

logging:
  level:
    root: INFO
    org.franchise.management: DEBUG
    org.springframework.data.mongodb: DEBUG
```

**O usar variable de entorno:**
```bash
export MONGODB_URI="mongodb+srv://usuario:password@cluster0.xxxxx.mongodb.net/franchises"
```

#### 3. Compilar el Proyecto

```bash
./gradlew clean build
```

#### 4. Ejecutar la Aplicación

```bash
./gradlew bootRun
```

La aplicación estará disponible en: **http://localhost:8080**

#### 5. Verificar que Funciona

```bash
# Test de conectividad
curl http://localhost:8080/actuator/health

# Crear una franquicia de prueba
curl -X POST http://localhost:8080/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s"}'
```

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080
```

### 1. Franquicias

#### ➕ Crear Franquicia
```http
POST /franchises
Content-Type: application/json

{
  "name": "McDonald's"
}
```

**Respuesta (200 OK):**
```json
{
  "id": "67123abc456def",
  "name": "McDonald's",
  "branchIds": [],
  "createdAt": "2024-10-18T22:30:45.123",
  "updatedAt": "2024-10-18T22:30:45.123"
}
```
---

### 2. Sucursales

#### ➕ Agregar Sucursal a Franquicia
```http
POST /franchises/{franchiseId}/branches
Content-Type: application/json

{
  "name": "Sucursal Centro"
}
```

**Respuesta (200 OK):**
```json
{
  "id": "67123abc456ghi",
  "name": "Sucursal Centro",
  "franchiseId": "67123abc456def",
  "productIds": [],
  "createdAt": "2024-10-18T22:31:00.456",
  "updatedAt": "2024-10-18T22:31:00.456"
}
```

#### ✏️ Actualizar Nombre de Sucursal (Plus)
```http
PUT /branches/{branchId}/name
Content-Type: application/json

{
  "name": "Sucursal Norte"
}
```

**Respuesta (200 OK):**
```json
{
  "id": "67123abc456ghi",
  "name": "Sucursal Norte",
  "franchiseId": "67123abc456def",
  "productIds": [],
  "createdAt": "2024-10-18T22:31:00.456",
  "updatedAt": "2024-10-18T22:31:00.456"
}
```

### 3. Productos

#### ➕ Agregar Producto a Sucursal
```http
POST /branches/{branchId}/products
Content-Type: application/json

{
  "name": "Coca Cola",
  "stock": 100
}
```

**Respuesta (200 OK):**
```json
{
  "id": "67123abc456jkl",
  "name": "Coca Cola",
  "stock": 100,
  "branchId": "67123abc456ghi",
  "createdAt": "2024-10-18T22:32:00.789",
  "updatedAt": "2024-10-18T22:32:00.789"
}
```

#### ❌ Eliminar Producto
```http
DELETE /franchises/products/{productId}
```

**Respuesta (204 No Content)**

#### 🔄 Modificar Stock de Producto
```http
PUT /products/{productId}/stock
Content-Type: application/json

{
  "stock": 150
}
```
**Respuesta (200 OK):**
```json
{
  "id": "67123abc456jkl",
  "name": "Coca Cola",
  "stock": 100,
  "branchId": "67123abc456ghi",
  "createdAt": "2024-10-18T22:32:00.789",
  "updatedAt": "2024-10-18T22:32:00.789"
}
```
#### ✏️ Actualizar Nombre de Producto (Plus)
```http
PUT /products/{productId}/name
Content-Type: application/json

{
  "name": "Coca Cola 2L"
}
```
**Respuesta (200 OK):**
```json
{
  "id": "67123abc456jkl",
  "name": "Coca Cola",
  "stock": 100,
  "branchId": "67123abc456ghi",
  "createdAt": "2024-10-18T22:32:00.789",
  "updatedAt": "2024-10-18T22:32:00.789"
}
```

### 4. Analítica

#### 📊 Obtener Productos con Mayor Stock por Sucursal
```http
GET /franchises/{franchiseId}/products/max-stock
```

**Respuesta (200 OK):**
```json
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

**Descripción:** Retorna el producto con mayor stock de cada sucursal de la franquicia especificada.

---

### Validaciones de la API

La API valida automáticamente:
- ✅ Campos obligatorios (`@NotBlank`, `@NotNull`)
- ✅ Stock no negativo (`@Min(0)`)
- ✅ Existencia de entidades padres (franchise, branch)

**Ejemplo de error de validación:**
```json
{
  "error": "El nombre de la franquicia es obligatorio"
}
```

---

## ⚛️ Operadores Reactivos Utilizados

El proyecto hace uso extensivo de operadores de Project Reactor:

### Operadores Implementados

| Operador | Uso en el Proyecto | Ubicación |
|----------|-------------------|-----------|
| **map** | Transformar DTOs → Entidades | Handlers |
| **flatMap** | Operaciones asíncronas encadenadas | Use Cases, Adapters |
| **flatMapMany** | Mono → Flux (queries múltiples) | ProductAdapter |
| **switchIfEmpty** | Valores por defecto cuando no hay datos | Todos los Use Cases |
| **doOnNext** | Logging de operaciones exitosas | Use Cases |
| **doOnError** | Logging de errores | Use Cases |
| **doOnComplete** | Logging de finalización | Adapters |
| **onErrorResume** | Manejo de errores y recuperación | Handlers, Use Cases |
| **collectList** | Flux → Mono<List> | Agregaciones |
| **filter** | Filtrado de elementos | Validaciones |

### Ejemplo de Encadenamiento Reactivo

```java
public Mono<Product> addProduct(String franchiseId, String branchId, Product product) {
    return request.bodyToMono(ProductRequestDTO.class)
            .flatMap(dto -> validationUtil.validate(dto))      // Validación
            .map(DTOMapper::toProduct)                          // Transformación
            .flatMap(p -> repository.addProduct(p))             // Persistencia
            .doOnNext(p -> log.info("✅ Producto creado"))     // Logging
            .switchIfEmpty(Mono.error(new NotFoundException())) // Error si vacío
            .onErrorResume(e -> handleError(e));                // Manejo de errores
}
```

### Señales Reactivas

**onNext:** Emisión de elementos
```java
.doOnNext(product -> log.info("Producto: {}", product.getName()))
```

**onError:** Manejo de errores
```java
.onErrorResume(e -> {
    log.error("Error: {}", e.getMessage());
    return Mono.error(e);
})
```

**onComplete:** Finalización del flujo
```java
.doOnComplete(() -> log.info("✅ Operación completada"))
```

---

## 🧪 Testing y Cobertura

### Estrategia de Testing

**Pirámide de Testing implementada:**
```
        /\
       /E2E\      (Pocos - Opcionales)
      /------\
     /  Int.  \   (Algunos - Adapters)
    /----------\
   /   Unit     \ (Mayoría - Domain + Use Cases)
  /--------------\
```

### Ejecutar Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte de cobertura
open build/reports/jacoco/test/html/index.html
```

### Cobertura Alcanzada

**Objetivo:** > 60% (Deseable: 80%)  
**Alcanzado:** 89% ✅

### Tipos de Tests Implementados

**1. Tests Unitarios (Unit Tests)**
```java
@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseTest {
    
    @Mock
    private FranchiseRepository repository;
    
    @InjectMocks
    private CreateFranchiseUseCase useCase;
    
    @Test
    void shouldCreateFranchise() {
        when(repository.save(any())).thenReturn(Mono.just(franchise));
        
        StepVerifier.create(useCase.execute(franchise))
            .expectNext(franchise)
            .verifyComplete();
    }
}
```

**2. Tests Reactivos (Reactive Tests)**
```java
StepVerifier.create(flux)
    .expectNext(element1)
    .expectNext(element2)
    .verifyComplete();
```

**3. Tests de Validación**
```java
@Test
void shouldValidateRequiredFields() {
    FranchiseRequestDTO emptyDTO = new FranchiseRequestDTO();
    
    StepVerifier.create(validationUtil.validate(emptyDTO))
        .expectError(IllegalArgumentException.class)
        .verify();
}
```

### Herramientas de Testing

- **JUnit 5:** Framework de testing
- **Mockito:** Mocking de dependencias
- **StepVerifier:** Testing de flujos reactivos
- **JaCoCo:** Medición de cobertura

---

## 💡 Decisiones de Diseño

### 1. ¿Por qué MongoDB?

**Ventajas:**
- ✅ **Esquema flexible:** Evolución del modelo sin migraciones
- ✅ **Drivers reactivos nativos:** Spring Data MongoDB Reactive
- ✅ **Modelo de documentos:** Se ajusta naturalmente a la jerarquía Franchise → Branch → Product
- ✅ **MongoDB Atlas:** Despliegue gratuito en la nube
- ✅ **Escalabilidad horizontal:** Sharding nativo

**Alternativa considerada:** PostgreSQL con R2DBC (descartada por complejidad de configuración)

### 2. ¿Por qué Referencias por ID vs Documentos Embebidos?

**Decisión:** Usar **referencias por ID**

```java
// ✅ Lo que usamos
public class Franchise {
    private List<String> branchIds;  // Referencias
}

// ❌ Alternativa descartada
public class Franchise {
    private List<Branch> branches;  // Embebido
}
```

**Razones:**
- ✅ **Más flexible** para operaciones CRUD independientes
- ✅ **Evita documentos grandes** (límite de MongoDB: 16MB)
- ✅ **Mejor para operaciones reactivas** (queries paralelas)
- ✅ **Escalable** para jerarquías profundas

### 3. ¿Por qué Arquitectura Hexagonal?

**Ventajas:**
- ✅ **Separación de concerns:** Dominio independiente de frameworks
- ✅ **Testeable:** Mockear fácilmente las dependencias
- ✅ **Mantenible:** Cambios en infrastructure no afectan domain
- ✅ **Migrable:** Cambiar de MongoDB a otra BD sin tocar domain

**Regla de dependencia:** 
```
Infrastructure → Application → Domain
(nunca al revés)
```

### 4. ¿Dónde Validar la Existencia de Entidades Padres?

**Decisión:** Validar en el **Adapter** (Repository Implementation)

**Razón:**
- ✅ El adapter conoce la estructura de datos
- ✅ Mantiene Use Cases simples
- ✅ `switchIfEmpty()` maneja casos de no existencia
- ✅ Consistente con el patrón reactivo

```java
return mongoTemplate.exists(query, "franchises")
    .flatMap(exists -> {
        if (!exists) return Mono.empty();
        // ... continuar operación
    });
```

### 5. ¿Por qué DTOs Separados?

**Decisión:** Usar DTOs para request

**Ventajas:**
- ✅ **Validación explícita** con Jakarta Validation
- ✅ **Separación API ↔ Domain:** Cambios en API no afectan dominio
- ✅ **Seguridad:** No exponer campos internos
- ✅ **Versionamiento:** Facilita múltiples versiones de API

### 6. Timestamps Automáticos

**Decisión:** Spring Data MongoDB Auditing

```java
@EnableReactiveMongoAuditing
@CreatedDate
@LastModifiedDate
```

**Ventajas:**
- ✅ Automático, no requiere código manual
- ✅ Consistente en todas las entidades
- ✅ Preparado para auditoría futura (@CreatedBy, @LastModifiedBy)

### 7. Logging Strategy

**Decisión:** SLF4J + Log4j2 con niveles apropiados

```java
log.info("✅ Operación exitosa")  // Producción
log.debug("🔍 Detalle técnico")   // Desarrollo
log.error("❌ Error crítico")     // Siempre
```

**Niveles por ambiente:**
- **Desarrollo:** DEBUG
- **Producción:** INFO
- **Errores:** ERROR (siempre)

---

## 📁 Estructura del Proyecto

```
franchise-management-api/
├── src/
│   ├── main/
│   │   ├── java/org/franchise/management/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Franchise.java
│   │   │   │   │   ├── Branch.java
│   │   │   │   │   └── Product.java
│   │   │   │   └── repository/
│   │   │   │       ├── FranchiseRepository.java
│   │   │   │       ├── BranchRepository.java
│   │   │   │       └── ProductRepository.java
│   │   │   ├── application/
│   │   │   │   └── usecase/
│   │   │   │       ├── CreateFranchiseUseCase.java
│   │   │   │       ├── AddBranchToFranchiseUseCase.java
│   │   │   │       ├── AddProductToBranchUseCase.java
│   │   │   │       ├── DeleteProductUseCase.java
│   │   │   │       ├── UpdateProductStockUseCase.java
│   │   │   │       └── GetMaxStockProductsUseCase.java
│   │   │   │       └── UpdateProductNameUseCase.java
│   │   │   │       └── UpdateBranchNameUseCase.java
│   │   │   ├── infrastructure/
│   │   │   │   ├── config/
│   │   │   │   │   ├── MongoConfig.java
│   │   │   │   │   └── ValidationConfig.java
│   │   │   │   ├── entrypoints/
│   │   │   │   │   └── webflux/
│   │   │   │   │       ├── handler/
│   │   │   │   │       │   ├── FranchiseHandler.java
│   │   │   │   │       │   ├── BranchHandler.java
│   │   │   │   │       │   └── ProductHandler.java
│   │   │   │   │       ├── router/
│   │   │   │   │       │   ├── FranchiseRouter.java
│   │   │   │   │       │   ├── BranchRouter.java
│   │   │   │   │       │   └── ProductRouter.java
│   │   │   │   │       ├── dto/
│   │   │   │   │       │   ├── FranchiseRequestDTO.java
│   │   │   │   │       │   ├── BranchRequestDTO.java
│   │   │   │   │       │   └── ProductRequestDTO.java
│   │   │   │   │       │   └── DTOMapper.java
│   │   │   │   │       │   └── UpdateNameRequestDTO.java
│   │   │   │   │       │   └── UpdateStockRequestDTO.java
│   │   │   │   │       └── util/
│   │   │   │   │           ├── ValidationUtil.java
│   │   │   │   │           └── ResponseUtil.java
│   │   │   │   └── drivenadapters/
│   │   │   │       └── mongo/
│   │   │   │           ├── adapters/
│   │   │   │           │   ├── FranchiseMongoAdapter.java
│   │   │   │           │   ├── BranchMongoAdapter.java
│   │   │   │           │   └── ProductMongoAdapter.java
│   │   │   │           └── repository/
│   │   │   │               ├── FranchiseMongoRepository.java
│   │   │   │               ├── BranchMongoRepository.java
│   │   │   │               └── ProductMongoRepository.java
│   │   │   └── FranchiseApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/org/franchise/management/
│           ├── domain/model/
│           ├── application/usecase/
│           └── infrastructure/entrypoints/webflux/handler/
├── build.gradle
├── settings.gradle
├── .gitignore
└── README.md
```

---

## 👤 Autor

**Daniel Pinto**
- GitHub: [@danielpinto2407](https://github.com/danielpinto2407)
- Email: [Tu Email]
- LinkedIn: [Tu LinkedIn]

---

## 📄 Licencia

Este proyecto es una prueba técnica para SETI S.A.S.

---

## 🔗 Referencias

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor](https://projectreactor.io/)
- [MongoDB Reactive](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.reactive)
- [Hexagonal Architecture](https://alistair.cockburn.us/hexagonal-architecture/)
- [Bancolombia Scaffold](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro/)

---

## ✅ Checklist de Cumplimiento de Requisitos

### Arquitectura de Software
- [x] Spring Boot + WebFlux
- [x] Arquitectura Hexagonal
- [x] Librerías reactivas compatibles
- [x] MongoDB en la nube (Atlas)
- [x] Operadores reactivos (map, flatMap, switchIfEmpty, etc.)
- [x] Señales onNext, onError, onComplete
- [x] Logging con SLF4J/Log4j2
- [x] Pruebas unitarias > 60% (alcanzado 89%)
- [x] README.md completo
- [x] APIs RESTful correctas

### Funcional
- [x] Agregar franquicia
- [x] Agregar sucursal
- [x] Agregar producto
- [x] Eliminar producto
- [x] Modificar stock
- [x] Mostrar producto con mayor stock por sucursal

### Puntos Extra
- [x] Actualizar nombre de sucursal
- [x] Actualizar nombre de producto
- [x] Despliegue en la nube (MongoDB Atlas)
- [x] Decisiones de diseño en README

### Notas Importantes
- [x] Repositorio público en GitHub
- [x] Documentación de despliegue local
- [x] Flujo de trabajo con Git (commits organizados)

---
### GraphQl

Además de la API REST, el proyecto ahora incluye **un endpoint GraphQL unificado** para manejar operaciones sobre franquicias, sucursales y productos desde un único punto.

### 📌 Endpoint

```
POST /graphql
Content-Type: application/json
```

### 🧬 Esquema GraphQL

```graphql
type Franchise {
  id: ID!
  name: String!
  branches: [Branch]
}

type Branch {
  id: ID!
  name: String!
  franchiseId: ID!
  products: [Product]
}

type Product {
  id: ID!
  name: String!
  stock: Int!
  branchId: ID!
}

type Query {
  franchises: [Franchise]
  franchiseById(id: ID!): Franchise
  branchesByFranchise(franchiseId: ID!): [Branch]
  productsByBranch(branchId: ID!): [Product]
}

type Mutation {
  createFranchise(name: String!): Franchise
  createBranch(franchiseId: ID!, name: String!): Branch
  createProduct(branchId: ID!, name: String!, stock: Int!): Product
  updateBranchName(branchId: ID!, name: String!): Branch
  updateProductName(productId: ID!, name: String!): Product
  updateProductStock(productId: ID!, stock: Int!): Product
  deleteProduct(productId: ID!): Boolean
}
```

---

## 🧪 Ejemplo: Mutation para Crear una Sucursal (Branch)

### 📥 Payload GraphQL

```json
{
  "query": "mutation ($inputFranchiseId: ID!, $inputName: String!) { createBranch(franchiseId: $inputFranchiseId, name: $inputName) { id name franchiseId } }",
  "variables": {
    "inputFranchiseId": "6735e80f94ed7a7c8f1a93c2",
    "inputName": "Sucursal Centro"
  }
}
```

### 📤 Respuesta

```json
{
  "data": {
    "createBranch": {
      "id": "6735e8d994ed7a7c8f1a93c6",
      "name": "Sucursal Centro",
      "franchiseId": "6735e80f94ed7a7c8f1a93c2"
    }
  }
}
```

---

## 📌 Secciones Actualizadas

Se ha integrado GraphQL en las siguientes partes del README:

* **Descripción general del proyecto**
* **Lista de tecnologías utilizadas** (incluye `spring-graphql`)
* **Nuevos ejemplos de queries y mutations**
* **Compatibilidad paralela con REST + GraphQL**

---

## 🛠️ Tecnologías Utilizadas (Actualizado)

| Tecnología           | Versión | Uso               |
| -------------------- | ------- | ----------------- |
| **Spring WebFlux**   | 3.2.x   | API REST Reactiva |
| **Spring GraphQL**   | 1.3.x   | Servidor GraphQL  |
| **MongoDB Reactive** | 7.x     | Acceso a datos    |

---

## 📡 Operaciones GraphQL Disponibles

### 🏷️ Query: Obtener Franquicias

```graphql
query {
  franchises {
    id
    name
    branches { id name }
  }
}
```

### 🏷️ Query: Productos por Sucursal

```graphql
query Products($branchId: ID!) {
  productsByBranch(branchId: $branchId) {
    id
    name
    stock
  }
}
```

### 🛠️ Mutation: Actualizar Stock

```graphql
mutation UpdateStock($id: ID!, $qty: Int!) {
  updateProductStock(productId: $id, stock: $qty) {
    id
    name
    stock
  }
}
```

---

## 🧭 Filosofía de la Integración REST + GraphQL

**REST** se mantiene para:

* Uso estándar de microservicios
* Compatibilidad con API Gateway
* Simplicidad para clientes móviles

**GraphQL** se añade para:

* Frontends complejos (React, Angular)
* Reducción de múltiples llamadas REST
* Consultas fuertemente tipadas
* Retorno de datos exactos (sin overfetching)

Ambos conviven en el proyecto.