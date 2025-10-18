# ☕ API de Gestión de Franquicias (Prueba Técnica)

Este proyecto implementa una **aplicación reactiva con Spring Boot** siguiendo los principios de **Arquitectura Limpia** inspirada en la [guía de arquitectura limpia de Bancolombia](https://bancolombia.github.io/scaffold-clean-architecture/docs/intro/).

Permite gestionar **franquicias**, **sucursales** y **productos**, utilizando **Spring WebFlux**, **MongoDB Atlas** y **Reactor**.

---

## 🧩 Estructura del Proyecto

```
src
 ├── domain
 │    ├── model                # Entidades de negocio principales (Franchise, Branch, Product)
 │    └── repository           # Puertos del dominio (interfaces)
 │
 ├── application
 │    └── usecase              # Casos de uso de aplicación que orquestan la lógica
 │
 ├── infrastructure
 │    ├── config               # Configuración técnica (MongoDB, beans, etc.)
 │    └── drivenadapters
 │         └── mongo           # Adaptadores de persistencia implementando los repositorios del dominio
 │
 └── entrypoints
      └── webflux              # Handlers y Routers (Endpoints funcionales)
           ├── handler
           └── router
```
---

## 🧱 Descripción de las Capas

- **Domain:** Lógica pura del negocio y contratos.  
  Define las entidades (`Franchise`, `Branch`, `Product`) y los **repositorios** (interfaces) que expresan qué necesita el dominio sin preocuparse de cómo se implementa.

- **Application:** Coordinación de casos de uso y flujos principales.  
  Contiene la lógica de orquestación entre el dominio y las infraestructuras, aplicando reglas de negocio concretas.

- **Infrastructure:** Implementaciones concretas (MongoDB).  
  Alberga los **adaptadores de salida**, las configuraciones técnicas y la conexión con proveedores externos como bases de datos.

- **Entrypoints:** Capa de exposición Web (API Reactiva).  
  Expone los **endpoints reactivos** a través de `handlers` y `routers`, conectando el mundo externo con la lógica interna.

---

## ⚙️ Tecnologías Utilizadas

| Capa | Tecnología |
|------|-------------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3 (Reactivo) |
| Web | Spring WebFlux (Functional Endpoints) |
| Base de datos | MongoDB Atlas |
| Logs | Log4j2 |
| Testing | JUnit 5 + Mockito + StepVerifier |
| Build Tool | Gradle |

---

## 🚀 Ejecución del Proyecto

### 1. Prerrequisitos
- Java 17+
- Gradle 8+
- Cadena de conexión a MongoDB Atlas configurada en `application.yml` o `.env`

Ejemplo:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://<usuario>:<contraseña>@cluster.mongodb.net/franchise-db
```

### 2. Ejecutar la aplicación

```bash
./gradlew bootRun
```

El servidor se ejecutará en:  
```
http://localhost:8080
```

---

## 🧠 Casos de Uso Implementados

### ✅ Gestión de Franquicias
1. **Crear una nueva franquicia**
   - `POST /franchises`
   - Body: `{ "name": "Coffee Time" }`

### ✅ Gestión de Sucursales
2. **Agregar una nueva sucursal a una franquicia**
   - `POST /franchises/{franchiseId}/branches`
   - Body: `{ "name": "Bogotá Norte" }`

### ✅ Gestión de Productos
3. **Agregar un nuevo producto a una sucursal**
   - `POST /franchises/{franchiseId}/branches/{branchId}/products`
   - Body: `{ "name": "Café Premium", "stock": 50 }`

4. **Eliminar un producto de una sucursal**
   - `DELETE /franchises/{franchiseId}/branches/{branchId}/products/{productId}`

5. **Actualizar el stock de un producto**
   - `PUT /franchises/{franchiseId}/branches/{branchId}/products/{productId}`
   - Body: `{ "stock": 80 }`

6. **Obtener el producto con mayor stock por sucursal de una franquicia**
   - `GET /franchises/{franchiseId}/products/max-stock`

---

## 📊 Pruebas

Para ejecutar las pruebas unitarias:
```bash
./gradlew test
```

- Cobertura actual: **64%**
- Pruebas incluidas:
  - Flujos exitosos y con error en handlers
  - Validaciones de cuerpo vacío y excepciones
  - Uso de `StepVerifier` para pruebas reactivas

---

## 🧾 Ejemplos de Entidades JSON

### Franchise
```json
{
  "id": "123",
  "name": "Coffee House",
  "branchIds": ["b001", "b002"],
  "createdAt": "2025-10-17T12:00:00",
  "updatedAt": "2025-10-17T12:10:00"
}
```

### Branch
```json
{
  "id": "b001",
  "name": "Bogotá Norte",
  "franchiseId": "123",
  "productIds": ["p001"],
  "createdAt": "2025-10-17T12:05:00",
  "updatedAt": "2025-10-17T12:10:00"
}
```

### Product
```json
{
  "id": "p001",
  "name": "Café Premium 500g",
  "stock": 50,
  "branchId": "b001",
  "createdAt": "2025-10-17T12:00:00",
  "updatedAt": "2025-10-17T12:10:00"
}
```

---

## 🧠 Logging

Cada operación registra información mediante **Log4j2**, por ejemplo:
```
✅ Nueva franquicia creada: Coffee House
❌ Error al agregar sucursal: Franquicia no encontrada
```

---

## 🧪 Próximos pasos
- Aumentar la cobertura de pruebas al 80%+  
---

## 👤 Autor
**Wilson Daniel Pinto Ríos**  
Desarrollador Backend - Clean Architecture & Reactive Spring  
📧 Contacto: [wdpinto@utp.edu.co]
